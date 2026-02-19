<?php
require_once __DIR__ . '/../src/Database.php';
require_once __DIR__ . '/../src/Controllers/AbonneController.php';
require_once __DIR__ . '/../src/Controllers/ArticleController.php';
require_once __DIR__ . '/../src/Controllers/AdminController.php';
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
    case '/inscription':
        $abonneController = new AbonneController();
        $abonneController->inscription();
        break;

    case '/ma-box':
        $abonneController = new AbonneController();
        $abonneController->maBox();
        break;

    // --- Back-office (Admin) --- 
    case '/admin/catalogue':

        $db = Database::getConnection();

        // --- FILTRES ---
        $categorieFilter = $_GET['categorie'] ?? null;
        $ageFilter = $_GET['age'] ?? null;

        $where = [];
        $params = [];

        if ($categorieFilter) {
            $where[] = "c.libelle = :categorie";
            $params['categorie'] = $categorieFilter;
        }

        if ($ageFilter) {
            $where[] = "a.age = :age";
            $params['age'] = $ageFilter;
        }

        $whereSQL = "";
        if (!empty($where)) {
            $whereSQL = " WHERE " . implode(" AND ", $where);
        }


        // --- TRI ---
        $allowedSort = [
            'categorie' => 'c.libelle',
            'age' => 'a.age',
            'etat' => 'a.etat',
            'prix' => 'a.prix',
            'poids' => 'a.poids'
        ];

        $sort = $_GET['sort'] ?? null;
        $order = $_GET['order'] ?? 'ASC';
        $order = strtoupper($order) === 'DESC' ? 'DESC' : 'ASC';

        $orderBy = "";
        if ($sort && isset($allowedSort[$sort])) {
            $orderBy = " ORDER BY " . $allowedSort[$sort] . " $order";
        }

        $sql = "
        SELECT a.id, a.libelle, c.libelle as categorie_nom, 
               a.age, a.etat, a.prix, a.poids 
        FROM articles a 
        LEFT JOIN categorie c ON a.categorie = c.id
        $whereSQL
        $orderBy
    ";

        $stmt = $db->prepare($sql);
        $stmt->execute($params);
        $articles = $stmt->fetchAll();

        renderView('admin/catalogue', [
            'title' => 'Catalogue des articles',
            'articles' => $articles,
            'currentCategorie' => $categorieFilter,
            'currentAge' => $ageFilter,
            'currentSort' => $sort,
            'currentOrder' => $order
        ]);

        break;

    case '/admin/article/ajouter':
        $articleController = new ArticleController();

        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $articleController->ajouter();
        } else {
            $articleController->ajouter();
        }
        break;
    
    case '/admin/article/modifier':
        $articleController = new ArticleController();

        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $articleController->modifier();
        } else {
            $articleController->modifier();
        }
        break;

    case '/admin/article/supprimer':
        $articleController = new ArticleController();

        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $articleController->supprimer();
        } else {
            $articleController->supprimer();
        }
        break;
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
            'scoreTotal' => 0
        ]);
        break;

    case '/admin/article/ajouter':
        $articleController = new ArticleController();
        $articleController->ajouter();
        break;
        
    case '/connexion':
        $abonneController = new AbonneController();
        $abonneController->connexion();
        break;

    case '/deconnexion':
        $abonneController = new AbonneController();
        $abonneController->deconnexion();
        break;
}