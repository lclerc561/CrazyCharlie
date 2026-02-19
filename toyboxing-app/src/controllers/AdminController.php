<?php
require_once __DIR__ . '/../Database.php';

class AdminController {
    
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

    public function validerBox() {
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $abonne = $_POST['abonne'] ?? '';
            // TODO : Mettre à jour le statut de la box dans la base de données
            
            header('Location: /admin/campagne');
            exit;
        }
    }
}