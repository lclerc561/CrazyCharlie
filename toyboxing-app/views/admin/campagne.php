<div class="d-flex justify-content-between align-items-center mb-4">
    <h2>🚀 Lancement de la Campagne</h2>
</div>

<div class="row">
    <div class="col-12 col-lg-4 mb-4">
        <div class="card p-4">
            <h5 class="mb-3">Paramétrage</h5>
            <form action="/admin/campagne" method="POST">
                <div class="mb-3">
                    <label for="poids_max" class="form-label fw-bold">Poids maximum par box (g)</label>
                    <input type="number" step="1" min="0" class="form-control" id="poids_max" name="poids_max" value="1200" required>
                </div>
                <button type="submit" class="btn btn-primary w-100">Lancer l'optimisation</button>
            </form>
        </div>
    </div>

    <div class="col-12 col-lg-8">
        <h5 class="mb-3">Résultats de l'algorithme</h5>
        
        <?php
        $scoreTotal = 70;
        $boxesMock = [
            'Alice' => [
                'articles' => [
                    ['id' => 'a1', 'cat' => 'SOC', 'age' => 'PE', 'etat' => 'N'],
                    ['id' => 'a2', 'cat' => 'FIG', 'age' => 'PE', 'etat' => 'TB'],
                    ['id' => 'a4', 'cat' => 'CON', 'age' => 'PE', 'etat' => 'N']
                ],
                'poids_total' => 1000,
                'prix_total' => 17
            ],
            'Bob' => [
                'articles' => [
                    ['id' => 'a7', 'cat' => 'EXT', 'age' => 'EN', 'etat' => 'N'],
                    ['id' => 'a6', 'cat' => 'CON', 'age' => 'EN', 'etat' => 'B'],
                    ['id' => 'a8', 'cat' => 'LIV', 'age' => 'EN', 'etat' => 'TB']
                ],
                'poids_total' => 1200,
                'prix_total' => 21
            ]
        ];
        ?>

        <div class="alert alert-success fw-bold">
            Score global de la composition : <?= $scoreTotal ?> points
        </div>

        <?php foreach ($boxesMock as $abonne => $box): ?>
            <div class="card mb-3">
                <div class="card-header bg-white d-flex justify-content-between align-items-center">
                    <h5 class="mb-0 text-primary">Box de <?= $abonne ?></h5>
                    <form action="/admin/box/valider" method="POST" class="m-0">
                        <input type="hidden" name="abonne" value="<?= $abonne ?>">
                        <button type="submit" class="btn btn-sm btn-success">Valider cette box</button>
                    </form>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-sm table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>ID</th>
                                    <th>Catégorie</th>
                                    <th>Âge</th>
                                    <th>État</th>
                                </tr>
                            </thead>
                            <tbody>
                                <?php foreach ($box['articles'] as $art): ?>
                                <tr>
                                    <td class="text-muted">#<?= $art['id'] ?></td>
                                    <td><span class="badge bg-info text-dark"><?= $art['cat'] ?></span></td>
                                    <td><span class="badge bg-secondary"><?= $art['age'] ?></span></td>
                                    <td><?= $art['etat'] ?></td>
                                </tr>
                                <?php endforeach; ?>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="card-footer bg-light d-flex justify-content-between small">
                    <span><strong>Poids total :</strong> <?= $box['poids_total'] ?> g</span>
                    <span><strong>Valeur estimée :</strong> <?= $box['prix_total'] ?> €</span>
                </div>
            </div>
        <?php endforeach; ?>

    </div>
</div>