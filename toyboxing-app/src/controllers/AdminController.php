<?php
require_once __DIR__ . '/../Database.php';

class AdminController {
    
    // 1. Afficher l'historique des box déjà validées
    public function showCampagne() {
        $db = Database::getConnection();
        
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
    }

    // 2. Lancer l'algorithme Java (Celle qui avait disparu !)
    public function lancerCampagne() {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $poids_max = $_POST['poids_max'] ?? 1200;
            $db = Database::getConnection();

            // Récupération avec jointure pour avoir le nom de la catégorie (SOC, FIG...) attendu par Java
            $stmtArt = $db->query("SELECT a.id, a.libelle, c.libelle as cat, a.age, a.etat, a.prix, a.poids FROM articles a LEFT JOIN categorie c ON a.categorie = c.id");
            $articles = $stmtArt->fetchAll();

            $stmtAbo = $db->query("SELECT id, prenom, tranche_age, preferences FROM abonnes");
            $abonnes = $stmtAbo->fetchAll();

            $csv = "articles\n";
            $artIndex = [];
            foreach($articles as $a) {
                $etat = empty($a['etat']) ? 'N' : $a['etat'];
                $csv .= "a{$a['id']};{$a['libelle']};{$a['cat']};{$a['age']};{$etat};{$a['prix']};{$a['poids']}\n";
                $artIndex["a{$a['id']}"] = $a;
            }

            $csv .= "\nabonnes\n";
            foreach($abonnes as $ab) {
                $prefs = empty($ab['preferences']) ? 'SOC,FIG,CON,EXT,EVL,LIV' : $ab['preferences'];
                $csv .= "s{$ab['id']};{$ab['prenom']};{$ab['tranche_age']};{$prefs}\n";
            }

            $csv .= "\nparametres\n{$poids_max}\n";

            // Appel au serveur Java 8080
            $options = [
                'http' => [
                    'header'  => "Content-Type: text/plain; charset=utf-8\r\n",
                    'method'  => 'POST',
                    'content' => $csv
                ]
            ];
            $context  = stream_context_create($options);
            $result = @file_get_contents("http://localhost:8080/solve", false, $context);

            if ($result === false) {
                die("<div class='alert alert-danger m-5'><h2>Erreur !</h2>Impossible de joindre le serveur Java sur le port 8080. Assure-toi qu'il est allumé !</div>");
            }

            $lines = explode("\n", trim($result));
            $scoreTotal = array_shift($lines);

            $boxes = [];
            foreach($lines as $line) {
                if (empty(trim($line))) continue;
                $parts = explode(";", trim($line));
                
                if (count($parts) >= 2) {
                    $prenom = $parts[0];
                    $idArtCsv = $parts[1];

                    if (!isset($boxes[$prenom])) {
                        $boxes[$prenom] = ['articles' => [], 'poids_total' => 0, 'prix_total' => 0];
                    }

                    if (isset($artIndex[$idArtCsv])) {
                        $fullArt = $artIndex[$idArtCsv];
                        $boxes[$prenom]['articles'][] = [
                            'id' => $fullArt['id'],
                            'categorie_nom' => $fullArt['cat'],
                            'age' => $fullArt['age'],
                            'etat' => $etat,
                            'prix' => $fullArt['prix'],
                            'poids' => $fullArt['poids']
                        ];
                        $boxes[$prenom]['poids_total'] += $fullArt['poids'];
                        $boxes[$prenom]['prix_total'] += $fullArt['prix'];
                    }
                }
            }

            renderView('admin/campagne', [
                'title' => 'Campagne de Box',
                'boxes' => $boxes,
                'scoreTotal' => $scoreTotal
            ]);
        }
    }

    // 3. Sauvegarder la box validée
    public function validerBox() {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $abonneNom = $_POST['abonne'] ?? '';
            $articlesIds = $_POST['articles'] ?? []; // On récupère les ID des articles cachés dans le formulaire
            $db = Database::getConnection();

            if ($abonneNom && !empty($articlesIds)) {
                $stmt = $db->prepare("SELECT id FROM abonnes WHERE prenom = :prenom LIMIT 1");
                $stmt->execute(['prenom' => $abonneNom]);
                $abo = $stmt->fetch();

                if ($abo) {
                    $idAbo = $abo['id'];
                    $nomBox = "Box de " . $abonneNom . " - " . date('Y-m-d');

                    foreach ($articlesIds as $idArt) {
                        // On insère l'article dans la box
                        $stmtBc = $db->prepare("INSERT INTO box_contenu (id_article) VALUES (:id_article)");
                        $stmtBc->execute(['id_article' => $idArt]);
                        $idContenu = $db->lastInsertId();

                        // On lie le tout à l'abonné
                        $stmtBl = $db->prepare("INSERT INTO box_lien (nom, id_abo, id_contenu, date) VALUES (:nom, :id_abo, :id_contenu, :date)");
                        $stmtBl->execute([
                            'nom' => $nomBox,
                            'id_abo' => $idAbo,
                            'id_contenu' => $idContenu,
                            'date' => date('Y-m-d')
                        ]);
                    }
                }
            }
            // On redirige vers l'historique
            header('Location: /admin/campagne');
            exit;
        }
    }
}