package com.toysacademy.model;

import java.util.Collections;
import java.util.List;

/**
 * Représente un abonné au main.java.com.toysacademy.service de toy box.
 * Un abonné peut avoir plusieurs enfants d'âges différents (box multi-enfants).
 * Il ordonne les six catégories de jouets par préférence.
 */
public class Abonne {
    private String identifiant;
    private String prenom;
    private List<Age> tranchesAgeEnfants; // Peut contenir 1 ou plusieurs tranches
    private List<Category> preferences; // Ordonnées de la plus à la moins souhaitée

    public Abonne(String identifiant, String prenom, List<Age> tranchesAgeEnfants,
            List<Category> preferences) {
        this.identifiant = identifiant;
        this.prenom = prenom;
        this.tranchesAgeEnfants = tranchesAgeEnfants;
        this.preferences = preferences;
    }

    /** Constructeur mono-enfant (compatibilité arrière). */
    public Abonne(String identifiant, String prenom, Age trancheAgeEnfant,
            List<Category> preferences) {
        this(identifiant, prenom, Collections.singletonList(trancheAgeEnfant), preferences);
    }

    /** Constructeur simplifié sans prénom, mono-enfant (compatibilité arrière). */
    public Abonne(String identifiant, Age trancheAgeEnfant, List<Category> preferences) {
        this(identifiant, identifiant, trancheAgeEnfant, preferences);
    }

    // --- Accesseurs ---

    public String getIdentifiant() {
        return identifiant;
    }

    public String getPrenom() {
        return prenom;
    }

    /** Retourne la liste de toutes les tranches d'âge des enfants. */
    public List<Age> getTrancheAgesEnfants() {
        return tranchesAgeEnfants;
    }

    /** Retourne la tranche d'âge principale (premier enfant). Compat arrière. */
    public Age getTrancheAgeEnfant() {
        return tranchesAgeEnfants.get(0);
    }

    /** Retourne true si l'abonné a plusieurs enfants d'âges différents. */
    public boolean isMultiEnfants() {
        return tranchesAgeEnfants.size() > 1;
    }

    public List<Category> getPreferences() {
        return preferences;
    }

    // Compatibilité avec l'ancien code
    public String getId() {
        return identifiant;
    }

    public Age getChildAge() {
        return getTrancheAgeEnfant();
    }

    /**
     * Vérifie si un article est compatible (exact ou adjacent) avec au moins
     * un des enfants de cet abonné.
     */
    public boolean isArticleCompatible(Article article) {
        for (Age ageEnfant : tranchesAgeEnfants) {
            if (article.getTrancheAge().isCompatible(ageEnfant)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si un article est exactement compatible avec au moins
     * un des enfants de cet abonné (même tranche d'âge).
     */
    public boolean isArticleExact(Article article) {
        for (Age ageEnfant : tranchesAgeEnfants) {
            if (article.getTrancheAge().isExact(ageEnfant)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si un article n'est que adjacent (pas exact) avec les enfants.
     */
    public boolean isArticleAdjacentSeulement(Article article) {
        return isArticleCompatible(article) && !isArticleExact(article);
    }

    /**
     * Retourne le rang de la catégorie dans les préférences (indexé à 1).
     * Retourne -1 si la catégorie n'est pas dans les préférences.
     */
    public int getRangPreference(Category categorie) {
        int index = preferences.indexOf(categorie);
        return (index == -1) ? -1 : index + 1;
    }

    /** Alias de compatibilité. */
    public int getPreferenceRank(Category category) {
        return getRangPreference(category);
    }

    @Override
    public String toString() {
        return "Abonne{" + prenom + ", ages=" + tranchesAgeEnfants + "}";
    }
}
