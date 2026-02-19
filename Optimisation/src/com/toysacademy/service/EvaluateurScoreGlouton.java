package com.toysacademy.service;

import com.toysacademy.model.*;
import java.util.*;

/**
 * Évaluateur de score "Glouton" (Optimisé).
 * Implémente les améliorations suivantes :
 * - Compatibilité d'âge élargie (acceptée avec pénalité sur le score)
 * - Bonus/Malus Multi-enfants (pénalité si un enfant n'est pas couvert)
 * - Calibrage prix (pénalité si hors fourchette)
 *
 * Règles :
 * 1. Poids max (contrainte dure)
 * 2. Box vide -> malus -10
 * 3. Préférences avec utilité dégressive
 * 4. Bonus état
 * 5. Malus d'équité (global)
 */
public class EvaluateurScoreGlouton {

    // Barème des points par rang de préférence (Règle 4)
    private static final int[] POINTS_PAR_RANG = { 0, 10, 8, 6, 4, 2, 1 };

    /**
     * Retourne les points pour un rang donné (1-based).
     */
    public static int pointsPourRang(int rang) {
        if (rang > 0 && rang < POINTS_PAR_RANG.length) {
            return POINTS_PAR_RANG[rang];
        }
        return (rang > 0) ? 1 : 0;
    }

    /**
     * Calcule le score d'une seule box.
     * Retourne Double.NEGATIVE_INFINITY si contrainte dure violée (poids).
     */
    public double evaluerBox(Box box, double poidsMax, int prixMin, int prixMax) {
        Abonne abonne = box.getAbonne();
        List<Article> articles = box.getArticles();
        double scoreBox = 0;

        // Règle 3 : Poids maximum
        if (box.getPoidsTotal() > poidsMax) {
            return Double.NEGATIVE_INFINITY;
        }

        // Règle 7 : Box vide
        if (articles.isEmpty()) {
            scoreBox -= 10;
            if (abonne.isMultiEnfants()) {
                scoreBox -= 10 * abonne.getTrancheAgesEnfants().size();
            }
            return scoreBox;
        }

        // Compteur de catégories pour utilités dégressives
        Map<Category, Integer> compteurCategories = new EnumMap<>(Category.class);

        for (Article article : articles) {
            boolean exact = abonne.isArticleExact(article);
            boolean adjacent = !exact && abonne.isArticleCompatible(article);

            if (!exact && !adjacent) {
                return Double.NEGATIVE_INFINITY; // Article incompatible
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

            // Pénalité adjacent (points divisés par 2)
            if (adjacent && pointsPreference > 0) {
                pointsPreference = Math.max(1, (int) Math.ceil(pointsPreference / 2.0));
            }

            int bonusEtat = article.getEtat().getBonus();
            scoreBox += pointsPreference + bonusEtat;
        }

        // Règle 9 : Multi-enfants
        // Chaque enfant doit avoir au moins un article compatible (exact ou adjacent)
        if (abonne.isMultiEnfants()) {
            for (Age ageEnfant : abonne.getTrancheAgesEnfants()) {
                boolean couvert = false;
                for (Article article : articles) {
                    if (article.getTrancheAge().isCompatible(ageEnfant)) {
                        couvert = true;
                        break;
                    }
                }
                if (!couvert) {
                    scoreBox -= 10; // Pénalité par enfant non couvert
                }
            }
        }

        // Règle 10 : Calibrage en prix
        if (prixMin > 0 || prixMax < Integer.MAX_VALUE) {
            int prixTotalBox = box.getPrixTotal();
            if (prixTotalBox < prixMin || prixTotalBox > prixMax) {
                scoreBox -= 5;
            }
        }

        return scoreBox;
    }

    /**
     * Évalue le score global.
     */
    public double evaluer(Composition composition, double poidsMax, int prixMin, int prixMax) {
        double scoreTotal = 0;
        List<Box> boxes = composition.getBoxes();

        // Pour le malus d'équité (Règle 8)
        int maxArticles = 0;

        for (Box box : boxes) {
            double scoreBox = evaluerBox(box, poidsMax, prixMin, prixMax);
            if (Double.isInfinite(scoreBox) && scoreBox < 0) {
                return Double.NEGATIVE_INFINITY;
            }
            scoreTotal += scoreBox;
            if (box.getNombreArticles() > maxArticles) {
                maxArticles = box.getNombreArticles();
            }
        }

        // Règle 8 : Malus d'équité global
        for (Box box : boxes) {
            if (maxArticles - box.getNombreArticles() >= 2) {
                scoreTotal -= 10;
            }
        }

        return scoreTotal;
    }

    /**
     * Surcharge pour compatibilité (sans prix).
     */
    public double evaluer(Composition composition, double poidsMax) {
        return evaluer(composition, poidsMax, 0, Integer.MAX_VALUE);
    }
}
