-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : jeu. 19 fév. 2026 à 20:59
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
  `tranche_age` enum('BB','PE','EN','AD') DEFAULT NULL,
  `ordre` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `abonnes`
--

INSERT INTO `abonnes` (`id`, `prenom`, `tranche_age`, `ordre`) VALUES
(1, 'léo', 'BB', NULL);

-- --------------------------------------------------------

--
-- Structure de la table `articles`
--

CREATE TABLE `articles` (
  `id` int(100) NOT NULL,
  `libelle` varchar(255) DEFAULT NULL,
  `categorie` enum('SOC','FIG','CON','EXT','EVL','LIV') DEFAULT NULL,
  `age` enum('BB','PE','EN','AD') DEFAULT NULL,
  `etat` enum('N','TB','B') DEFAULT NULL,
  `prix` int(100) DEFAULT NULL,
  `poids` int(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `articles`
--

INSERT INTO `articles` (`id`, `libelle`, `categorie`, `age`, `etat`, `prix`, `poids`) VALUES
(3, 't1', 'EXT', 'BB', 'N', 1, 123);

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
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `abonnes`
--
ALTER TABLE `abonnes`
  MODIFY `id` int(100) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT pour la table `articles`
--
ALTER TABLE `articles`
  MODIFY `id` int(100) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

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
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
