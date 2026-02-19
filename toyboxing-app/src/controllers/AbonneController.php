<?php
// src/Controllers/AbonneController.php

require_once __DIR__ . '/../Database.php';

class AbonneController
{
    // Gère la page d'accueil (affichage du formulaire ou traitement)
    public function inscription()
    {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $db = Database::getConnection();

            $prenom = trim($_POST['prenom'] ?? '');
            $tranche_age = $_POST['tranche_age'] ?? null;
            $email = trim($_POST['email'] ?? '');
            
            // Les préférences (qu'on transforme en chaîne ex: "SOC,FIG,LIV...")
            // À terme, ton ami BDD devra sûrement adapter la table pour les stocker
            $preferencesArray = $_POST['preferences'] ?? [];
            $preferencesStr = implode(',', $preferencesArray);

            if ($prenom && $tranche_age) {
                // On insère l'abonné dans la base de données
                $stmt = $db->prepare("INSERT INTO abonnes (prenom, tranche_age) VALUES (:prenom, :tranche_age)");
                $stmt->execute([
                    ':prenom' => $prenom,
                    ':tranche_age' => $tranche_age
                ]);
            }

            // On crée le cookie pour retenir l'abonné (valable 30 jours)
            // L'email lui servira d'identifiant pour la suite
            setcookie('abonne_email', $email, time() + (86400 * 30), "/");

            // Redirection vers sa page perso
            header('Location: /ma-box');
            exit;
        } else {
            $this->render('abonnes/inscription', [
                'title' => 'Inscription - ToyBox'
            ]);
        }
    }

    // Gère l'affichage de la page "Ma Box"
    public function maBox()
    {
        $this->render('abonnes/ma-box', [
            'title' => 'Consulter ma Box'
        ]);
    }

    // Fonction de rendu (identique à celle de ton ami)
    private function render($view, $variables = [])
    {
        extract($variables);
        ob_start();
        $file = __DIR__ . "/../../views/$view.php";
        if(file_exists($file)) {
            require $file;
        } else {
            echo "<div class='alert alert-danger mt-5'>Vue introuvable : $view.php</div>";
        }
        $content = ob_get_clean();
        require __DIR__ . "/../../views/layout.php";
    }
}