<div class="d-flex justify-content-between align-items-center mb-4">
    <h2>Catalogue des articles</h2>
    <a href="/admin/article/ajouter" class="btn btn-primary">+ Ajouter un don</a>
</div>

<div class="card">
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Désignation</th>
                        <th>Catégorie</th>
                        <th>Âge</th>
                        <th>État</th>
                        <th>Prix</th>
                        <th>Poids</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <?php 
                    $articlesMock = [
                        ['id' => 'a1', 'nom' => 'Monopoly Junior', 'cat' => 'SOC', 'age' => 'PE', 'etat' => 'N', 'prix' => 8, 'poids' => 400],
                        ['id' => 'a2', 'nom' => 'Barbie Aventurière', 'cat' => 'FIG', 'age' => 'PE', 'etat' => 'TB', 'prix' => 5, 'poids' => 300],
                        ['id' => 'a3', 'nom' => 'Puzzle éducatif', 'cat' => 'EVL', 'age' => 'PE', 'etat' => 'TB', 'prix' => 7, 'poids' => 350],
                        ['id' => 'a6', 'nom' => 'Kapla 200 pièces', 'cat' => 'CON', 'age' => 'EN', 'etat' => 'B', 'prix' => 10, 'poids' => 600],
                        ['id' => 'a7', 'nom' => 'Cerf-volant Pirate', 'cat' => 'EXT', 'age' => 'EN', 'etat' => 'N', 'prix' => 6, 'poids' => 400],
                    ];

                    foreach ($articlesMock as $art): 
                    ?>
                    <tr>
                        <td class="text-muted fw-bold">#<?= $art['id'] ?></td>
                        <td class="fw-bold"><?= $art['nom'] ?></td>
                        <td><span class="badge bg-info text-dark"><?= $art['cat'] ?></span></td>
                        <td><span class="badge bg-secondary"><?= $art['age'] ?></span></td>
                        <td><?= $art['etat'] ?></td>
                        <td><?= $art['prix'] ?> €</td>
                        <td><?= $art['poids'] ?> g</td>
                        <td>
                            <button class="btn btn-sm btn-outline-secondary" disabled>Modifier</button>
                        </td>
                    </tr>
                    <?php endforeach; ?>
                </tbody>
            </table>
        </div>
    </div>
</div>

<nav aria-label="Pagination catalogue" class="mt-4">
    <ul class="pagination justify-content-center">
        <li class="page-item disabled"><a class="page-link" href="#">Précédent</a></li>
        <li class="page-item active"><a class="page-link" href="#">1</a></li>
        <li class="page-item"><a class="page-link" href="#">2</a></li>
        <li class="page-item"><a class="page-link" href="#">3</a></li>
        <li class="page-item"><a class="page-link" href="#">Suivant</a></li>
    </ul>
</nav>