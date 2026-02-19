<h2 class="mb-4">Modifier l'article</h2>

<form method="POST" class="card p-4">

    <input type="hidden" name="id" value="<?= $article['id'] ?>">

    <div class="mb-3">
        <label class="form-label">Désignation</label>
        <input type="text" name="designation" class="form-control"
               value="<?= htmlspecialchars($article['libelle']) ?>" required>
    </div>

    <div class="mb-3">
        <label class="form-label">Catégorie</label>
        <select name="categorie" class="form-select" required>
            <option value="1" <?= $article['categorie'] === 'SOC' ? 'selected' : '' ?>>SOC</option>
            <option value="2" <?= $article['categorie'] === 'FIG' ? 'selected' : '' ?>>FIG</option>
            <option value="3" <?= $article['categorie'] === 'CON' ? 'selected' : '' ?>>CON</option>
            <option value="4" <?= $article['categorie'] === 'EXT' ? 'selected' : '' ?>>EXT</option>
            <option value="5" <?= $article['categorie'] === 'EVL' ? 'selected' : '' ?>>EVL</option>
            <option value="6" <?= $article['categorie'] === 'LIV' ? 'selected' : '' ?>>LIV</option>
        </select>
    </div>

    <div class="mb-3">
        <label class="form-label">Tranche d'âge</label>
        <select name="tranche_age" class="form-select" required>
            <option value="BB" <?= $article['age'] === 'BB' ? 'selected' : '' ?>>0-3 ans</option>
            <option value="PE" <?= $article['age'] === 'PE' ? 'selected' : '' ?>>3-6 ans</option>
            <option value="EN" <?= $article['age'] === 'EN' ? 'selected' : '' ?>>6-10 ans</option>
            <option value="AD" <?= $article['age'] === 'AD' ? 'selected' : '' ?>>10+ ans</option>
        </select>
    </div>

    <div class="mb-3">
        <label class="form-label">État</label>
        <select name="etat" class="form-select" required>
            <option value="N" <?= $article['etat'] === 'N' ? 'selected' : '' ?>>Neuf</option>
            <option value="TB" <?= $article['etat'] === 'TB' ? 'selected' : '' ?>>Très bon état</option>
            <option value="B" <?= $article['etat'] === 'B' ? 'selected' : '' ?>>bon état</option>
        </select>
    </div>

    <div class="mb-3">
        <label class="form-label">Prix (€)</label>
        <input type="number" step="0.01" name="prix" class="form-control"
               value="<?= $article['prix'] ?>">
    </div>

    <div class="mb-3">
        <label class="form-label">Poids (g)</label>
        <input type="number" name="poids" class="form-control"
               value="<?= $article['poids'] ?>">
    </div>

    <button type="submit" class="btn btn-primary">
        Enregistrer les modifications
    </button>

</form>
