package com.toysacademy.io;

import com.toysacademy.model.Article;
import com.toysacademy.model.Box;
import com.toysacademy.model.Composition;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class CSVManager {

    public void sauvegarderSolution(String cheminFichierSortie, Composition solution, double score) throws IOException {
        Path p = Paths.get(cheminFichierSortie);
        Path parent = p.getParent();
        if (parent != null) Files.createDirectories(parent);

        try (Writer w = Files.newBufferedWriter(p, StandardCharsets.UTF_8)) {
            writeSolution(w, solution, score);
        }
    }

    public String solutionToCsvString(Composition solution, double score) {
        StringWriter sw = new StringWriter();
        try {
            writeSolution(sw, solution, score);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return sw.toString();
    }

    private void writeSolution(Writer writer, Composition solution, double score) throws IOException {
        BufferedWriter bw = (writer instanceof BufferedWriter) ? (BufferedWriter) writer : new BufferedWriter(writer);

        // 1) Score en 1ère ligne
        bw.write(String.valueOf((int) score));
        bw.newLine();

        // 2) Lignes : PrenomAbonne;ArticleId;Categorie;Age;Etat
        for (Box box : solution.getBoxes()) {
            // --- récupérer le prénom de l'abonné ---
            // Ajustez ici si votre getter s'appelle différemment
            String prenom = box.getAbonne().getPrenom();

            for (Article a : box.getArticles()) {
                bw.write(prenom);
                bw.write(";");
                bw.write(a.getId());
                bw.write(";");
                bw.write(a.getCategorie().toString());
                bw.write(";");
                bw.write(a.getAge().toString());
                bw.write(";");
                bw.write(a.getEtat().toString());
                bw.newLine();
            }
        }

        bw.flush();
    }
}
