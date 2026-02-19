<?php

require_once __DIR__ . '/../Database.php';

class AbonneController
{
    // --- 1. L'INSCRIPTION ---
    public function inscription()
    {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $db = Database::getConnection();

            $prenom = trim($_POST['prenom'] ?? '');
            $nom = trim($_POST['nom'] ?? '');
            $email = trim($_POST['email'] ?? '');
            $tranche_age = $_POST['tranche_age'] ?? null;
            
            $preferencesArray = $_POST['preferences'] ?? [];
            $preferencesStr = implode(',', $preferencesArray);

            if ($prenom && $nom && $email && $tranche_age) {
                $stmtCheck = $db->prepare("SELECT id FROM abonnes WHERE email = :email");
                $stmtCheck->execute([':email' => $email]);
                
                if ($stmtCheck->fetch()) {
                    // Si l'email est trouvé, on renvoie la vue avec un message d'erreur
                    $this->render('abonnes/inscription', [
                        'title' => 'Inscription - ToyBox',
                        'error' => "Cette adresse email est déjà utilisée. Veuillez vous connecter."
                    ]);
                    return; // On arrête l'exécution ici !
                }

                // Si l'email n'existe pas, on l'insère normalement
                $stmt = $db->prepare("INSERT INTO abonnes (prenom, nom, email, tranche_age, preferences) VALUES (:prenom, :nom, :email, :tranche_age, :preferences)");
                $stmt->execute([
                    ':prenom' => $prenom,
                    ':nom' => $nom,
                    ':email' => $email,
                    ':tranche_age' => $tranche_age,
                    ':preferences' => $preferencesStr
                ]);
            }

            // On crée le cookie et on redirige
            setcookie('abonne_email', $email, time() + (86400 * 30), "/");
            header('Location: /ma-box');
            exit;
            
        } else {
            $this->render('abonnes/inscription', [
                'title' => 'Inscription - ToyBox'
            ]);
        }
    }

    // --- 2. LA CONNEXION ---
    public function connexion()
    {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $db = Database::getConnection();
            $email = trim($_POST['email'] ?? '');

            // On cherche l'abonné
            $stmt = $db->prepare("SELECT id FROM abonnes WHERE email = :email");
            $stmt->execute([':email' => $email]);
            
            if ($stmt->fetch()) {
                // L'abonné existe : on lui redonne son cookie et on l'envoie sur sa box
                setcookie('abonne_email', $email, time() + (86400 * 30), "/");
                header('Location: /ma-box');
                exit;
            } else {
                // L'abonné n'existe pas : erreur
                $this->render('abonnes/connexion', [
                    'title' => 'Connexion - ToyBox',
                    'error' => "Aucun compte trouvé avec cette adresse email. Vérifiez l'orthographe ou inscrivez-vous."
                ]);
            }
        } else {
            $this->render('abonnes/connexion', [
                'title' => 'Connexion - ToyBox'
            ]);
        }
    }

    // --- 3. LA DÉCONNEXION ---
    public function deconnexion()
    {
        setcookie('abonne_email', '', time() - 3600, "/");
        header('Location: /');
        exit;
    }

    // --- 4. MA BOX ---
    public function maBox()
    {
        $db = Database::getConnection();
        
        $abonne = null;
        $articles = [];

        if (isset($_COOKIE['abonne_email'])) {
            $email = $_COOKIE['abonne_email'];
            
            $stmt = $db->prepare("SELECT * FROM abonnes WHERE email = :email LIMIT 1");
            $stmt->execute([':email' => $email]);
            $abonne = $stmt->fetch();

            if ($abonne) {
                $stmtBox = $db->prepare("
                    SELECT art.libelle, c.libelle as categorie_nom, art.etat, art.prix, art.poids
                    FROM box_lien bl
                    JOIN box_contenu bc ON bl.id_contenu = bc.id
                    JOIN articles art ON bc.id_article = art.id
                    LEFT JOIN categorie c ON art.categorie = c.id
                    WHERE bl.id_abo = :id_abo
                ");
                $stmtBox->execute([':id_abo' => $abonne['id']]);
                $articles = $stmtBox->fetchAll();
            }
        }

        $this->render('abonnes/ma-box', [
            'title' => 'Consulter ma Box',
            'abonne' => $abonne,
            'articles' => $articles
        ]);
    }

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