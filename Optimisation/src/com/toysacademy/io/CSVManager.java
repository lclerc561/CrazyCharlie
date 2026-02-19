package com.toysacademy.io;

import com.toysacademy.model.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Gestionnaire d'entrée/sortie CSV.
 * Lecture des fichiers séparés (articles.csv, abonnes.csv) et écriture des
 * solutions.
 */
public class CSVManager {

    // =========================================================================
    // Lecture de fichiers séparés (format hérité)
    // =========================================================================

    /** Lit les articles depuis un fichier CSV séparé. */
    public List<Article> lireArticles(String cheminFichier) throws IOException {
        List<Article> articles = new ArrayList<>();
        List<String> lignes = Files.readAllLines(Paths.get(cheminFichier));

        for (String ligne : lignes) {
            ligne = nettoyerLigne(ligne);
            if (ligne.isEmpty())
                continue;

            String[] parties = ligne.split(";");
            if (parties.length < 5)
                continue;

            try {
                String id = parties[0].trim();
                if (id.equalsIgnoreCase("id") || id.toLowerCase().startsWith("ident"))
                    continue; // Ignorer l'en-tête

                Category categorie = Category.fromString(parties[1]);
                Age age = Age.fromString(parties[2]);
                double poids = Double.parseDouble(parties[3].replace(",", ".").trim());
                State etat = State.fromString(parties[4]);

                articles.add(new Article(id, categorie, age, poids, etat));
            } catch (Exception e) {
                System.err.println("Ligne article ignorée : " + ligne + " -> " + e.getMessage());
            }
        }
        return articles;
    }

    /** Lit les abonnés depuis un fichier CSV séparé. */
    public List<Abonne> lireAbonnes(String cheminFichier) throws IOException {
        List<Abonne> abonnes = new ArrayList<>();
        List<String> lignes = Files.readAllLines(Paths.get(cheminFichier));

        for (String ligne : lignes) {
            ligne = nettoyerLigne(ligne);
            if (ligne.isEmpty())
                continue;

            String[] parties = ligne.split(";");
            if (parties.length < 3)
                continue;

            try {
                String id = parties[0].trim();
                if (id.equalsIgnoreCase("id") || id.toLowerCase().startsWith("ident"))
                    continue;

                Age trancheAge = Age.fromString(parties[1]);
                List<Category> preferences = new ArrayList<>();
                for (int i = 2; i < parties.length; i++) {
                    String pref = parties[i].trim();
                    if (!pref.isEmpty()) {
                        preferences.add(Category.fromString(pref));
                    }
                }

                abonnes.add(new Abonne(id, trancheAge, preferences));
            } catch (Exception e) {
                System.err.println("Ligne abonné ignorée : " + ligne + " -> " + e.getMessage());
            }
        }
        return abonnes;
    }

    // Alias de compatibilité
    public List<Article> readArticles(String f) throws IOException {
        return lireArticles(f);
    }

    public List<Abonne> readAbonnes(String f) throws IOException {
        return lireAbonnes(f);
    }

    // =========================================================================
    // Écriture des solutions
    // =========================================================================

    /**
     * Génère le contenu CSV de la solution.
     * Première ligne = score, puis une ligne par article affecté.
     */
    public String genererContenuCsv(Composition composition, double scoreGlobal) {
        StringBuilder sb = new StringBuilder();
        sb.append((int) scoreGlobal).append(System.lineSeparator());

        for (Box box : composition.getBoxes()) {
            String prenom = box.getAbonne().getPrenom();
            for (Article article : box.getArticles()) {
                sb.append(prenom).append("; ")
                        .append(article.getIdentifiant()).append("; ")
                        .append(article.getCategorie()).append("; ")
                        .append(article.getTrancheAge()).append("; ")
                        .append(article.getEtat())
                        .append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    /** Sauvegarde la solution dans un fichier CSV. */
    public void sauvegarderSolution(String cheminFichier, Composition composition,
            double scoreGlobal) throws IOException {
        String contenu = genererContenuCsv(composition, scoreGlobal);
        Path chemin = Paths.get(cheminFichier);
        // Créer les répertoires parents si nécessaire
        if (chemin.getParent() != null) {
            Files.createDirectories(chemin.getParent());
        }
        try (PrintWriter ecrivain = new PrintWriter(
                Files.newBufferedWriter(chemin))) {
            ecrivain.print(contenu);
        }
    }

    // Alias de compatibilité
    public String generateCsvContent(Composition c, double s) {
        return genererContenuCsv(c, s);
    }

    public void writeSolution(String f, Composition c, double s) throws IOException {
        sauvegarderSolution(f, c, s);
    }

    /** Nettoie une ligne : supprime espaces et points-virgules en fin de ligne. */
    private String nettoyerLigne(String ligne) {
        if (ligne == null)
            return "";
        ligne = ligne.trim();
        while (ligne.endsWith(";")) {
            ligne = ligne.substring(0, ligne.length() - 1).trim();
        }
        return ligne;
    }
}
