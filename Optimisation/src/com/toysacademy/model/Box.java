package com.toysacademy.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente une box destinée à un abonné.
 * Contient la liste des articles affectés, le poids total et le prix total.
 */
public class Box {
    private Abonne abonne;
    private List<Article> articles;
    private double poidsTotal; // Cache pour éviter de re-sommer à chaque accès
    private int prixTotal; // Cache du prix total en euros

    public Box(Abonne abonne) {
        this.abonne = abonne;
        this.articles = new ArrayList<>();
        this.poidsTotal = 0.0;
        this.prixTotal = 0;
    }

    /** Ajoute un article à la box et met à jour les caches. */
    public void addArticle(Article article) {
        this.articles.add(article);
        this.poidsTotal += article.getPoids();
        this.prixTotal += article.getPrix();
    }

    /** Retire un article de la box et met à jour les caches. */
    public void removeArticle(Article article) {
        if (this.articles.remove(article)) {
            this.poidsTotal -= article.getPoids();
            this.prixTotal -= article.getPrix();
        }
    }

    public Abonne getAbonne() {
        return abonne;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public int getNombreArticles() {
        return articles.size();
    }

    /** Retourne le poids total (depuis le cache interne). */
    public double getPoidsTotal() {
        return poidsTotal;
    }

    /** Retourne le prix total en euros (depuis le cache interne). */
    public int getPrixTotal() {
        return prixTotal;
    }

    /** Alias de compatibilité. */
    public double getTotalWeight() {
        return poidsTotal;
    }

    public int getArticleCount() {
        return articles.size();
    }
}
