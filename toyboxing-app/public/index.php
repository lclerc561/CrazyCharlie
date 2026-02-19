<?php
require_once __DIR__ . '/../src/Database.php';
// 1. On récupère l'URL demandée (ex: /admin/catalogue)
$request = $_SERVER['REQUEST_URI'];
$path = parse_url($request, PHP_URL_PATH);

// 2. Fonction pour charger une vue à l'intérieur du layout
function renderView($viewPath, $variables = [])
{
    extract($variables); // Transforme ['title' => 'Test'] en $title = 'Test'
    ob_start(); // On commence à capturer l'affichage

    // On essaie de charger le fichier de la vue
    $file = "../views/$viewPath.php";
    if (file_exists($file)) {
        require $file;
    } else {
        echo "<div class='alert alert-danger mt-5'><h1>Vue non trouvée</h1><p>Il manque le fichier : <code>views/$viewPath.php</code></p></div>";
    }

    $content = ob_get_clean(); // On stocke la vue générée
    require "../views/layout.php"; // On l'injecte dans le layout global
}

// 3. Définition des routes
switch ($path) {

    // --- Front-office (Abonnés) ---
    case '/':
    case '':
        // Si l'abonné valide le formulaire
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            // TODO: Relier à la base de données et créer le cookie
            die("Traitement de l'inscription en cours... (À coder)");
        } else {
            renderView('abonnes/inscription', ['title' => 'Inscription - ToyBox']);
        }
        break;

    case '/ma-box':
        renderView('abonnes/ma-box', ['title' => 'Consulter ma Box']);
        break;

    // --- Back-office (Admin) --- 
    case '/admin/catalogue':
        $db = Database::getConnection();
        // On fait une jointure pour récupérer le libellé de la catégorie (ex: SOC) au lieu de l'ID (ex: 1)
        $stmt = $db->query("SELECT a.id, a.libelle, c.libelle as categorie_nom, a.age, a.etat, a.prix, a.poids 
                            FROM articles a 
                            LEFT JOIN categorie c ON a.categorie = c.id");
        $articles = $stmt->fetchAll();
        
        renderView('admin/catalogue', [
            'title' => 'Catalogue des articles',
            'articles' => $articles // On passe la variable à la vue
        ]);
        break;

    case '/admin/article/ajouter':
        // Si l'admin valide l'ajout d'un article
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            // TODO: Insérer l'article en base de données
            // Pour le moment, on simule un succès en redirigeant vers le catalogue
            header('Location: /admin/catalogue');
            exit;
        } else {
            renderView('admin/ajout-article', ['title' => 'Ajouter un don']);
        }
        break;

    case '/admin/campagne':
        $db = Database::getConnection();
        // On récupère les box enregistrées en liant abonnés, contenus et articles
        $stmt = $db->query("
            SELECT a.prenom as abonne_nom, art.id, art.libelle, c.libelle as categorie_nom, art.age, art.etat, art.prix, art.poids
            FROM box_lien bl
            JOIN abonnes a ON bl.id_abo = a.id
            JOIN box_contenu bc ON bl.id_contenu = bc.id
            JOIN articles art ON bc.id_article = art.id
            LEFT JOIN categorie c ON art.categorie = c.id
        ");
        $lignes = $stmt->fetchAll();
        
        $boxes = [];
        // On regroupe les articles par prénom d'abonné
        foreach ($lignes as $ligne) {
            $abonne = $ligne['abonne_nom'];
            if (!isset($boxes[$abonne])) {
                $boxes[$abonne] = ['articles' => [], 'poids_total' => 0, 'prix_total' => 0];
            }
            $boxes[$abonne]['articles'][] = $ligne;
            $boxes[$abonne]['poids_total'] += $ligne['poids'];
            $boxes[$abonne]['prix_total'] += $ligne['prix'];
        }

        renderView('admin/campagne', [
            'title' => 'Campagne de Box',
            'boxes' => $boxes,
            'scoreTotal' => 0 // Pour l'instant on met 0, l'algo le calculera plus tard
        ]);
        break;

    // --- Erreur 404 ---
    default:
        http_response_code(404);
        renderView('404', ['title' => 'Page introuvable']);
        break;
}