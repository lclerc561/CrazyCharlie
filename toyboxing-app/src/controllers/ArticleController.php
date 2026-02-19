<?php

require_once __DIR__ . '/../Database.php';

class ArticleController
{
    public function ajouter()
    {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $this->store();
        } else {
            $this->create();
        }
    }

    private function create()
    {
        $this->render('admin/ajout-article', [
            'title' => 'Ajouter un don'
        ]);
    }

    private function store()
    {
        $db = Database::getConnection();

        // Récupération des données du formulaire
        $designation = trim($_POST['designation'] ?? '');
        $categorie   = $_POST['categorie'] ?? null;
        $tranche_age = $_POST['tranche_age'] ?? null;
        $etat        = $_POST['etat'] ?? null;
        $prix        = $_POST['prix'] ?? 0;
        $poids       = $_POST['poids'] ?? 0;

        // Validation simple
        if (!$designation || !$categorie || !$tranche_age || !$etat) {
            die("Tous les champs obligatoires doivent être remplis.");
        }

        // Insertion en base
        $stmt = $db->prepare("
            INSERT INTO articles (libelle, categorie, age, etat, prix, poids)
            VALUES (:libelle, :categorie, :age, :etat, :prix, :poids)
        ");

        $stmt->execute([
            'libelle'   => $designation,
            'categorie' => $categorie,
            'age'       => $tranche_age,
            'etat'      => $etat,
            'prix'      => $prix,
            'poids'     => $poids
        ]);

        header('Location: /admin/catalogue');
        exit;
    }

    private function render($view, $variables = [])
    {
        extract($variables);
        ob_start();

        require __DIR__ . "/../../views/$view.php";

        $content = ob_get_clean();
        require __DIR__ . "/../../views/layout.php";
    }

    public function modifier()
    {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        $this->update();
        } else {
        $this->edit();
        }
    }

    private function edit()
    {
        $db = Database::getConnection();

        $id = $_GET['id'] ?? null;

        if (!$id) {
            die("ID manquant.");
        }

        $stmt = $db->prepare("SELECT * FROM articles WHERE id = :id");
        $stmt->execute(['id' => $id]);
        $article = $stmt->fetch();

        if (!$article) {
            die("Article introuvable.");
        }

        $this->render('admin/modifier-article', [
            'title' => 'Modifier un article',
            'article' => $article
        ]);
    }

    private function update()
    {
        $db = Database::getConnection();

        $id          = $_POST['id'] ?? null;
        $designation = trim($_POST['designation'] ?? '');
        $categorie   = $_POST['categorie'] ?? null;
        $tranche_age = $_POST['tranche_age'] ?? null;
        $etat        = $_POST['etat'] ?? null;
        $prix        = $_POST['prix'] ?? 0;
        $poids       = $_POST['poids'] ?? 0;

        if (!$id || !$designation || !$categorie || !$tranche_age || !$etat) {
            die("Tous les champs obligatoires doivent être remplis.");
        }

        $stmt = $db->prepare("
            UPDATE articles
            SET libelle = :libelle,
            categorie = :categorie,
            age = :age,
            etat = :etat,
            prix = :prix,
            poids = :poids
            WHERE id = :id
        ");

        $stmt->execute([
            'libelle'   => $designation,
            'categorie' => $categorie,
            'age'       => $tranche_age,
            'etat'      => $etat,
            'prix'      => $prix,
            'poids'     => $poids,
            'id'        => $id
        ]);

        header('Location: /admin/catalogue');
        exit;
    }

    public function supprimer()
    {
        $id = $_GET['id'] ?? null;

        if (!$id) {
            die("ID manquant.");
        }

        $db = Database::getConnection();

        $stmt = $db->prepare("DELETE FROM articles WHERE id = :id");
        $stmt->execute(['id' => $id]);

        header('Location: /admin/catalogue');
        exit;
    }



}
