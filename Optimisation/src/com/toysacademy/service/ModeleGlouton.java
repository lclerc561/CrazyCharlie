package com.toysacademy.service;

import com.toysacademy.model.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Modèle Glouton (Basique / Strict)
 * Version dégradée de Modele2Optimise sans les améliorations :
 * - Pas d'âge adjacent
 * - Pas de calibrage prix
 * - Pas d'optimisation multi-enfants spécifique
 */
public class ModeleGlouton implements Solveur {

    private final EvaluateurScoreGlouton evaluateur = new EvaluateurScoreGlouton();
    private final Random aleatoire = new Random(42);

    // Taux d'apprentissage initial (Learning Rate).
    // Valeur élevée (100.0) pour permettre une exploration globale de l'espace des
    // solutions au début (haute "température").
    // Permet d'accepter des dégradations temporaires pour sortir des optimums
    // locaux.
    private static final double TAUX_APPRENTISSAGE_INITIAL = 100.0;

    // Facteur d'oubli (Decay Rate).
    // Contrôle la vitesse de réduction du taux d'apprentissage.
    // 0.9995 = Décroissance lente pour une convergence stable.
    private static final double FACTEUR_OUBLI = 0.9995;

    // Nombre d'époques (Epochs) ou itérations d'optimisation.
    private static final int MAX_EPOCHS = 50_000;

    @Override
    public String getNom() {
        return "ModeleGlouton";
    }

    @Override
    public Composition resoudre(List<Abonne> abonnes, List<Article> articles,
                                double poidsMax, int prixMin, int prixMax) {
        // Ignorer prixMin et prixMax
        return resoudre(abonnes, articles, poidsMax);
    }

    @Override
    public Composition resoudre(List<Abonne> abonnes, List<Article> articles, double poidsMax) {
        // Pré-calculer l'index par tranche d'âge (EXACT uniquement)
        Map<Age, List<Article>> indexParAge = articles.stream()
                .collect(Collectors.groupingBy(Article::getTrancheAge));

        // Phase 1 : Construction gloutonne interactive (STRICT)
        Composition solution = gloutonInteractif(abonnes, indexParAge, articles, poidsMax);

        // Phase 2 : Optimisation par Descente de Gradient Stochastique (Simulé)
        solution = optimisationStochastique(solution, abonnes, poidsMax);

        return solution;
    }

    /**
     * Retourne les articles compatibles pour un abonné (EXACT uniquement).
     */
    private List<Article> getArticlesCompatibles(Abonne abonne, Map<Age, List<Article>> indexParAge) {
        List<Article> result = new ArrayList<>();
        for (Age ageEnfant : abonne.getTrancheAgesEnfants()) {
            // Articles exacts uniquement
            result.addAll(indexParAge.getOrDefault(ageEnfant, Collections.emptyList()));
        }
        // Dédupliquer
        return new ArrayList<>(new LinkedHashSet<>(result));
    }

    private Composition gloutonInteractif(List<Abonne> abonnes,
            Map<Age, List<Article>> indexParAge,
            List<Article> allArticles,
            double poidsMax) {
        Composition composition = new Composition();
        for (Abonne abonne : abonnes) {
            composition.ajouterBox(new Box(abonne));
        }

        Set<Article> disponibles = new HashSet<>(allArticles);

        // Pré-calculer les articles compatibles par box (STRICT)
        Map<Box, List<Article>> compatiblesParBox = new HashMap<>();
        for (Box box : composition.getBoxes()) {
            compatiblesParBox.put(box, getArticlesCompatibles(box.getAbonne(), indexParAge));
        }

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

                    // STRICT: Vérification explicite supplémentaire
                    if (!box.getAbonne().isArticleExact(article))
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

        // Pas de pénalité adjacent car adjacent interdit

        return points + article.getEtat().getBonus();
    }

    // =========================================================================
    // Phase 2 : Optimisation Stochastique (Inspiré IA)
    // =========================================================================

    private Composition optimisationStochastique(Composition solutionInitiale,
            List<Abonne> abonnes, double poidsMax) {
        Composition solutionActuelle = clonerComposition(solutionInitiale);
        double scoreActuel = evaluateur.evaluer(solutionActuelle, poidsMax);

        Composition meilleureSolution = clonerComposition(solutionActuelle);
        double meilleurScore = scoreActuel;

        double learningRate = TAUX_APPRENTISSAGE_INITIAL;

        for (int epoch = 0; epoch < MAX_EPOCHS; epoch++) {
            if (learningRate < 0.001)
                break;

            Composition voisin = clonerComposition(solutionActuelle);
            boolean mutationReussie = effectuerMutation(voisin, abonnes, poidsMax);

            if (!mutationReussie)
                continue;

            double scoreVoisin = evaluateur.evaluer(voisin, poidsMax);

            if (Double.isInfinite(scoreVoisin) && scoreVoisin < 0)
                continue;

            double gradient = scoreVoisin - scoreActuel;

            // Critère d'acceptation (Metropolis adapté)
            if (gradient > 0 || aleatoire.nextDouble() < Math.exp(gradient / learningRate)) {
                solutionActuelle = voisin;
                scoreActuel = scoreVoisin;

                if (scoreActuel > meilleurScore) {
                    meilleureSolution = clonerComposition(solutionActuelle);
                    meilleurScore = scoreActuel;
                }
            }

            learningRate *= FACTEUR_OUBLI;
        }

        return meilleureSolution;
    }

    private boolean effectuerMutation(Composition comp, List<Abonne> abonnes,
            double poidsMax) {
        int typeMouvement = aleatoire.nextInt(4);
        List<Box> boxes = comp.getBoxes();
        List<Article> nonAffectes = comp.getArticlesNonAffectes();

        if (boxes.isEmpty())
            return false;

        switch (typeMouvement) {
            case 0: { // Retrait Box → Pool
                Box box = boxes.get(aleatoire.nextInt(boxes.size()));
                if (box.getArticles().isEmpty())
                    return false;
                Article a = box.getArticles().get(aleatoire.nextInt(box.getArticles().size()));
                box.removeArticle(a);
                nonAffectes.add(a);
                return true;
            }

            case 1: { // Ajout Pool → Box (STRICT)
                if (nonAffectes.isEmpty())
                    return false;
                Article article = nonAffectes.get(aleatoire.nextInt(nonAffectes.size()));
                List<Box> boxesCompatibles = new ArrayList<>();
                for (Box box : boxes) {
                    if (box.getAbonne().isArticleExact(article) // STRICT
                            && box.getPoidsTotal() + article.getPoids() <= poidsMax) {
                        boxesCompatibles.add(box);
                    }
                }
                if (boxesCompatibles.isEmpty())
                    return false;
                Box cible = boxesCompatibles.get(aleatoire.nextInt(boxesCompatibles.size()));
                nonAffectes.remove(article);
                cible.addArticle(article);
                return true;
            }

            case 2: { // Transfert Box → Box (STRICT)
                if (boxes.size() < 2)
                    return false;
                Box source = boxes.get(aleatoire.nextInt(boxes.size()));
                if (source.getArticles().isEmpty())
                    return false;
                Article article = source.getArticles()
                        .get(aleatoire.nextInt(source.getArticles().size()));
                List<Box> destinations = new ArrayList<>();
                for (Box box : boxes) {
                    if (box != source
                            && box.getAbonne().isArticleExact(article) // STRICT
                            && box.getPoidsTotal() + article.getPoids() <= poidsMax) {
                        destinations.add(box);
                    }
                }
                if (destinations.isEmpty())
                    return false;
                Box dest = destinations.get(aleatoire.nextInt(destinations.size()));
                source.removeArticle(article);
                dest.addArticle(article);
                return true;
            }

            case 3: { // Échange Box ↔ Box (STRICT)
                if (boxes.size() < 2)
                    return false;
                Box b1 = boxes.get(aleatoire.nextInt(boxes.size()));
                Box b2 = boxes.get(aleatoire.nextInt(boxes.size()));
                if (b1 == b2 || b1.getArticles().isEmpty() || b2.getArticles().isEmpty())
                    return false;
                Article a1 = b1.getArticles().get(aleatoire.nextInt(b1.getArticles().size()));
                Article a2 = b2.getArticles().get(aleatoire.nextInt(b2.getArticles().size()));

                // STRICT checks
                if (!b2.getAbonne().isArticleExact(a1)
                        || !b1.getAbonne().isArticleExact(a2))
                    return false;

                double poids1Apres = b1.getPoidsTotal() - a1.getPoids() + a2.getPoids();
                double poids2Apres = b2.getPoidsTotal() - a2.getPoids() + a1.getPoids();
                if (poids1Apres > poidsMax || poids2Apres > poidsMax)
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
