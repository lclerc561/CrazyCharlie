package com.toysacademy.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente une composition complète de box pour une campagne.
 * Contient les box affectées et les articles non utilisés.
 */
public class Composition {
    private List<Box> boxes;
    private List<Article> articlesNonAffectes;

    public Composition() {
        this.boxes = new ArrayList<>();
        this.articlesNonAffectes = new ArrayList<>();
    }

    public void ajouterBox(Box box) {
        this.boxes.add(box);
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public void setArticlesNonAffectes(List<Article> articlesNonAffectes) {
        this.articlesNonAffectes = articlesNonAffectes;
    }

    public List<Article> getArticlesNonAffectes() {
        return articlesNonAffectes;
    }

    // Alias de compatibilité
    public void addBox(Box box) {
        ajouterBox(box);
    }

    public void setUnassignedArticles(List<Article> u) {
        setArticlesNonAffectes(u);
    }

    public List<Article> getUnassignedArticles() {
        return getArticlesNonAffectes();
    }

    /** Retourne le nombre total d'articles affectés dans toutes les box. */
    public int getNombreArticlesAffectes() {
        return boxes.stream().mapToInt(Box::getNombreArticles).sum();
    }
}
