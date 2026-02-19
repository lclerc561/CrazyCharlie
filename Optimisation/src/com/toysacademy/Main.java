package com.toysacademy;

import com.toysacademy.io.AnalyseurFichier;
import com.toysacademy.io.AnalyseurFichier.DonneesProbleme;
import com.toysacademy.io.CSVManager;
import com.toysacademy.model.*;
import com.toysacademy.service.*;
// unused import removed

/**
 * Point d'entrée principal (hérité, conservé pour compatibilité).
 * Pour le benchmarking multi-modèles, utiliser {@link Lanceur}.
 */
public class Main {
    public static void main(String[] args) {
        try {
            String cheminFichier = args.length > 0 ? args[0] : "./Optimisation/data/exemple.csv";

            // Parser le fichier unifié
            AnalyseurFichier analyseur = new AnalyseurFichier();
            DonneesProbleme donnees = analyseur.analyser(cheminFichier);

            System.out.println("Chargement de " + donnees.articles.size()
                    + " articles et " + donnees.abonnes.size() + " abonnés.");

            EvaluateurScoreGlouton evaluateur = new EvaluateurScoreGlouton();
            CSVManager csvManager = new CSVManager();

            // Modèle GloutonDescente (par défaut)
            Algo modele = new ModeleGloutonDescente();
            Composition solution = modele.resoudre(
                    donnees.abonnes, donnees.articles, donnees.poidsMax);
            double score = evaluateur.evaluer(solution, donnees.poidsMax);
            System.out.println("Score " + modele.getNom() + " : " + (int) score);
            csvManager.sauvegarderSolution("results/solution_gloutondescente.csv", solution, score);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
