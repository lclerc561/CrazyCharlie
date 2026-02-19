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

            EvaluateurScoreGlouton evaluateurGlouton = new EvaluateurScoreGlouton();
            CSVManager csvManager = new CSVManager();

            // Modèle Glouton
            Solveur modeleGlouton = new ModeleGlouton();
            Composition solutionGlouton = modeleGlouton.resoudre(
                    donnees.abonnes, donnees.articles, donnees.poidsMax);
            double scoreGlouton = evaluateurGlouton.evaluer(solutionGlouton, donnees.poidsMax);
            System.out.println("Score " + modeleGlouton.getNom() + " : " + (int) scoreGlouton);
            csvManager.sauvegarderSolution("results/solution_modeleglouton.csv", solutionGlouton, scoreGlouton);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
