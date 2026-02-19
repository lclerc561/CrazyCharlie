package com.toysacademy.service;

import com.toysacademy.model.*;
import java.util.List;

/**
 * Interface commune pour tous les solveurs (modèles) d'optimisation.
 * Chaque implémentation représente une stratégie/algorithme différent.
 */
public interface Solveur {

    /**
     * Résout le problème d'optimisation des toy box (avec prix).
     *
     * @param abonnes  liste des abonnés participants
     * @param articles liste des articles disponibles
     * @param poidsMax poids maximum autorisé par box (en grammes)
     * @param prixMin  prix minimum cible par box (en euros)
     * @param prixMax  prix maximum cible par box (en euros)
     * @return la composition optimisée
     */
    default Composition resoudre(List<Abonne> abonnes, List<Article> articles,
            double poidsMax, int prixMin, int prixMax) {
        // Par défaut, ignorer la contrainte prix (compat arrière)
        return resoudre(abonnes, articles, poidsMax);
    }

    /**
     * Résout le problème d'optimisation (sans contrainte de prix).
     */
    Composition resoudre(List<Abonne> abonnes, List<Article> articles, double poidsMax);

    /**
     * Retourne le nom lisible de ce modèle (pour l'affichage du benchmarking).
     */
    String getNom();
}
