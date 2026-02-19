package com.toysacademy.service;

import com.toysacademy.model.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Modèle Génétique — Algorithme évolutionnaire.
 *
 * Fait évoluer une population de solutions candidates via :
 * - Sélection par tournoi
 * - Croisement (échange de blocs d'affectation)
 * - Mutations (retrait, ajout, transfert, échange)
 * - Élitisme (conservation des meilleurs)
 *
 * @see <a href=
 *      "https://fr.wikipedia.org/wiki/Algorithme_évolutionniste">Algorithme
 *      évolutionniste</a>
 */
public class ModeleGenetique implements Algo {

    private final EvaluateurScoreGlouton evaluateur = new EvaluateurScoreGlouton();
    private final Random aleatoire = new Random(42);

    // Paramètres de l'algorithme génétique
    private static final int TAILLE_POPULATION = 30;
    private static final int NOMBRE_GENERATIONS = 200;
    private static final double TAUX_MUTATION = 0.15;
    private static final double TAUX_CROISEMENT = 0.7;
    private static final int TAILLE_TOURNOI = 3;
    private static final int ELITISME = 2;
    private static final int MUTATIONS_PAR_INDIVIDU = 3;

    private int prixMin = 0;
    private int prixMax = Integer.MAX_VALUE;

    @Override
    public String getNom() {
        return "Genetique";
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

        // Étape 1 : Générer la population initiale (variantes gloutonnes)
        List<Individu> population = genererPopulationInitiale(
                abonnes, indexParAge, articles, poidsMax);

        // Étape 2 : Évolution
        for (int generation = 0; generation < NOMBRE_GENERATIONS; generation++) {
            List<Individu> nouvellePopulation = new ArrayList<>();

            // Élitisme : conserver les meilleurs
            population.sort((a, b) -> Double.compare(b.score, a.score));
            for (int i = 0; i < ELITISME && i < population.size(); i++) {
                nouvellePopulation.add(population.get(i));
            }

            // Remplir le reste de la population
            while (nouvellePopulation.size() < TAILLE_POPULATION) {
                Individu parent1 = selectionTournoi(population);
                Individu parent2 = selectionTournoi(population);

                Individu enfant;
                if (aleatoire.nextDouble() < TAUX_CROISEMENT) {
                    enfant = croisement(parent1, parent2, abonnes, articles, poidsMax);
                } else {
                    // Copie du meilleur parent
                    enfant = parent1.score >= parent2.score
                            ? new Individu(clonerComposition(parent1.composition), parent1.score)
                            : new Individu(clonerComposition(parent2.composition), parent2.score);
                }

                // Mutation
                if (aleatoire.nextDouble() < TAUX_MUTATION) {
                    for (int m = 0; m < MUTATIONS_PAR_INDIVIDU; m++) {
                        effectuerMutation(enfant.composition, poidsMax);
                    }
                    enfant.score = evaluateur.evaluer(enfant.composition, poidsMax, prixMin, prixMax);
                }

                if (!Double.isInfinite(enfant.score) || enfant.score > 0) {
                    nouvellePopulation.add(enfant);
                }
            }

            population = nouvellePopulation;
        }

        // Retourner le meilleur individu
        population.sort((a, b) -> Double.compare(b.score, a.score));
        return population.get(0).composition;
    }

    // =========================================================================
    // Population initiale : variantes gloutonnes
    // =========================================================================

    private List<Individu> genererPopulationInitiale(List<Abonne> abonnes,
            Map<Age, List<Article>> indexParAge,
            List<Article> articles, double poidsMax) {
        List<Individu> population = new ArrayList<>();

        // Solution gloutonne déterministe
        Composition glouton = construireGlouton(abonnes, indexParAge, articles, poidsMax, false);
        double score = evaluateur.evaluer(glouton, poidsMax, prixMin, prixMax);
        population.add(new Individu(glouton, score));

        // Variantes avec perturbation
        for (int i = 1; i < TAILLE_POPULATION; i++) {
            Composition variante = construireGlouton(abonnes, indexParAge, articles, poidsMax, true);
            // Appliquer quelques mutations pour diversifier
            for (int m = 0; m < i % 5 + 1; m++) {
                effectuerMutation(variante, poidsMax);
            }
            double scoreVar = evaluateur.evaluer(variante, poidsMax, prixMin, prixMax);
            if (!Double.isInfinite(scoreVar) || scoreVar > 0) {
                population.add(new Individu(variante, scoreVar));
            } else {
                // Fallback : copie de la solution gloutonne mutée
                Composition copie = clonerComposition(glouton);
                effectuerMutation(copie, poidsMax);
                double scoreCopie = evaluateur.evaluer(copie, poidsMax, prixMin, prixMax);
                population.add(new Individu(copie, scoreCopie));
            }
        }

        return population;
    }

    private Composition construireGlouton(List<Abonne> abonnes,
            Map<Age, List<Article>> indexParAge,
            List<Article> allArticles, double poidsMax, boolean perturbe) {
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

                    // Perturbation aléatoire pour créer de la diversité
                    if (perturbe) {
                        gain *= (0.5 + aleatoire.nextDouble());
                    }

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
    // Opérateurs génétiques
    // =========================================================================

    /**
     * Sélection par tournoi : choisit le meilleur parmi k individus tirés au sort.
     */
    private Individu selectionTournoi(List<Individu> population) {
        Individu meilleur = null;
        for (int i = 0; i < TAILLE_TOURNOI; i++) {
            Individu candidat = population.get(aleatoire.nextInt(population.size()));
            if (meilleur == null || candidat.score > meilleur.score) {
                meilleur = candidat;
            }
        }
        return meilleur;
    }

    /**
     * Croisement : échange l'affectation de certaines boxes entre deux parents.
     * Produit un enfant qui hérite des meilleures boxes de chaque parent.
     */
    private Individu croisement(Individu parent1, Individu parent2,
            List<Abonne> abonnes, List<Article> allArticles, double poidsMax) {
        Composition enfant = new Composition();
        Set<Article> utilises = new HashSet<>();
        List<Box> boxes1 = parent1.composition.getBoxes();
        List<Box> boxes2 = parent2.composition.getBoxes();

        for (int i = 0; i < abonnes.size(); i++) {
            Box boxParent;
            if (aleatoire.nextBoolean()) {
                boxParent = (i < boxes1.size()) ? boxes1.get(i) : boxes2.get(i);
            } else {
                boxParent = (i < boxes2.size()) ? boxes2.get(i) : boxes1.get(i);
            }

            Box nouvelleBox = new Box(abonnes.get(i));
            enfant.ajouterBox(nouvelleBox);

            // Copier les articles du parent choisi (si pas déjà utilisés)
            for (Article article : boxParent.getArticles()) {
                if (!utilises.contains(article)
                        && nouvelleBox.getAbonne().isArticleCompatible(article)
                        && nouvelleBox.getPoidsTotal() + article.getPoids() <= poidsMax) {
                    nouvelleBox.addArticle(article);
                    utilises.add(article);
                }
            }
        }

        // Articles non affectés
        List<Article> nonAffectes = new ArrayList<>();
        for (Article article : allArticles) {
            if (!utilises.contains(article)) {
                nonAffectes.add(article);
            }
        }
        enfant.setArticlesNonAffectes(nonAffectes);

        double score = evaluateur.evaluer(enfant, poidsMax, prixMin, prixMax);
        return new Individu(enfant, score);
    }

    // =========================================================================
    // Mutations (4 types, identiques aux autres modèles)
    // =========================================================================

    private boolean effectuerMutation(Composition comp, double poidsMax) {
        int type = aleatoire.nextInt(4);
        List<Box> boxes = comp.getBoxes();
        List<Article> pool = comp.getArticlesNonAffectes();

        if (boxes.isEmpty())
            return false;

        switch (type) {
            case 0: {
                Box box = boxes.get(aleatoire.nextInt(boxes.size()));
                if (box.getArticles().isEmpty())
                    return false;
                Article a = box.getArticles().get(aleatoire.nextInt(box.getArticles().size()));
                box.removeArticle(a);
                pool.add(a);
                return true;
            }
            case 1: {
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
            case 2: {
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
            case 3: {
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

    /** Encapsule une solution et son score (individu de la population). */
    private static class Individu {
        Composition composition;
        double score;

        Individu(Composition composition, double score) {
            this.composition = composition;
            this.score = score;
        }
    }
}
