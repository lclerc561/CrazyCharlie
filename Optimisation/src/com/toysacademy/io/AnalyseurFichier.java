package com.toysacademy.io;

import com.toysacademy.model.*;
import com.toysacademy.model.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Analyseur du fichier d'entrée au format unifié (data_example.txt).
 * Machine à états : lit les sections « articles », « abonnes » et « parametres
 * ».
 *
 * Format abonnés multi-enfants :
 * id; prenom; age1,age2; pref1, pref2, ...
 *
 * Format paramètres :
 * poidsMax
 * prixMin;prixMax (optionnel)
 */
public class AnalyseurFichier {

    /** Résultat du parsing contenant les composants du problème. */
    public static class DonneesProbleme {
        public final List<Article> articles;
        public final List<Abonne> abonnes;
        public final double poidsMax;
        public final int prixMin;
        public final int prixMax;

        public DonneesProbleme(List<Article> articles, List<Abonne> abonnes,
                double poidsMax, int prixMin, int prixMax) {
            this.articles = articles;
            this.abonnes = abonnes;
            this.poidsMax = poidsMax;
            this.prixMin = prixMin;
            this.prixMax = prixMax;
        }

        /** Constructeur de compatibilité (sans prix). */
        public DonneesProbleme(List<Article> articles, List<Abonne> abonnes, double poidsMax) {
            this(articles, abonnes, poidsMax, 0, Integer.MAX_VALUE);
        }
    }

    /**
     * Analyse le fichier d'entrée unifié et retourne les données du problème.
     */
    public DonneesProbleme analyser(String cheminFichier) throws IOException {
        List<Article> articles = new ArrayList<>();
        List<Abonne> abonnes = new ArrayList<>();
        double poidsMax = 0;
        int prixMin = 0;
        int prixMax = Integer.MAX_VALUE;

        List<String> lignes = Files.readAllLines(Paths.get(cheminFichier));
        String section = null;
        boolean poidsMaxLu = false;

        for (String ligneBrute : lignes) {
            String ligne = ligneBrute.trim();
            if (ligne.isEmpty())
                continue;

            // Détection de section
            String ligneMin = ligne.toLowerCase();
            if (ligneMin.contains("articles") && section == null) {
                section = "articles";
                continue;
            } else if (ligneMin.contains("abonnes") || ligneMin.contains("abonnés")) {
                section = "abonnes";
                continue;
            } else if (ligneMin.contains("parametres") || ligneMin.contains("paramètres")) {
                section = "parametres";
                poidsMaxLu = false;
                continue;
            }

            switch (section != null ? section : "") {
                case "articles":
                    analyserArticle(ligne, articles);
                    break;
                case "abonnes":
                    analyserAbonne(ligne, abonnes);
                    break;
                case "parametres":
                    if (!poidsMaxLu) {
                        try {
                            poidsMax = Double.parseDouble(ligne.trim());
                            poidsMaxLu = true;
                        } catch (NumberFormatException e) {
                            // Ignorer
                        }
                    } else {
                        // Deuxième ligne : prixMin;prixMax
                        try {
                            String[] parts = ligne.split(";");
                            if (parts.length >= 2) {
                                prixMin = Integer.parseInt(parts[0].trim());
                                prixMax = Integer.parseInt(parts[1].trim());
                            }
                        } catch (NumberFormatException e) {
                            // Ignorer
                        }
                    }
                    break;
            }
        }

        return new DonneesProbleme(articles, abonnes, poidsMax, prixMin, prixMax);
    }

    /**
     * Parse une ligne de la section articles.
     * Format: id; désignation; catégorie; âge; état; prix; poids
     */
    private void analyserArticle(String ligne, List<Article> articles) {
        String[] parties = ligne.split(";");
        if (parties.length < 7)
            return;

        try {
            String id = parties[0].trim();
            String designation = parties[1].trim();
            Category categorie = Category.fromString(parties[2]);
            Age trancheAge = Age.fromString(parties[3]);
            State etat = State.fromString(parties[4]);
            int prix = Integer.parseInt(parties[5].trim());
            double poids = Double.parseDouble(parties[6].trim());

            articles.add(new Article(id, designation, categorie, trancheAge, etat, prix, poids));
        } catch (Exception e) {
            System.err.println("Ligne article ignorée : " + ligne + " -> " + e.getMessage());
        }
    }

    /**
     * Parse une ligne de la section abonnés.
     * Format mono-enfant : id; prénom; age; pref1, pref2, ...
     * Format multi-enfants : id; prénom; age1,age2; pref1, pref2, ...
     */
    private void analyserAbonne(String ligne, List<Abonne> abonnes) {
        String[] parties = ligne.split(";");
        if (parties.length < 4)
            return;

        try {
            String id = parties[0].trim();
            String prenom = parties[1].trim();

            // Parser les tranches d'âge (peut être "PE" ou "PE,EN")
            List<Age> tranchesAge = new ArrayList<>();
            String ageField = parties[2].trim();
            String[] ageTokens = ageField.split(",");
            for (String ageToken : ageTokens) {
                String at = ageToken.trim();
                if (!at.isEmpty()) {
                    tranchesAge.add(Age.fromString(at));
                }
            }

            // Les préférences sont séparées par des virgules dans le 4ème champ
            List<Category> preferences = new ArrayList<>();
            String[] prefs = parties[3].split(",");
            for (String pref : prefs) {
                String p = pref.trim();
                if (!p.isEmpty()) {
                    preferences.add(Category.fromString(p));
                }
            }

            abonnes.add(new Abonne(id, prenom, tranchesAge, preferences));
        } catch (Exception e) {
            System.err.println("Ligne abonné ignorée : " + ligne + " -> " + e.getMessage());
        }
    }
}
