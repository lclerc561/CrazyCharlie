-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : jeu. 19 fév. 2026 à 21:43
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `crazy_charly_day`
--

-- --------------------------------------------------------

--
-- Structure de la table `abonnes`
--

CREATE TABLE `abonnes` (
  `id` int(100) NOT NULL,
  `prenom` varchar(255) DEFAULT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `tranche_age` enum('BB','PE','EN','AD') DEFAULT NULL,
  `preferences` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `abonnes`
--

INSERT INTO `abonnes` (`id`, `prenom`, `nom`, `email`, `tranche_age`, `preferences`) VALUES
(1, 'léo', NULL, NULL, 'BB', NULL),
(2, 'Emma', 'Dupont', 'emma@test.fr', 'PE', 'SOC,FIG,EVL,CON,LIV,EXT'),
(3, 'Lucas', 'Martin', 'lucas@test.fr', 'EN', 'EXT,CON,EVL,SOC,FIG,LIV'),
(4, 'Chloé', 'Dubois', 'chloe@test.fr', 'BB', 'EVL,LIV,FIG,SOC,CON,EXT'),
(5, 'Tom', 'Bernard', 'tom@test.fr', 'AD', 'SOC,CON,LIV,FIG,EXT,EVL');

-- --------------------------------------------------------

--
-- Structure de la table `articles`
--

CREATE TABLE `articles` (
  `id` int(100) NOT NULL,
  `libelle` varchar(255) DEFAULT NULL,
  `categorie` int(10) DEFAULT NULL,
  `age` enum('BB','PE','EN','AD') DEFAULT NULL,
  `etat` enum('N','TB','B') DEFAULT NULL,
  `prix` int(100) DEFAULT NULL,
  `poids` int(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `articles`
--

INSERT INTO `articles` (`id`, `libelle`, `categorie`, `age`, `etat`, `prix`, `poids`) VALUES
(1, 'test 1', 1, 'BB', NULL, 12, 50),
(2, 'Monopoly Junior', 1, 'PE', 'N', 10, 500),
(3, 'Poupée Princesse', 2, 'PE', 'TB', 5, 300),
(4, 'Tapis d\'éveil musical', 5, 'BB', 'TB', 15, 800),
(5, 'Camion de Pompier Lego', 3, 'EN', 'N', 20, 600),
(6, 'Ballon de foot en cuir', 4, 'EN', 'B', 4, 400),
(7, 'Livre Tchoupi va à l\'école', 6, 'PE', 'TB', 3, 150),
(8, 'Jeu de cartes Uno', 1, 'EN', 'N', 5, 200),
(9, 'Grosse Peluche Ours', 5, 'BB', 'TB', 8, 250),
(10, 'Boite de 200 Kapla', 3, 'PE', 'B', 15, 1000),
(11, 'Cerf-volant coloré', 4, 'EN', 'N', 7, 150),
(12, 'Livre sonore des animaux', 6, 'BB', 'B', 5, 300),
(13, 'Figurine Spiderman', 2, 'EN', 'TB', 6, 200),
(14, 'Jeu d\'échecs en bois', 1, 'AD', 'TB', 12, 700),
(15, 'Lego Architecture Paris', 3, 'AD', 'N', 30, 1200),
(16, 'Roman Le Seigneur des Anneaux', 6, 'AD', 'N', 8, 350);

-- --------------------------------------------------------

--
-- Structure de la table `box_contenu`
--

CREATE TABLE `box_contenu` (
  `id` int(100) NOT NULL,
  `id_article` int(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `box_contenu`
--

INSERT INTO `box_contenu` (`id`, `id_article`) VALUES
(1, 1);

-- --------------------------------------------------------

--
-- Structure de la table `box_lien`
--

CREATE TABLE `box_lien` (
  `id` int(100) NOT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `id_abo` int(100) DEFAULT NULL,
  `id_contenu` int(100) DEFAULT NULL,
  `date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `box_lien`
--

INSERT INTO `box_lien` (`id`, `nom`, `id_abo`, `id_contenu`, `date`) VALUES
(1, 'TEST LIEN', 1, 1, '2026-02-12');

-- --------------------------------------------------------

--
-- Structure de la table `categorie`
--

CREATE TABLE `categorie` (
  `id` int(10) NOT NULL,
  `libelle` varchar(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `categorie`
--

INSERT INTO `categorie` (`id`, `libelle`) VALUES
(1, 'SOC'),
(2, 'FIG'),
(3, 'CON'),
(4, 'EXT'),
(5, 'EVL'),
(6, 'LIV');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `abonnes`
--
ALTER TABLE `abonnes`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `articles`
--
ALTER TABLE `articles`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `box_contenu`
--
ALTER TABLE `box_contenu`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `box_lien`
--
ALTER TABLE `box_lien`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `categorie`
--
ALTER TABLE `categorie`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `abonnes`
--
ALTER TABLE `abonnes`
  MODIFY `id` int(100) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT pour la table `articles`
--
ALTER TABLE `articles`
  MODIFY `id` int(100) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT pour la table `box_contenu`
--
ALTER TABLE `box_contenu`
  MODIFY `id` int(100) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT pour la table `box_lien`
--
ALTER TABLE `box_lien`
  MODIFY `id` int(100) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT pour la table `categorie`
--
ALTER TABLE `categorie`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
