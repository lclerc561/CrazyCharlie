package com.toysacademy.model;

/**
 * Les six catégories d'articles proposées par Toys Academy.
 */
public enum Category {
    SOC, // Jeux de société
    FIG, // Figurines et poupées
    CON, // Jeux de construction
    EXT, // Jeux d'extérieur
    EVL, // Jeux d'éveil et éducatifs
    LIV; // Livres jeunesse

    /**
     * Parse une chaîne de caractères en Category.
     * Gère les espaces et les virgules parasites.
     */
    public static Category fromString(String texte) {
        // Nettoyer : enlever virgules, espaces
        String normalise = texte.trim().replaceAll(",", "").toUpperCase();
        try {
            return Category.valueOf(normalise);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Catégorie inconnue : " + texte);
        }
    }
}
