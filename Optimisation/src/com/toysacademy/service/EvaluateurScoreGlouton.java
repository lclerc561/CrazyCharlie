package com.toysacademy.service;

import com.toysacademy.model.*;
import java.util.*;

/**
 * Évaluateur de score "Glouton" (Basique / Strict).
 * Désactive les "améliorations" suivantes :
 * - Compatibilité d'âge élargie (seul l'âge exact compte)
 * - Bonus/Malus Multi-enfants (pas de contrainte de couverture, pas de malus)
 * - Calibrage prix (pas de malus hors fourchette)
 *
 * Règles conservées :
 * 1. Poids max (contrainte dure)
 * 2. Box vide -> malus -10
 * 3. Préférences avec utilité dégressive
 * 4. Bonus état
 * 5. Malus d'équité (global)
 */
public class EvaluateurScoreGlouton {

    private static final int[] POINTS_PAR_RANG = { 0, 10, 8, 6, 4, 2, 1 };

    public static int pointsPourRang(int rang) {
        if (rang > 0 && rang < POINTS_PAR_RANG.length) {
            return POINTS_PAR_RANG[rang];
        }
        return (rang > 0) ? 1 : 0;
    }

    public double evaluerBox(Box box, double poidsMax) {
        Abonne abonne = box.getAbonne();
        List<Article> articles = box.getArticles();
        double scoreBox = 0;

        // Poids max
        if (box.getPoidsTotal() > poidsMax) {
            return Double.NEGATIVE_INFINITY;
        }

        // Box vide
        if (articles.isEmpty()) {
            return -10.0;
        }

        Map<Category, Integer> compteurCategories = new EnumMap<>(Category.class);

        for (Article article : articles) {
            // STRICT : Seul l'âge exact est autorisé
            if (!abonne.isArticleExact(article)) {
                return Double.NEGATIVE_INFINITY;
            }

            Category cat = article.getCategorie();
            int compte = compteurCategories.getOrDefault(cat, 0);
            compteurCategories.put(cat, compte + 1);

            int rangBase = abonne.getRangPreference(cat);
            int pointsPreference = 0;

            if (rangBase > 0) {
                int rangEffectif = rangBase + compte;
                pointsPreference = pointsPourRang(rangEffectif);
            }

            // Pas de division par 2 car pas d'adjacent autorisé ici

            int bonusEtat = article.getEtat().getBonus();
            scoreBox += pointsPreference + bonusEtat;
        }

        // PAS de malus multi-enfants
        // PAS de malus prix

        return scoreBox;
    }

    public double evaluer(Composition composition, double poidsMax) {
        double scoreTotal = 0;
        List<Box> boxes = composition.getBoxes();
        int maxArticles = 0;

        for (Box box : boxes) {
            double scoreBox = evaluerBox(box, poidsMax);
            if (Double.isInfinite(scoreBox) && scoreBox < 0) {
                return Double.NEGATIVE_INFINITY;
            }
            scoreTotal += scoreBox;
            if (box.getNombreArticles() > maxArticles) {
                maxArticles = box.getNombreArticles();
            }
        }

        // Malus d'équité (conservé car non demandé à être retiré explicitement, mais
        // fait partie des règles de base)
        for (Box box : boxes) {
            if (maxArticles - box.getNombreArticles() >= 2) {
                scoreTotal -= 10;
            }
        }

        return scoreTotal;
    }
}
