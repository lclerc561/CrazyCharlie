package com.toysacademy.model;

/**
 * États possibles d'un article (Règle 5 : bonus d'état).
 * N (Neuf) → +2, TB (Très bon état) → +1, B (Bon état) → +0.
 */
public enum State {
    N(2), // Neuf
    TB(1), // Très bon état
    B(0); // Bon état

    private final int bonus;

    State(int bonus) {
        this.bonus = bonus;
    }

    /** Retourne le bonus de points lié à l'état de l'article. */
    public int getBonus() {
        return bonus;
    }

    /**
     * Parse une chaîne de caractères en State.
     */
    public static State fromString(String texte) {
        String normalise = texte.trim().toUpperCase();
        for (State s : State.values()) {
            if (s.name().equals(normalise)) {
                return s;
            }
        }
        throw new IllegalArgumentException("État inconnu : " + texte);
    }
}
