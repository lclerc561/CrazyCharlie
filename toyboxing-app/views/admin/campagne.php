<div class="d-flex justify-content-between align-items-center mb-4">
    <h2>🚀 Lancement de la Campagne</h2>
    <a href="/admin/catalogue" class="btn btn-outline-secondary">Retour au catalogue</a>
</div>

<div class="row">
    <div class="col-12 col-lg-4 mb-4">
        <div class="card p-4">
            <h5 class="mb-3">Paramétrage</h5>
            <form action="/admin/campagne" method="POST">
                <div class="mb-3">
                    <label for="poids_max" class="form-label fw-bold">Poids maximum par box (g)</label>
                    <input type="number" step="1" min="0" class="form-control" id="poids_max" name="poids_max" value="<?= htmlspecialchars($_POST['poids_max'] ?? 1200) ?>" required>
                </div>
                <button type="submit" class="btn btn-primary w-100">Lancer l'optimisation</button>
            </form>
        </div>
    </div>

    <div class="col-12 col-lg-8">
        
        <h5 class="mb-3">
            <?= (isset($isHistory) && $isHistory) ? 'Historique des expéditions' : 'Résultats de l\'algorithme' ?>
        </h5>
        
        <?php if (!isset($isHistory) || !$isHistory): ?>
            <div class="alert alert-success fw-bold">
                Score global de la composition : <?= htmlspecialchars($scoreTotal ?? 0) ?> points
            </div>
        <?php endif; ?>

        <?php if (!empty($boxes)): ?>
            <?php foreach ($boxes as $boxKey => $box): ?>
                
                <div class="card mb-3 shadow-sm border-0">
                    <div class="card-header bg-white d-flex justify-content-between align-items-center">
                        <h5 class="mb-0 text-primary">
                            <?= (isset($isHistory) && $isHistory) ? htmlspecialchars($boxKey) : "Nouvelle Box de " . htmlspecialchars($boxKey) ?>
                        </h5>
                        
                        <?php if (isset($isHistory) && $isHistory): ?>
                            <span class="badge bg-secondary">Déjà validée</span>
                        <?php else: ?>
                            <form action="/admin/box/valider" method="POST" class="m-0 form-valider-box">
                                <input type="hidden" name="abonne" value="<?= htmlspecialchars($boxKey) ?>">
                                <?php foreach ($box['articles'] as $art): ?>
                                    <input type="hidden" name="articles[]" value="<?= htmlspecialchars($art['id']) ?>">
                                <?php endforeach; ?>
                                <button type="submit" class="btn btn-sm btn-success btn-valider">Valider cette box</button>
                            </form>
                        <?php endif; ?>
                    </div>
                    
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-sm table-hover align-middle mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th class="ps-3">ID</th>
                                        <th>Jouet</th>
                                        <th>Catégorie</th>
                                        <th>Âge</th>
                                        <th>État</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <?php foreach ($box['articles'] as $art): ?>
                                    <tr>
                                        <td class="text-muted ps-3">#<?= htmlspecialchars($art['id']) ?></td>
                                        <td class="fw-bold"><?= htmlspecialchars($art['libelle'] ?? 'Article') ?></td>
                                        <td><span class="badge bg-info text-dark"><?= htmlspecialchars($art['categorie_nom'] ?? 'N/A') ?></span></td>
                                        <td><span class="badge bg-secondary"><?= htmlspecialchars($art['age']) ?></span></td>
                                        <td><?= htmlspecialchars($art['etat'] ?? 'Non précisé') ?></td>
                                    </tr>
                                    <?php endforeach; ?>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div class="card-footer bg-light d-flex justify-content-between small">
                        <span><strong>Poids total :</strong> <?= htmlspecialchars($box['poids_total']) ?> g</span>
                        <span><strong>Valeur estimée :</strong> <?= htmlspecialchars($box['prix_total']) ?> €</span>
                    </div>
                </div>
            <?php endforeach; ?>
        <?php else: ?>
            <div class="alert alert-warning">Aucune box n'a encore été générée.</div>
        <?php endif; ?>

    </div>
</div>

<script>
document.querySelectorAll('.form-valider-box').forEach(form => {
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const btn = this.querySelector('.btn-valider');
        btn.innerHTML = '⏳ Validation...';
        btn.disabled = true;

        fetch(this.action, {
            method: 'POST',
            body: new FormData(this)
        })
        .then(response => response.json())
        .then(data => {
            if(data.success) {
                this.outerHTML = '<span class="badge bg-secondary">Déjà validée</span>';
            }
        })
        .catch(error => {
            alert('Erreur lors de la sauvegarde.');
            btn.innerHTML = 'Valider cette box';
            btn.disabled = false;
        });
    });
});
</script>