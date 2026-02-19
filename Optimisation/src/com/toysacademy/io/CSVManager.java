package com.toysacademy.io;

import com.toysacademy.model.Composition;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class CSVManager {

    /**
     * Sauvegarde la solution sur disque (comportement existant).
     */
    public void sauvegarderSolution(String cheminFichierSortie, Composition solution, double score) throws IOException {
        Path p = Paths.get(cheminFichierSortie);
        Files.createDirectories(p.getParent());

        try (Writer w = Files.newBufferedWriter(p, StandardCharsets.UTF_8)) {
            writeSolution(w, solution, score);
        }
    }

    /**
     * Renvoie le CSV de la solution sous forme de String (pour réponse HTTP).
     * Format IDENTIQUE à sauvegarderSolution car on réutilise writeSolution.
     */
    public String solutionToCsvString(Composition solution, double score) {
        StringWriter sw = new StringWriter();
        try {
            writeSolution(sw, solution, score);
        } catch (IOException e) {
            // StringWriter ne jette normalement pas IOException,
            // mais on garde la signature propre.
            throw new UncheckedIOException(e);
        }
        return sw.toString();
    }

    /**
     * Méthode commune : écrit la solution dans un Writer.
     * ==> C'est LA source unique du format CSV.
     */
    private void writeSolution(Writer writer, Composition solution, double score) throws IOException {
        BufferedWriter bw = (writer instanceof BufferedWriter) ? (BufferedWriter) writer : new BufferedWriter(writer);

        // 1) Première ligne : score entier (comme vous le faites déjà)
        bw.write(String.valueOf((int) score));
        bw.newLine();

        // 2) Lignes suivantes : affectations
        // ------------------------------------------------------------------
        // ADAPTEZ ICI : selon votre modèle Composition
        //
        // L'énoncé montre un format du genre:
        // AbonnePrenom;ArticleId;Categorie;Age;Etat
        //
        // Vous devez itérer sur vos affectations.
        //
        // Exemple A (si vous avez une liste d'objets affectation):
        // for (Affectation aff : solution.getAffectations()) { ... }
        //
        // Exemple B (si Composition stocke un mapping abonné->liste d'articles):
        // for (Abonne ab : solution.getAbonnes()) { for (Article a : solution.getArticlesPour(ab)) ...}
        //
        // IMPORTANT : gardez exactement le même ordre/format que votre CSV actuel.
        // ------------------------------------------------------------------

        // ======== EXEMPLE GENERIQUE (à adapter) ========
        // Supposons:
        // - solution.getLignesCsv() renvoie déjà les lignes au bon format (si vous avez ça)
        // for (String line : solution.getLignesCsv()) {
        //     bw.write(line);
        //     bw.newLine();
        // }

        // ======== ADAPTATION COURANTE (mapping abonné -> articles) ========
        // Si votre Composition a une méthode genre: solution.getAffectations()
        // et que chaque affectation a: abonneNom/prenom, articleId, categorie, age, etat
        //
        // for (var aff : solution.getAffectations()) {
        //     bw.write(aff.getAbonnePrenom() + ";" +
        //              aff.getArticleId() + ";" +
        //              aff.getCategorie() + ";" +
        //              aff.getAge() + ";" +
        //              aff.getEtat());
        //     bw.newLine();
        // }

        // >>> À VOUS de remplacer ce bloc par votre itération réelle. <<<
        throw new IllegalStateException(
                "CSVManager.writeSolution(): adaptez la partie 'AFFECTATIONS' à votre structure Composition."
        );

        // bw.flush(); // flush en fin si vous retirez l'exception
    }
}
