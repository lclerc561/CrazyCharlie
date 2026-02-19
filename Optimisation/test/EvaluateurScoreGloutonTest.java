import com.toysacademy.model.*;
import com.toysacademy.service.EvaluateurScoreGlouton;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests JUnit 5 pour EvaluateurScoreGlouton.
 *
 * Pré-requis Maven/Gradle :
 * - junit-jupiter-api
 * - junit-jupiter-engine
 *
 * NOTE: on suppose que State existe avec N/TB/B et getBonus().
 */
public class EvaluateurScoreGloutonTest {

    private final EvaluateurScoreGlouton eval = new EvaluateurScoreGlouton();
    private static final double POIDS_MAX = 1200.0;

    // Helper: abonné PE avec préférences standard
    private Abonne abonnePE(String id, String prenom) {
        return new Abonne(id, prenom, Age.PE,
                List.of(Category.SOC, Category.EVL, Category.FIG, Category.CON, Category.EXT, Category.LIV));
    }

    // Helper: abonné EN avec préférences standard
    private Abonne abonneEN(String id, String prenom) {
        return new Abonne(id, prenom, Age.EN,
                List.of(Category.CON, Category.EXT, Category.SOC, Category.EVL, Category.FIG, Category.LIV));
    }

    // Helper: article
    private Article art(String id, Category cat, Age age, State etat, int prix, double poids) {
        return new Article(id, id, cat, age, etat, prix, poids);
    }

    // Helper: composition avec N boxes
    private Composition comp(Box... boxes) {
        Composition c = new Composition();
        for (Box b : boxes) c.ajouterBox(b);
        return c;
    }

    // ---------------- TESTS ----------------

    @Test
    void score_est_fini_pour_solution_valide() {
        Abonne emma = abonnePE("s1", "Emma");
        Abonne lucas = abonnePE("s2", "Lucas");

        Box b1 = new Box(emma);
        b1.addArticle(art("a1", Category.SOC, Age.PE, State.N, 200, 300));
        b1.addArticle(art("a5", Category.EVL, Age.PE, State.TB, 150, 250));

        Box b2 = new Box(lucas);
        b2.addArticle(art("a3", Category.CON, Age.PE, State.B, 500, 400));
        b2.addArticle(art("a4", Category.EXT, Age.PE, State.N, 800, 350));

        double score = eval.evaluer(comp(b1, b2), POIDS_MAX);
        assertFalse(Double.isInfinite(score), "Le score ne doit pas être infini");
        assertFalse(Double.isNaN(score), "Le score ne doit pas être NaN");
    }

    @Test
    void depassement_poids_retourne_moins_infini() {
        Abonne emma = abonnePE("s1", "Emma");

        Box b1 = new Box(emma);
        b1.addArticle(art("a1", Category.SOC, Age.PE, State.N, 10, POIDS_MAX + 0.1)); // trop lourd

        double score = eval.evaluer(comp(b1), POIDS_MAX);
        assertEquals(Double.NEGATIVE_INFINITY, score, "Dépassement poids => -inf");
    }

    @Test
    void article_non_compatible_age_retourne_moins_infini() {
        Abonne noah = abonneEN("s1", "Noah"); // EN
        // BB n'est pas adjacent à EN => incompatible
        Article bb = art("a1", Category.CON, Age.BB, State.N, 10, 100);

        Box b = new Box(noah);
        b.addArticle(bb);

        double score = eval.evaluer(comp(b), POIDS_MAX);
        assertEquals(Double.NEGATIVE_INFINITY, score, "Âge non adjacent => -inf");
    }

    @Test
    void age_adjacent_est_autorise_et_score_est_fini() {
        Abonne noah = abonneEN("s1", "Noah"); // EN
        // PE est adjacent à EN => autorisé
        Article pe = art("a1", Category.CON, Age.PE, State.TB, 10, 100);

        Box b = new Box(noah);
        b.addArticle(pe);

        double score = eval.evaluer(comp(b), POIDS_MAX);
        assertFalse(Double.isInfinite(score), "Âge adjacent => score fini");
    }

    @Test
    void box_vide_applique_malus_moins_10() {
        Abonne emma = abonnePE("s1", "Emma");

        Box vide = new Box(emma); // pas d'articles
        double score = eval.evaluer(comp(vide), POIDS_MAX);

        assertEquals(-10.0, score, 1e-9, "Box vide => -10");
    }

    @Test
    void equite_applique_malus_si_ecart_au_moins_2() {
        Abonne a = abonnePE("s1", "A");
        Abonne b = abonnePE("s2", "B");
        Abonne c = abonnePE("s3", "C");

        // Box A : 3 articles
        Box boxA = new Box(a);
        boxA.addArticle(art("a1", Category.SOC, Age.PE, State.N, 10, 100));
        boxA.addArticle(art("a2", Category.EVL, Age.PE, State.N, 10, 100));
        boxA.addArticle(art("a3", Category.FIG, Age.PE, State.N, 10, 100));

        // Box C : 3 articles
        Box boxC = new Box(c);
        boxC.addArticle(art("c1", Category.SOC, Age.PE, State.N, 10, 100));
        boxC.addArticle(art("c2", Category.EVL, Age.PE, State.N, 10, 100));
        boxC.addArticle(art("c3", Category.FIG, Age.PE, State.N, 10, 100));

        // Cas 1 : boxB = 1 article => écart 2 => malus équité -10 sur B
        Box boxB1 = new Box(b);
        boxB1.addArticle(art("b1", Category.SOC, Age.PE, State.N, 10, 100));

        double scoreAvecMalus = eval.evaluer(comp(boxA, boxB1, boxC), POIDS_MAX);

        // Cas 2 : boxB = 2 articles => écart 1 => PAS de malus équité
        Box boxB2 = new Box(b);
        boxB2.addArticle(art("b1", Category.SOC, Age.PE, State.N, 10, 100));
        boxB2.addArticle(art("b2", Category.EVL, Age.PE, State.N, 10, 100));

        double scoreSansMalus = eval.evaluer(comp(boxA, boxB2, boxC), POIDS_MAX);

        // Ici, scoreSansMalus doit être >= scoreAvecMalus (car on a un article en plus ET pas de -10 équité)
        assertTrue(scoreSansMalus > scoreAvecMalus, "Sans malus équité doit être strictement meilleur");

        // Et surtout : on vérifie que le malus existe bien (au moins -10)
        // (différence minimale : scoreSansMalus - scoreAvecMalus >= 10 - gainArticleAjouté ?)
        // On évite les deltas exacts, on vérifie la logique : la config 3/1/3 est pénalisée.
        // Si vous tenez à un delta exact, il faut neutraliser le gain du 2e article (pas trivial avec préférences).
        assertTrue(scoreSansMalus - scoreAvecMalus >= 10.0,
                "La différence doit refléter au moins le -10 d'équité");
    }


    @Test
    void calibrage_prix_hors_fourchette_applique_malus_5() {
        Abonne emma = abonnePE("s1", "Emma");

        Box b = new Box(emma);
        // prix total = 10 (hors [50,100])
        b.addArticle(art("a1", Category.SOC, Age.PE, State.N, 10, 100));

        Composition c = comp(b);

        double scoreSansPrix = eval.evaluer(c, POIDS_MAX, 0, Integer.MAX_VALUE);
        double scoreAvecPrix = eval.evaluer(c, POIDS_MAX, 50, 100);

        assertEquals(scoreSansPrix - 5.0, scoreAvecPrix, 1e-9, "Hors fourchette prix => -5");
    }

    @Test
    void multi_enfants_applique_moins10_par_enfant_non_couvert_delta_exact() {
        // Même préférences
        List<Category> prefs = List.of(Category.CON, Category.SOC, Category.EVL, Category.FIG, Category.EXT, Category.LIV);

        // Abonné mono-enfant PE
        Abonne mono = new Abonne("s1", "Mono", Age.PE, prefs);

        // Abonné multi-enfants PE + AD
        Abonne multi = new Abonne("s2", "Multi", List.of(Age.PE, Age.AD), prefs);

        // Même contenu d'articles dans les deux boxes
        Article a1 = art("a1", Category.CON, Age.PE, State.N, 10, 100);
        Article a2 = art("a2", Category.SOC, Age.PE, State.N, 10, 100);

        Box boxMono = new Box(mono);
        boxMono.addArticle(a1);
        boxMono.addArticle(a2);

        Box boxMulti = new Box(multi);
        boxMulti.addArticle(a1);
        boxMulti.addArticle(a2);

        double scoreMono = eval.evaluer(comp(boxMono), POIDS_MAX);
        double scoreMulti = eval.evaluer(comp(boxMulti), POIDS_MAX);

        // AD n'est pas couvert (articles PE seulement, PE n'est pas compatible avec AD)
        // => pénalité -10 uniquement dans le cas multi
        assertEquals(scoreMono - 10.0, scoreMulti, 1e-9,
                "Multi-enfants: enfant AD non couvert => -10 exactement");
    }


    @Test
    void penalite_adjacent_reduit_points_preference() {
        // On fabrique deux compositions identiques sauf l'âge de l'article (exact vs adjacent)
        Abonne noah = abonneEN("s1", "Noah"); // préférences: CON en rang 1

        Article exactEN = art("a1", Category.CON, Age.EN, State.N, 10, 100);
        Article adjPE = art("a2", Category.CON, Age.PE, State.N, 10, 100); // adjacent => pref /2

        Box bExact = new Box(noah);
        bExact.addArticle(exactEN);

        Box bAdj = new Box(noah);
        bAdj.addArticle(adjPE);

        double scoreExact = eval.evaluer(comp(bExact), POIDS_MAX);
        double scoreAdj = eval.evaluer(comp(bAdj), POIDS_MAX);

        assertTrue(scoreAdj < scoreExact, "Adjacent doit donner moins que exact (préférence /2)");
    }
}
