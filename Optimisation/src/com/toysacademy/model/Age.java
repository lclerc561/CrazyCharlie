package com.toysacademy.model;

/**
 * Tranches d'âge cibles pour les articles et les enfants des abonnés.
 * Ordre : BB(0) < PE(1) < EN(2) < AD(3).
 * Deux tranches sont adjacentes si leur distance ordinale == 1.
 */
public enum Age {
    BB, // 0-3 ans (bébé)
    PE, // 3-6 ans (petit enfant)
    EN, // 6-10 ans (enfant)
    AD; // 10+ ans (adolescent)

    /**
     * Vérifie si deux tranches d'âge sont adjacentes.
     * BB↔PE, PE↔EN, EN↔AD.
     */
    public boolean isAdjacent(Age other) {
        return Math.abs(this.ordinal() - other.ordinal()) == 1;
    }

    /**
     * Vérifie si un article de cette tranche est compatible avec un enfant de l'âge
     * donné.
     * Compatible = même tranche OU tranche adjacente.
     */
    public boolean isCompatible(Age ageEnfant) {
        return this == ageEnfant || this.isAdjacent(ageEnfant);
    }

    /**
     * Retourne true si la compatibilité est exacte (même tranche).
     */
    public boolean isExact(Age ageEnfant) {
        return this == ageEnfant;
    }

    /**
     * Parse une chaîne de caractères en Age.
     * Gère les espaces et les points-virgules parasites.
     */
    public static Age fromString(String texte) {
        String normalise = texte.trim().split(" ")[0].toUpperCase();
        try {
            return Age.valueOf(normalise);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tranche d'âge inconnue : " + texte);
        }
    }
}
