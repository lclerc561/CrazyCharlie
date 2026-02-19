package com.toysacademy.model;

/**
 * Représente un article (jouet, jeu, livre) issu de dons.
 * Chaque article est unique et peut être affecté à une seule box.
 */
public class Article {
    private String identifiant;
    private String designation;
    private Category categorie;
    private Age trancheAge;
    private State etat;
    private int prix;   // en euros
    private double poids; // en grammes

    public Article(String identifiant, String designation, Category categorie,
                   Age trancheAge, State etat, int prix, double poids) {
        this.identifiant = identifiant;
        this.designation = designation;
        this.categorie = categorie;
        this.trancheAge = trancheAge;
        this.etat = etat;
        this.prix = prix;
        this.poids = poids;
    }

    /** Constructeur simplifié (sans désignation ni prix, pour compatibilité). */
    public Article(String identifiant, Category categorie, Age trancheAge,
                   double poids, State etat) {
        this(identifiant, "", categorie, trancheAge, etat, 0, poids);
    }

    // --- Accesseurs ---

    public String getIdentifiant() { return identifiant; }
    public String getDesignation() { return designation; }
    public Category getCategorie() { return categorie; }
    public Age getTrancheAge() { return trancheAge; }
    public State getEtat() { return etat; }
    public int getPrix() { return prix; }
    public double getPoids() { return poids; }

    // Compatibilité avec l'ancien code
    public String getId() { return identifiant; }
    public Category getCategory() { return categorie; }
    public Age getAge() { return trancheAge; }
    public double getWeight() { return poids; }
    public State getState() { return etat; }

    @Override
    public String toString() {
        return "Article{" +
                "id='" + identifiant + '\'' +
                ", categorie=" + categorie +
                ", age=" + trancheAge +
                ", poids=" + poids +
                ", etat=" + etat +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return identifiant.equals(article.identifiant);
    }

    @Override
    public int hashCode() {
        return identifiant.hashCode();
    }
}
