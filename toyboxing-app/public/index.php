<?php
// public/index.php - Le Routeur principal

// 1. On récupère l'URL demandée (ex: /admin/catalogue)
$request = $_SERVER['REQUEST_URI'];
$path = parse_url($request, PHP_URL_PATH);

// 2. Fonction pour charger une vue à l'intérieur du layout
function renderView($viewPath, $variables = []) {
    extract($variables); // Transforme ['title' => 'Test'] en $title = 'Test'
    ob_start(); // On commence à capturer l'affichage
    
    // On essaie de charger le fichier de la vue
    $file = "../views/$viewPath.php";
    if(file_exists($file)) {
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
        renderView('admin/catalogue', ['title' => 'Catalogue des articles']);
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
        renderView('admin/campagne', ['title' => 'Campagne de Box']);
        break;

    // --- Erreur 404 ---
    default:
        http_response_code(404);
        renderView('404', ['title' => 'Page introuvable']);
        break;
}