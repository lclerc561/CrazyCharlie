package com.toysacademy.service;

import com.toysacademy.model.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Modèle Glouton Pur — Approche gloutonne uniquement.
 * Remplit les boxes article par article en choisissant à chaque étape
 * l'affectation la plus rentable (meilleur gain marginal) parmi les
 * articles compatibles. Pas de phase d'optimisation post-construction.
 *
 * @see <a href="https://fr.wikipedia.org/wiki/Algorithme_glouton">Algorithme
 *      glouton</a>
 */
public class ModeleGlouton implements Algo {

    @Override
    public String getNom() {
        return "Glouton";
    }

    @Override
    public Composition resoudre(List<Abonne> abonnes, List<Article> articles, double poidsMax) {
        Map<Age, List<Article>> indexParAge = articles.stream()
                .collect(Collectors.groupingBy(Article::getTrancheAge));

        Composition composition = new Composition();
        for (Abonne abonne : abonnes) {
            composition.ajouterBox(new Box(abonne));
        }

        Set<Article> disponibles = new HashSet<>(articles);

        // Pré-calculer les articles compatibles par box (Exact + Adjacent)
        Map<Box, List<Article>> compatiblesParBox = new HashMap<>();
        for (Box box : composition.getBoxes()) {
            compatiblesParBox.put(box, getArticlesCompatibles(box.getAbonne(), indexParAge));
        }

        // Boucle gloutonne : à chaque itération, trouver la meilleure affectation
        while (true) {
            Box meilleureBox = null;
            Article meilleurArticle = null;
            double meilleurGain = -1;

            for (Box box : composition.getBoxes()) {
                List<Article> compatibles = compatiblesParBox.get(box);
                for (Article article : compatibles) {
                    if (!disponibles.contains(article))
                        continue;
                    if (box.getPoidsTotal() + article.getPoids() > poidsMax)
                        continue;

                    double gain = calculerGainMarginal(article, box);
                    if (gain > meilleurGain) {
                        meilleurGain = gain;
                        meilleureBox = box;
                        meilleurArticle = article;
                    }
                }
            }

            if (meilleureBox != null) {
                meilleureBox.addArticle(meilleurArticle);
                disponibles.remove(meilleurArticle);
            } else {
                break;
            }
        }

        composition.setArticlesNonAffectes(new ArrayList<>(disponibles));
        return composition;
    }

    private List<Article> getArticlesCompatibles(Abonne abonne, Map<Age, List<Article>> indexParAge) {
        List<Article> result = new ArrayList<>();
        for (Age ageEnfant : abonne.getTrancheAgesEnfants()) {
            result.addAll(indexParAge.getOrDefault(ageEnfant, Collections.emptyList()));
            for (Age age : Age.values()) {
                if (age.isAdjacent(ageEnfant)) {
                    result.addAll(indexParAge.getOrDefault(age, Collections.emptyList()));
                }
            }
        }
        return new ArrayList<>(new LinkedHashSet<>(result));
    }

    private double calculerGainMarginal(Article article, Box box) {
        Abonne abonne = box.getAbonne();
        int rangPref = abonne.getRangPreference(article.getCategorie());

        int compte = 0;
        for (Article a : box.getArticles()) {
            if (a.getCategorie() == article.getCategorie())
                compte++;
        }

        int points = 0;
        if (rangPref > 0) {
            int rangEffectif = rangPref + compte;
            points = EvaluateurScoreGlouton.pointsPourRang(rangEffectif);
        }

        if (abonne.isArticleAdjacentSeulement(article) && points > 0) {
            points = Math.max(1, (int) Math.ceil(points / 2.0));
        }

        return points + article.getEtat().getBonus();
    }
}
