<?php
require_once __DIR__ . '/../Database.php';

class AdminController {
    
    public function showCampagne() {
        $db = Database::getConnection();
        
        $stmt = $db->query("
            SELECT bl.nom as box_nom, a.prenom as abonne_nom, art.id, art.libelle, c.libelle as categorie_nom, art.age, art.etat, art.prix, art.poids
            FROM box_lien bl
            JOIN abonnes a ON bl.id_abo = a.id
            JOIN box_contenu bc ON bl.id_contenu = bc.id
            JOIN articles art ON bc.id_article = art.id
            LEFT JOIN categorie c ON art.categorie = c.id
            ORDER BY bl.id DESC
        ");
        $lignes = $stmt->fetchAll();
        
        $boxes = [];
        foreach ($lignes as $ligne) {
            $boxKey = $ligne['box_nom'];
            if (!isset($boxes[$boxKey])) {
                $boxes[$boxKey] = ['articles' => [], 'poids_total' => 0, 'prix_total' => 0];
            }
            $boxes[$boxKey]['articles'][] = $ligne;
            $boxes[$boxKey]['poids_total'] += $ligne['poids'];
            $boxes[$boxKey]['prix_total'] += $ligne['prix'];
        }

        renderView('admin/campagne', [
            'title' => 'Historique des Campagnes',
            'boxes' => $boxes,
            'scoreTotal' => 0,
            'isHistory' => true
        ]);
    }

    public function lancerCampagne() {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $poids_max = $_POST['poids_max'] ?? 1200;
            $db = Database::getConnection();

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
                            'libelle' => $fullArt['libelle'],
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
                'scoreTotal' => $scoreTotal,
                'isHistory' => false
            ]);
        }
    }

    public function validerBox() {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $abonneNom = $_POST['abonne'] ?? '';
            $articlesIds = $_POST['articles'] ?? [];
            $db = Database::getConnection();

            if ($abonneNom && !empty($articlesIds)) {
                $stmt = $db->prepare("SELECT id FROM abonnes WHERE prenom = :prenom LIMIT 1");
                $stmt->execute(['prenom' => $abonneNom]);
                $abo = $stmt->fetch();

                if ($abo) {
                    $idAbo = $abo['id'];
                    $nomBox = "Box de " . $abonneNom . " - " . date('Y-m-d H:i:s');

                    foreach ($articlesIds as $idArt) {
                        $stmtBc = $db->prepare("INSERT INTO box_contenu (id_article) VALUES (:id_article)");
                        $stmtBc->execute(['id_article' => $idArt]);
                        $idContenu = $db->lastInsertId();

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
            
            header('Content-Type: application/json');
            echo json_encode(['success' => true]);
            exit;
        }
    }
}