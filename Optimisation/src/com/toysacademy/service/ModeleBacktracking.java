package com.toysacademy.service;

import com.toysacademy.model.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Modèle Branch & Bound — Exploration arborescente avec élagage.
 *
 * Utilise un backtracking avec borne supérieure pour couper les branches
 * non prometteuses. Commence par une solution gloutonne pour avoir
 * une bonne borne inférieure initiale.
 *
 * Limité en temps pour éviter l'explosion combinatoire.
 *
 * @see <a href="https://fr.wikipedia.org/wiki/Séparation_et_évaluation">Branch
 *      & Bound</a>
 */
public class ModeleBacktracking implements Algo {

    private final EvaluateurScoreGlouton evaluateur = new EvaluateurScoreGlouton();

    // Limite de temps en millisecondes
    private static final long TIMEOUT_MS = 10_000;

    private int prixMin = 0;
    private int prixMax = Integer.MAX_VALUE;

    // État global de la recherche
    private Composition meilleureSolution;
    private double meilleurScore;
    private long debutRecherche;

    @Override
    public String getNom() {
        return "Backtracking";
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

        // Solution initiale gloutonne (borne inférieure)
        Composition solutionGloutonne = construireGlouton(abonnes, indexParAge, articles, poidsMax);
        meilleureSolution = clonerComposition(solutionGloutonne);
        meilleurScore = evaluateur.evaluer(meilleureSolution, poidsMax, prixMin, prixMax);

        // Pré-calculer les articles triés par valeur décroissante par box
        Map<Integer, List<ArticleValue>> articlesParBox = new HashMap<>();
        List<Box> boxes = solutionGloutonne.getBoxes();

        for (int i = 0; i < boxes.size(); i++) {
            List<ArticleValue> valeurs = new ArrayList<>();
            Abonne abonne = boxes.get(i).getAbonne();
            for (Article article : articles) {
                if (abonne.isArticleCompatible(article) && article.getPoids() <= poidsMax) {
                    double val = estimerValeur(article, abonne);
                    valeurs.add(new ArticleValue(article, val));
                }
            }
            valeurs.sort((a, b) -> Double.compare(b.valeur, a.valeur));
            articlesParBox.put(i, valeurs);
        }

        // Lancer le Branch & Bound
        debutRecherche = System.currentTimeMillis();

        Composition solutionVide = new Composition();
        for (Abonne abonne : abonnes) {
            solutionVide.ajouterBox(new Box(abonne));
        }
        Set<Article> disponibles = new HashSet<>(articles);

        branchAndBound(solutionVide, disponibles, articlesParBox, poidsMax, 0);

        return meilleureSolution;
    }

    /**
     * Exploration récursive Branch & Bound box par box.
     * Pour chaque box, essaie d'affecter les meilleurs articles compatibles.
     */
    private void branchAndBound(Composition solution, Set<Article> disponibles,
            Map<Integer, List<ArticleValue>> articlesParBox,
            double poidsMax, int boxIndex) {

        // Vérification timeout
        if (System.currentTimeMillis() - debutRecherche > TIMEOUT_MS)
            return;

        List<Box> boxes = solution.getBoxes();

        // Si toutes les boxes ont été traitées, évaluer
        if (boxIndex >= boxes.size()) {
            solution.setArticlesNonAffectes(new ArrayList<>(disponibles));
            double score = evaluateur.evaluer(solution, poidsMax, prixMin, prixMax);
            if (score > meilleurScore) {
                meilleureSolution = clonerComposition(solution);
                meilleureSolution.setArticlesNonAffectes(new ArrayList<>(disponibles));
                meilleurScore = score;
            }

            return;
        }

        Box boxCourante = boxes.get(boxIndex);
        List<ArticleValue> candidats = articlesParBox.get(boxIndex);

        // Borne supérieure : score actuel + somme des meilleures valeurs possibles
        // pour les boxes restantes
        double borneSuperieure = calculerBorneSuperieure(solution, disponibles,
                articlesParBox, poidsMax, boxIndex);

        if (borneSuperieure <= meilleurScore)
            return; // Élagage

        // Branche 1 : Essayer d'ajouter des articles à cette box
        List<Article> articlesAjoutables = new ArrayList<>();
        for (ArticleValue av : candidats) {
            if (!disponibles.contains(av.article))
                continue;
            if (boxCourante.getPoidsTotal() + av.article.getPoids() > poidsMax)
                continue;
            articlesAjoutables.add(av.article);
            if (articlesAjoutables.size() >= 8)
                break; // Limiter la largeur
        }

        // Explorer les combinaisons (sous-ensembles limités)
        // On utilise une approche "ajouter un par un" pour limiter la complexité
        for (int i = 0; i < articlesAjoutables.size() && !timeout(); i++) {
            Article article = articlesAjoutables.get(i);
            if (boxCourante.getPoidsTotal() + article.getPoids() > poidsMax)
                continue;

            // Brancher : ajouter l'article
            boxCourante.addArticle(article);
            disponibles.remove(article);

            // Continuer avec la même box (essayer d'en ajouter d'autres)
            branchAndBound(solution, disponibles, articlesParBox, poidsMax, boxIndex);

            // Aussi essayer de passer à la box suivante avec cet article ajouté
            branchAndBound(solution, disponibles, articlesParBox, poidsMax, boxIndex + 1);

            // Backtrack
            boxCourante.removeArticle(article);
            disponibles.add(article);
        }

        // Branche 2 : Passer à la box suivante sans rien ajouter de plus
        branchAndBound(solution, disponibles, articlesParBox, poidsMax, boxIndex + 1);
    }

    private boolean timeout() {
        return System.currentTimeMillis() - debutRecherche > TIMEOUT_MS;
    }

    private double calculerBorneSuperieure(Composition solution, Set<Article> disponibles,
            Map<Integer, List<ArticleValue>> articlesParBox,
            double poidsMax, int boxIndex) {
        // Score actuel des boxes déjà remplies
        double scoreActuel = 0;
        List<Box> boxes = solution.getBoxes();
        for (int i = 0; i < boxIndex; i++) {
            double s = evaluateur.evaluerBox(boxes.get(i), poidsMax, prixMin, prixMax);
            if (Double.isInfinite(s) && s < 0)
                return Double.NEGATIVE_INFINITY;
            scoreActuel += s;
        }

        // Borne optimiste pour les boxes restantes
        for (int i = boxIndex; i < boxes.size(); i++) {
            double poidsDispo = poidsMax - boxes.get(i).getPoidsTotal();
            double scoreOptimiste = 0;
            List<ArticleValue> candidats = articlesParBox.get(i);
            if (candidats != null) {
                for (ArticleValue av : candidats) {
                    if (!disponibles.contains(av.article))
                        continue;
                    if (av.article.getPoids() <= poidsDispo) {
                        scoreOptimiste += av.valeur;
                        poidsDispo -= av.article.getPoids();
                    }
                    if (poidsDispo <= 0)
                        break;
                }
            }
            // Ajouter score des articles déjà dans la box
            for (Article a : boxes.get(i).getArticles()) {
                scoreOptimiste += estimerValeur(a, boxes.get(i).getAbonne());
            }
            scoreActuel += scoreOptimiste;
        }

        return scoreActuel;
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

    // =========================================================================
    // Construction gloutonne (identique aux autres modèles)
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

    /** Structure interne pour trier les articles par valeur estimée. */
    private static class ArticleValue {
        final Article article;
        final double valeur;

        ArticleValue(Article article, double valeur) {
            this.article = article;
            this.valeur = valeur;
        }
    }
}
