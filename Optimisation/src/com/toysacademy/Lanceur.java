package com.toysacademy;

import com.toysacademy.io.AnalyseurFichier;
import com.toysacademy.io.AnalyseurFichier.DonneesProbleme;
import com.toysacademy.io.CSVManager;
import com.toysacademy.model.*;
import com.toysacademy.service.*;

// unused import removed

/**
 * Lanceur de benchmarking multi-modèles.
 *
 * Exécute tous les modèles d'optimisation sur les données d'entrée,
 * compare les scores et sauvegarde les solutions en CSV.
 *
 * Utilisation :
 * java com.toysacademy.Lanceur [fichier_donnees]
 * (par défaut : data/pb5.csv)
 */
public class Lanceur {

    // Liste des modèles disponibles
    private static final Algo[] MODELES = {
            new ModeleGlouton(),
            new ModeleGloutonDescente(),
            new ModeleRecuitSimule(),
            new ModeleBacktracking(),
            new ModeleBinPacking(),
            new ModeleGenetique()
    };

    public static void main(String[] args) {
        String cheminFichier = args.length > 0 ? args[0] : "./data/pb5.csv";

        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║     ToyBoxing — Benchmarking Multi-Modèles          ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();

        try {
            // --- Chargement des données ---
            AnalyseurFichier analyseur = new AnalyseurFichier();
            DonneesProbleme donnees = analyseur.analyser(cheminFichier);

            System.out.println("Fichier       : " + cheminFichier);
            System.out.println("Articles      : " + donnees.articles.size());
            System.out.println("Abonnés       : " + donnees.abonnes.size());
            System.out.println("Poids max/box : " + (int) donnees.poidsMax + "g");

            // Afficher les infos multi-enfants
            long multiEnfants = donnees.abonnes.stream()
                    .filter(Abonne::isMultiEnfants).count();
            System.out.println("Multi-enfants : " + multiEnfants + "/" + donnees.abonnes.size());

            System.out.println();

            // --- Exécution des modèles ---
            CSVManager csvManager = new CSVManager();

            // Stocker les résultats pour le tableau comparatif
            String[] noms = new String[MODELES.length];
            double[] scores = new double[MODELES.length];
            double[] temps = new double[MODELES.length];
            int[] articlesAffectes = new int[MODELES.length];
            Composition meilleureSolution = null;
            double meilleurScore = Double.NEGATIVE_INFINITY;
            String meilleurNom = "";

            for (int i = 0; i < MODELES.length; i++) {
                Algo modele = MODELES[i];
                noms[i] = modele.getNom();

                System.out.println("► Exécution de " + noms[i] + "...");

                long debut = System.nanoTime();
                Composition solution = modele.resoudre(
                        donnees.abonnes, donnees.articles, donnees.poidsMax,
                        donnees.prixMin, donnees.prixMax);
                long fin = System.nanoTime();

                EvaluateurScoreGlouton evalGlouton = new EvaluateurScoreGlouton();
                double score = evalGlouton.evaluer(solution, donnees.poidsMax, donnees.prixMin, donnees.prixMax);

                double duree = (fin - debut) / 1_000_000_000.0;

                scores[i] = score;
                temps[i] = duree;
                articlesAffectes[i] = solution.getNombreArticlesAffectes();

                System.out.println("  Score : " + (int) score + " | Temps : "
                        + String.format("%.4f", duree) + "s");

                // Sauvegarder le CSV de chaque modèle
                // Extraire le nom du dataset (ex: "pb5")
                String nomDataset = new java.io.File(cheminFichier).getName().replaceFirst("[.][^.]+$", "");
                String nomFichierSortie = "results/solution_" + noms[i].toLowerCase() + "_" + nomDataset + ".csv";
                csvManager.sauvegarderSolution(nomFichierSortie, solution, score);

                if (score > meilleurScore) {
                    meilleurScore = score;
                    meilleureSolution = solution;
                    meilleurNom = noms[i];
                }
            }

            // --- Tableau comparatif ---
            System.out.println();
            afficherTableau(noms, scores, temps, articlesAffectes, donnees.articles.size());

            // --- Sauvegarder la meilleure solution ---
            if (meilleureSolution != null) {
                csvManager.sauvegarderSolution("results/solution.csv", meilleureSolution, meilleurScore);
                System.out.println("\n" + "Solution (" + meilleurNom
                        + ", score " + (int) meilleurScore + ") sauvegardee dans results/solution.csv");
            }

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Affiche le tableau comparatif des résultats.
     */
    private static void afficherTableau(String[] noms, double[] scores, double[] temps,
            int[] articlesAffectes, int totalArticles) {
        // Calculer les largeurs de colonnes
        int largNom = 20;
        for (String nom : noms)
            largNom = Math.max(largNom, nom.length() + 2);

        String format = "║ %-" + largNom + "s ║ %7s ║ %10s ║ %10s ║%n";
        String separateur = repeter("═", largNom + 2);

        System.out.println("╔" + separateur + "╦═════════╦════════════╦════════════╗");
        System.out.printf(format, "Modèle", "Score", "Temps", "Articles");
        System.out.println("╠" + separateur + "╬═════════╬════════════╬════════════╣");

        for (int i = 0; i < noms.length; i++) {
            System.out.printf(format,
                    noms[i],
                    (int) scores[i],
                    String.format("%.4fs", temps[i]),
                    articlesAffectes[i] + "/" + totalArticles);
        }

        System.out.println("╚" + separateur + "╩═════════╩════════════╩════════════╝");
    }

    /** Répète un caractère n fois. */
    private static String repeter(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++)
            sb.append(s);
        return sb.toString();
    }
}
