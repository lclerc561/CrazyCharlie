# Dépôt du projet de l'équipe L'EQU1P3 #

<!-- Supprimez les exemples dont vous n'avez pas besoin, gardez la -->
<!-- structure générale du document et répondez aux questions posées -->

## Liste des membres ##

 - MARTINEZ VILLORIA / Oscar / IL-1
 - KHODJAOUI / Elias / IL-1
 - HARY/ Alexandre / DACS
 - CLERC/ Léo / DWM-2
 - SORET / Mathias / DWM-2

## URL ##

- https://github.com/lclerc561/CrazyCharlie
- de l'application finale

##  Partie application Web##

### Liste des numéros de fonctionnalités implantées ###

<!-- Énumération de la liste, commentaires au besoin -->

### Commentaires additionnels ###

<!-- Indiquez toutes les données utiles au test (identifiants, mots de -->
<!-- passes, données déjà saisies etc...), décrivez les éventuelles -->
<!-- fonctionnalités additionnelles -->

Il y avait du code que voilà :

```
git push -u origin master
```

Et une image aussi :

![Texte alternatif](boite_cuisine.png "Logo officiel")

##  Partie Optimisation ##

Nous avons développé une solution en Java modulaire pour résoudre le problème de répartition des jouets (ToyBoxing).

### Architecture
- **Langage** : Java 21
- **Structure** :
  - `com.toysacademy.model` : Structures de données (Box, Article, Abonné).
  - `com.toysacademy.service` : Algorithmes d'optimisation et évaluateur de score.
  - `com.toysacademy.io` : Lecture/Écriture CSV et parsing.
  - `com.toysacademy.Lanceur` : Point d'entrée pour les benchmarks en ligne de commande.
  - `com.toysacademy.ServerHTTP` : API REST native pour l'intégration avec l'application Web.

### Améliorations et Stratégies d'Optimisation
Au-delà de l'énoncé de base, nous avons implémenté plusieurs **stratégies avancées** pour maximiser le score :

1.  **Gestion des Contraintes Souples (Soft Constraints)** :
    Contrairement à une approche rigide, notre système **tolère le dépassement du budget** (10€-60€) ou un léger déséquilibre d'équité si cela permet d'inclure des articles à très forte valeur ajoutée. L'algorithme accepte de payer une pénalité (ex: -5 points) pour gagner davantage (ex: +20 points), ce qui débloque des solutions inaccessibles autrement.

2.  **Compatibilité Étendue (Adjacence)** :
    Nous avons assoupli les règles de compatibilité d'âge. Un jouet "PE" (Petit Enfant) peut être attribué à un enfant "EN" (Enfant) moyennant une réduction de score. Cela élargit considérablement l'espace de recherche et évite de laisser des boîtes vides ou sous-optimisées.

3.  **Logique Multi-Enfants Avancée** :
    Pour les familles nombreuses, l'évaluateur vérifie que **chaque enfant** reçoit au moins un article compatible (exact ou adjacent). Si ce n'est pas le cas, une forte pénalité est appliquée, forçant l'algorithme génétique à prioriser la couverture complète de la fratrie.

### Algorithmes Implémentés
Nous avons comparé 6 approches distinctes pour maximiser le score global :

1.  **Glouton (Greedy)** : Approche naïve qui remplit les boîtes séquentiellement avec les meilleurs articles disponibles. Très rapide mais souvent sous-optimal.
2.  **Glouton avec Descente** : Amélioration locale itérative de la solution gloutonne.
3.  **Recuit Simulé (Simulated Annealing)** : Algorithme probabiliste permettant d'échapper aux optimums locaux en acceptant parfois des dégradations temporaires.
4.  **Bin Packing** : Heuristique axée sur le remplissage optimal du volume (poids) des boîtes.
5.  **Backtracking** : Recherche exhaustive exacte. Efficace sur de très petites instances mais limité par un timeout de security (10s) sur les jeux de données complexes.
6.  **Génétique (Le plus performant)** : Algorithme évolutionnaire qui fait évoluer une population de solutions candidates via des mécanismes de sélection naturelle, croisement et mutations.

### Résultats
Les tests effectués sur différents jeux de données générés (variété de familles, contraintes budgétaires strictes) ont confirmé la robustesse de nos algorithmes. L'approche **Génétique** offre systématiquement le meilleur compromis entre qualité de solution et temps de calcul, surpassant les méthodes heuristiques plus simples (Glouton, Recuit Simulé) sur les instances complexes, notamment celles impliquant de nombreux enfants avec des préférences divergentes. Elle parvient à maximiser la satisfaction globale tout en respectant les contraintes budgétaires et d'équité.

### Intégration Web
Pour relier l'optimisation au site PHP, nous avons implémenté un serveur HTTP léger (`ServerHTTP.java`) écoutant sur le port 8080. Il expose un endpoint `POST /solve` qui accepte le CSV brut, exécute tous les modèles en parallèle, sélectionne la meilleure solution et la renvoie au format CSV.

##  Déploiement ##

Pour cette partie, nous avons suivi l'approche :

 - les explications...
 - ...de ce qu'on a fait
