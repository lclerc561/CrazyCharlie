package com.toysacademy;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import com.toysacademy.io.AnalyseurFichier;
import com.toysacademy.io.AnalyseurFichier.DonneesProbleme;
import com.toysacademy.io.CSVManager;
import com.toysacademy.model.Composition;
import com.toysacademy.service.EvaluateurScoreGlouton;
import com.toysacademy.service.ModeleGlouton;
import com.toysacademy.service.Solveur;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.UUID;

public class ServerHTTP {

    // Vous pouvez en ajouter d'autres plus tard (Recuit, etc.)
    private static final Solveur[] MODELES = {
            new ModeleGlouton()
    };

    public static void main(String[] args) throws Exception {
        int port = (args.length > 0) ? Integer.parseInt(args[0]) : 8080;

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Endpoint principal
        server.createContext("/solve", ServerHTTP::handleSolve);

        // Healthcheck
        server.createContext("/health", ex -> {
            addCors(ex);
            byte[] resp = "OK".getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
            ex.sendResponseHeaders(200, resp.length);
            try (OutputStream os = ex.getResponseBody()) {
                os.write(resp);
            }
        });

        server.setExecutor(null);
        server.start();

        System.out.println("ToyBoxing HTTP server démarré :");
        System.out.println("  GET  http://localhost:" + port + "/health");
        System.out.println("  POST http://localhost:" + port + "/solve   (body = CSV d'entrée brut)");
    }

    private static void handleSolve(HttpExchange exchange) throws IOException {
        // CORS + OPTIONS preflight
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            addCors(exchange);
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            addCors(exchange);
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        addCors(exchange);

        // Lire le body (CSV brut)
        String inputCsv;
        try (InputStream is = exchange.getRequestBody()) {
            inputCsv = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        if (inputCsv == null || inputCsv.isBlank()) {
            sendText(exchange, 400, "Body CSV vide.");
            return;
        }

        // Votre AnalyseurFichier lit un chemin => on écrit un fichier temporaire
        Path tmpDir = Files.createTempDirectory("toyboxing_");
        Path tmpFile = tmpDir.resolve("input_" + UUID.randomUUID() + ".csv");
        Files.writeString(tmpFile, inputCsv, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        try {
            // 1) Parse
            AnalyseurFichier analyseur = new AnalyseurFichier();
            DonneesProbleme donnees = analyseur.analyser(tmpFile.toString());

            // 2) Solve (comme Lanceur)
            Composition meilleureSolution = null;
            double meilleurScore = Double.NEGATIVE_INFINITY;

            for (Solveur modele : MODELES) {
                Composition solution = modele.resoudre(
                        donnees.abonnes,
                        donnees.articles,
                        donnees.poidsMax,
                        donnees.prixMin,
                        donnees.prixMax
                );

                EvaluateurScoreGlouton eval = new EvaluateurScoreGlouton();
                double score = eval.evaluer(solution, donnees.poidsMax);

                if (score > meilleurScore) {
                    meilleurScore = score;
                    meilleureSolution = solution;
                }
            }

            if (meilleureSolution == null) {
                sendText(exchange, 500, "Aucune solution produite.");
                return;
            }

            // 3) Export CSV solution -> STRING
            CSVManager csvManager = new CSVManager();
            String outputCsv = csvManager.solutionToCsvString(meilleureSolution, meilleurScore);

            // 4) Réponse CSV (le site lit la réponse et la traite)
            Headers h = exchange.getResponseHeaders();
            h.set("Content-Type", "text/csv; charset=utf-8");
            // Optionnel: inline = le navigateur n'essaie pas forcément de télécharger
            h.set("Content-Disposition", "inline; filename=\"solution.csv\"");

            byte[] resp = outputCsv.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, resp.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(resp);
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, 500, "Erreur traitement: " + e.getMessage());
        } finally {
            // Nettoyage fichiers temporaires
            try { Files.deleteIfExists(tmpFile); } catch (Exception ignored) {}
            try { Files.deleteIfExists(tmpDir); } catch (Exception ignored) {}
        }
    }

    private static void addCors(HttpExchange ex) {
        Headers h = ex.getResponseHeaders();
        // En prod, remplacez "*" par votre domaine exact (ex: https://monsite.com)
        h.set("Access-Control-Allow-Origin", "*");
        h.set("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        h.set("Access-Control-Allow-Headers", "Content-Type");
        h.set("Access-Control-Max-Age", "86400");
    }

    private static void sendText(HttpExchange ex, int code, String msg) throws IOException {
        addCors(ex);
        byte[] resp = msg.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        ex.sendResponseHeaders(code, resp.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(resp);
        }
    }

}
