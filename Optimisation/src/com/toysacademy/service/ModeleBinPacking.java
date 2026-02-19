package com.toysacademy.service;

import com.toysacademy.model.*;
import java.util.*;

/**
 * Modèle Bin Packing — Adapte le problème en un sac à dos multi-dimensionnel.
 *
 * Chaque box est un "sac" avec contrainte de poids max.
 * La valeur de chaque article est son score de préférence + bonus état.
 * Utilise une heuristique Best-Fit Decreasing + optimisation locale.
 *
 * @see <a href="https://fr.wikipedia.org/wiki/Problème_du_sac_à_dos">Sac à
 *      dos</a>
 */
public class ModeleBinPacking implements Algo {

    private final EvaluateurScoreGlouton evaluateur = new EvaluateurScoreGlouton();

    private int prixMin = 0;
    private int prixMax = Integer.MAX_VALUE;

    @Override
    public String getNom() {
        return "BinPacking";
    }

    @Override
    public Composition resoudre(List<Abonne> abonnes, List<Article> articles,
            double poidsMax, int prixMin, int prixMax) {
        this.prixMin = prixMin;
        this.prixMax = prixMax;
        return resoudre(abonnes, articles, poidsMax);
    }

    @Override
    public Composition resoudre(List<Abonne> abonnes, List<Article> articles, double poidsMax) {
        // Étape 1 : Résolution par sac à dos pour chaque box individuellement
        Composition solution = sacADosParBox(abonnes, articles, poidsMax);

        // Étape 2 : Rééquilibrage pour l'équité (malus si écart >= 2 articles)
        reequilibrer(solution, poidsMax);

        // Étape 3 : Optimisation locale par échanges
        optimisationLocale(solution, poidsMax);

        return solution;
    }

    /**
     * Résout un problème de sac à dos (0/1 knapsack) par programmation dynamique
     * pour chaque box indépendamment.
     */
    private Composition sacADosParBox(List<Abonne> abonnes, List<Article> articles,
            double poidsMax) {
        Composition composition = new Composition();
        Set<Article> utilises = new HashSet<>();

        // Trier les abonnés : ceux avec moins de choix d'abord (plus contraint d'abord)
        List<Abonne> abonnesTries = new ArrayList<>(abonnes);
        abonnesTries.sort((a, b) -> {
            int choixA = (int) articles.stream()
                    .filter(art -> a.isArticleCompatible(art)).count();
            int choixB = (int) articles.stream()
                    .filter(art -> b.isArticleCompatible(art)).count();
            return Integer.compare(choixA, choixB);
        });

        for (Abonne abonne : abonnesTries) {
            Box box = new Box(abonne);
            composition.ajouterBox(box);

            // Articles compatibles et disponibles
            List<Article> compatibles = new ArrayList<>();
            for (Article article : articles) {
                if (!utilises.contains(article) && abonne.isArticleCompatible(article)
                        && article.getPoids() <= poidsMax) {
                    compatibles.add(article);
                }
            }

            // Trier par ratio valeur/poids décroissant (heuristique fractionnaire)
            compatibles.sort((a, b) -> {
                double ratioA = estimerValeur(a, abonne) / Math.max(1, a.getPoids());
                double ratioB = estimerValeur(b, abonne) / Math.max(1, b.getPoids());
                return Double.compare(ratioB, ratioA);
            });

            // Limiter pour la performance (top N candidats)
            if (compatibles.size() > 50) {
                compatibles = compatibles.subList(0, 50);
            }

            // Sac à dos 0/1 par programmation dynamique (poids discrétisé)
            List<Article> selection = knapsackDP(compatibles, poidsMax, abonne);

            for (Article article : selection) {
                box.addArticle(article);
                utilises.add(article);
            }
        }

        // Articles non affectés
        List<Article> nonAffectes = new ArrayList<>();
        for (Article article : articles) {
            if (!utilises.contains(article)) {
                nonAffectes.add(article);
            }
        }
        composition.setArticlesNonAffectes(nonAffectes);

        return composition;
    }

    /**
     * Sac à dos 0/1 par programmation dynamique.
     * Discrétise le poids en grammes.
     */
    private List<Article> knapsackDP(List<Article> articles, double poidsMax, Abonne abonne) {
        int n = articles.size();
        int capacite = (int) poidsMax;

        if (n == 0 || capacite <= 0)
            return new ArrayList<>();

        // dp[i] = meilleur score atteignable avec un poids total <= i
        double[] dp = new double[capacite + 1];
        boolean[][] choisi = new boolean[n][capacite + 1];

        for (int i = 0; i < n; i++) {
            Article article = articles.get(i);
            int poids = (int) article.getPoids();
            double valeur = estimerValeurContextuelle(article, abonne, articles, i);

            // Parcours inverse pour 0/1 knapsack
            for (int w = capacite; w >= poids; w--) {
                if (dp[w - poids] + valeur > dp[w]) {
                    dp[w] = dp[w - poids] + valeur;
                    choisi[i][w] = true;
                }
            }
        }

        // Reconstruction de la solution
        List<Article> selection = new ArrayList<>();
        int w = capacite;
        for (int i = n - 1; i >= 0; i--) {
            if (choisi[i][w]) {
                selection.add(articles.get(i));
                w -= (int) articles.get(i).getPoids();
            }
        }

        return selection;
    }

    /**
     * Estimation de la valeur d'un article tenant compte du contexte
     * (catégories déjà sélectionnées pour l'utilité dégressive).
     */
    private double estimerValeurContextuelle(Article article, Abonne abonne,
            List<Article> candidats, int currentIndex) {
        int rangPref = abonne.getRangPreference(article.getCategorie());

        // Compter les articles de même catégorie déjà avant dans la liste
        int compte = 0;
        for (int i = 0; i < currentIndex; i++) {
            if (candidats.get(i).getCategorie() == article.getCategorie())
                compte++;
        }

        int points = 0;
        if (rangPref > 0) {
            points = EvaluateurScoreGlouton.pointsPourRang(rangPref + compte);
        }

        if (abonne.isArticleAdjacentSeulement(article) && points > 0) {
            points = Math.max(1, (int) Math.ceil(points / 2.0));
        }

        return points + article.getEtat().getBonus();
    }

    private double estimerValeur(Article article, Abonne abonne) {
        int rangPref = abonne.getRangPreference(article.getCategorie());
        int points = 0;
        if (rangPref > 0) {
            points = EvaluateurScoreGlouton.pointsPourRang(rangPref);
        }
        if (abonne.isArticleAdjacentSeulement(article) && points > 0) {
            points = Math.max(1, (int) Math.ceil(points / 2.0));
        }
        return points + article.getEtat().getBonus();
    }

    /**
     * Rééquilibrage : transfère des articles des boxes sur-remplies vers celles
     * qui en manquent pour réduire le malus d'équité.
     */
    private void reequilibrer(Composition composition, double poidsMax) {
        List<Box> boxes = composition.getBoxes();
        List<Article> pool = composition.getArticlesNonAffectes();

        for (int round = 0; round < 5; round++) {
            int maxArticles = 0;
            int minArticles = Integer.MAX_VALUE;
            for (Box box : boxes) {
                maxArticles = Math.max(maxArticles, box.getNombreArticles());
                minArticles = Math.min(minArticles, box.getNombreArticles());
            }
            if (maxArticles - minArticles < 2)
                break;

            // Transférer du max vers le min
            for (Box boxRiche : boxes) {
                if (boxRiche.getNombreArticles() < maxArticles)
                    continue;
                for (Box boxPauvre : boxes) {
                    if (boxPauvre.getNombreArticles() > minArticles)
                        continue;
                    if (boxRiche == boxPauvre)
                        continue;

                    // Chercher un article transférable
                    for (Article article : new ArrayList<>(boxRiche.getArticles())) {
                        if (boxPauvre.getAbonne().isArticleCompatible(article)
                                && boxPauvre.getPoidsTotal() + article.getPoids() <= poidsMax) {
                            boxRiche.removeArticle(article);
                            boxPauvre.addArticle(article);
                            break;
                        }
                    }
                }
            }

            // Tenter de placer des articles du pool dans les boxes pauvres
            for (Box boxPauvre : boxes) {
                if (boxPauvre.getNombreArticles() >= minArticles + 1)
                    continue;
                for (int i = pool.size() - 1; i >= 0; i--) {
                    Article article = pool.get(i);
                    if (boxPauvre.getAbonne().isArticleCompatible(article)
                            && boxPauvre.getPoidsTotal() + article.getPoids() <= poidsMax) {
                        pool.remove(i);
                        boxPauvre.addArticle(article);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Optimisation locale : échanges d'articles entre boxes si ça améliore le
     * score.
     */
    private void optimisationLocale(Composition composition, double poidsMax) {
        double scoreCourant = evaluateur.evaluer(composition, poidsMax, prixMin, prixMax);
        boolean ameliore = true;

        for (int iter = 0; iter < 1000 && ameliore; iter++) {
            ameliore = false;
            List<Box> boxes = composition.getBoxes();

            // Échanges 2-opt entre toutes les paires de boxes
            for (int i = 0; i < boxes.size() && !ameliore; i++) {
                for (int j = i + 1; j < boxes.size() && !ameliore; j++) {
                    Box b1 = boxes.get(i);
                    Box b2 = boxes.get(j);

                    for (Article a1 : new ArrayList<>(b1.getArticles())) {
                        for (Article a2 : new ArrayList<>(b2.getArticles())) {
                            // Vérifier compatibilité croisée
                            if (!b2.getAbonne().isArticleCompatible(a1)
                                    || !b1.getAbonne().isArticleCompatible(a2))
                                continue;

                            double p1 = b1.getPoidsTotal() - a1.getPoids() + a2.getPoids();
                            double p2 = b2.getPoidsTotal() - a2.getPoids() + a1.getPoids();
                            if (p1 > poidsMax || p2 > poidsMax)
                                continue;

                            // Essayer l'échange
                            b1.removeArticle(a1);
                            b2.removeArticle(a2);
                            b1.addArticle(a2);
                            b2.addArticle(a1);

                            double nouveauScore = evaluateur.evaluer(composition, poidsMax, prixMin, prixMax);
                            if (nouveauScore > scoreCourant) {
                                scoreCourant = nouveauScore;
                                ameliore = true;
                                break;
                            } else {
                                // Annuler
                                b1.removeArticle(a2);
                                b2.removeArticle(a1);
                                b1.addArticle(a1);
                                b2.addArticle(a2);
                            }
                        }
                        if (ameliore)
                            break;
                    }
                }
            }
        }
    }
}
