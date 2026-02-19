package com.toysacademy.service;

import com.toysacademy.model.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Modèle Recuit Simulé — Exploration de l'espace des solutions en acceptant
 * parfois des dégradations temporaires pour échapper aux optima locaux.
 *
 * Phase 1 : Construction gloutonne (identique au ModeleGlouton).
 * Phase 2 : Recuit simulé avec refroidissement géométrique.
 *
 * @see <a href="https://fr.wikipedia.org/wiki/Recuit_simulé">Recuit simulé</a>
 */
public class ModeleRecuitSimule implements Algo {

    private final EvaluateurScoreGlouton evaluateur = new EvaluateurScoreGlouton();
    private final Random aleatoire = new Random(42);

    // Paramètres du recuit simulé
    private static final double TEMPERATURE_INITIALE = 200.0;
    private static final double TEMPERATURE_FINALE = 0.01;
    private static final double TAUX_REFROIDISSEMENT = 0.9997;
    private static final int ITERATIONS_PAR_PALIER = 5;
    private static final int MAX_ITERATIONS = 100_000;

    private int prixMin = 0;
    private int prixMax = Integer.MAX_VALUE;

    @Override
    public String getNom() {
        return "RecuitSimule";
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
        Map<Age, List<Article>> indexParAge = articles.stream()
                .collect(Collectors.groupingBy(Article::getTrancheAge));

        // Phase 1 : Solution initiale gloutonne
        Composition solutionInitiale = construireGlouton(abonnes, indexParAge, articles, poidsMax);

        // Phase 2 : Recuit simulé
        return recuitSimule(solutionInitiale, abonnes, poidsMax);
    }

    // =========================================================================
    // Phase 1 : Construction gloutonne
    // =========================================================================

    private Composition construireGlouton(List<Abonne> abonnes,
            Map<Age, List<Article>> indexParAge,
            List<Article> allArticles, double poidsMax) {
        Composition composition = new Composition();
        for (Abonne abonne : abonnes) {
            composition.ajouterBox(new Box(abonne));
        }

        Set<Article> disponibles = new HashSet<>(allArticles);
        Map<Box, List<Article>> compatiblesParBox = new HashMap<>();
        for (Box box : composition.getBoxes()) {
            compatiblesParBox.put(box, getArticlesCompatibles(box.getAbonne(), indexParAge));
        }

        while (true) {
            Box meilleureBox = null;
            Article meilleurArticle = null;
            double meilleurGain = -1;

            for (Box box : composition.getBoxes()) {
                for (Article article : compatiblesParBox.get(box)) {
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

    // =========================================================================
    // Phase 2 : Recuit Simulé
    // =========================================================================

    private Composition recuitSimule(Composition solutionInitiale,
            List<Abonne> abonnes, double poidsMax) {
        Composition solutionActuelle = clonerComposition(solutionInitiale);
        double scoreActuel = evaluateur.evaluer(solutionActuelle, poidsMax, prixMin, prixMax);

        Composition meilleureSolution = clonerComposition(solutionActuelle);
        double meilleurScore = scoreActuel;

        double temperature = TEMPERATURE_INITIALE;
        int iterationsSansAmelioration = 0;

        for (int iter = 0; iter < MAX_ITERATIONS && temperature > TEMPERATURE_FINALE; iter++) {
            // Générer un voisin par mutation
            Composition voisin = clonerComposition(solutionActuelle);
            boolean mutationOk = effectuerMutation(voisin, poidsMax);

            if (!mutationOk)
                continue;

            double scoreVoisin = evaluateur.evaluer(voisin, poidsMax, prixMin, prixMax);

            if (Double.isInfinite(scoreVoisin) && scoreVoisin < 0)
                continue;

            double deltaE = scoreVoisin - scoreActuel;

            // Critère d'acceptation Metropolis
            if (deltaE > 0 || aleatoire.nextDouble() < Math.exp(deltaE / temperature)) {
                solutionActuelle = voisin;
                scoreActuel = scoreVoisin;

                if (scoreActuel > meilleurScore) {
                    meilleureSolution = clonerComposition(solutionActuelle);
                    meilleurScore = scoreActuel;
                    iterationsSansAmelioration = 0;
                } else {
                    iterationsSansAmelioration++;
                }
            } else {
                iterationsSansAmelioration++;
            }

            // Refroidissement géométrique
            if (iter % ITERATIONS_PAR_PALIER == 0) {
                temperature *= TAUX_REFROIDISSEMENT;
            }

            // Réchauffement si bloqué trop longtemps
            if (iterationsSansAmelioration > 5000) {
                temperature = Math.min(temperature * 2.0, TEMPERATURE_INITIALE / 2.0);
                iterationsSansAmelioration = 0;
            }
        }

        return meilleureSolution;
    }

    // =========================================================================
    // Mutations (4 types de voisinage)
    // =========================================================================

    private boolean effectuerMutation(Composition comp, double poidsMax) {
        int type = aleatoire.nextInt(4);
        List<Box> boxes = comp.getBoxes();
        List<Article> pool = comp.getArticlesNonAffectes();

        if (boxes.isEmpty())
            return false;

        switch (type) {
            case 0: { // Retrait : Box → Pool
                Box box = boxes.get(aleatoire.nextInt(boxes.size()));
                if (box.getArticles().isEmpty())
                    return false;
                Article a = box.getArticles().get(aleatoire.nextInt(box.getArticles().size()));
                box.removeArticle(a);
                pool.add(a);
                return true;
            }
            case 1: { // Ajout : Pool → Box compatible
                if (pool.isEmpty())
                    return false;
                Article article = pool.get(aleatoire.nextInt(pool.size()));
                List<Box> cibles = new ArrayList<>();
                for (Box box : boxes) {
                    if (box.getAbonne().isArticleCompatible(article)
                            && box.getPoidsTotal() + article.getPoids() <= poidsMax) {
                        cibles.add(box);
                    }
                }
                if (cibles.isEmpty())
                    return false;
                Box cible = cibles.get(aleatoire.nextInt(cibles.size()));
                pool.remove(article);
                cible.addArticle(article);
                return true;
            }
            case 2: { // Transfert : Box → Box
                if (boxes.size() < 2)
                    return false;
                Box src = boxes.get(aleatoire.nextInt(boxes.size()));
                if (src.getArticles().isEmpty())
                    return false;
                Article article = src.getArticles().get(aleatoire.nextInt(src.getArticles().size()));
                List<Box> dests = new ArrayList<>();
                for (Box box : boxes) {
                    if (box != src && box.getAbonne().isArticleCompatible(article)
                            && box.getPoidsTotal() + article.getPoids() <= poidsMax) {
                        dests.add(box);
                    }
                }
                if (dests.isEmpty())
                    return false;
                Box dest = dests.get(aleatoire.nextInt(dests.size()));
                src.removeArticle(article);
                dest.addArticle(article);
                return true;
            }
            case 3: { // Échange : Box ↔ Box
                if (boxes.size() < 2)
                    return false;
                Box b1 = boxes.get(aleatoire.nextInt(boxes.size()));
                Box b2 = boxes.get(aleatoire.nextInt(boxes.size()));
                if (b1 == b2 || b1.getArticles().isEmpty() || b2.getArticles().isEmpty())
                    return false;
                Article a1 = b1.getArticles().get(aleatoire.nextInt(b1.getArticles().size()));
                Article a2 = b2.getArticles().get(aleatoire.nextInt(b2.getArticles().size()));

                if (!b2.getAbonne().isArticleCompatible(a1)
                        || !b1.getAbonne().isArticleCompatible(a2))
                    return false;

                double p1 = b1.getPoidsTotal() - a1.getPoids() + a2.getPoids();
                double p2 = b2.getPoidsTotal() - a2.getPoids() + a1.getPoids();
                if (p1 > poidsMax || p2 > poidsMax)
                    return false;

                b1.removeArticle(a1);
                b2.removeArticle(a2);
                b1.addArticle(a2);
                b2.addArticle(a1);
                return true;
            }
        }
        return false;
    }

    // =========================================================================
    // Utilitaires
    // =========================================================================

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
            points = EvaluateurScoreGlouton.pointsPourRang(rangPref + compte);
        }
        if (abonne.isArticleAdjacentSeulement(article) && points > 0) {
            points = Math.max(1, (int) Math.ceil(points / 2.0));
        }
        return points + article.getEtat().getBonus();
    }

    private Composition clonerComposition(Composition original) {
        Composition copie = new Composition();
        copie.setArticlesNonAffectes(new ArrayList<>(original.getArticlesNonAffectes()));
        for (Box box : original.getBoxes()) {
            Box copieBox = new Box(box.getAbonne());
            for (Article a : box.getArticles()) {
                copieBox.addArticle(a);
            }
            copie.ajouterBox(copieBox);
        }
        return copie;
    }
}
