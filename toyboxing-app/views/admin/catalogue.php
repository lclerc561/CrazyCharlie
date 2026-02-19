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
                        <th><a href="?sort=categorie&order=<?= ($currentSort === 'categorie' && $currentOrder === 'ASC') ? 'DESC' : 'ASC' ?>">Catégorie</a></th>
                        <th><a href="?sort=age&order=<?= ($currentSort === 'age' && $currentOrder === 'ASC') ? 'DESC' : 'ASC' ?>">Âge</a></th>
                        <th><a href="?sort=etat&order=<?= ($currentSort === 'etat' && $currentOrder === 'ASC') ? 'DESC' : 'ASC' ?>">État</a></th>
                        <th><a href="?sort=prix&order=<?= ($currentSort === 'prix' && $currentOrder === 'ASC') ? 'DESC' : 'ASC' ?>">Prix</a></th>
                        <th><a href="?sort=poids&order=<?= ($currentSort === 'poids' && $currentOrder === 'ASC') ? 'DESC' : 'ASC' ?>">Poids</a></th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <?php if (!empty($articles)): ?>
                        <?php foreach ($articles as $art): ?>
                        <tr>
                            <td class="text-muted fw-bold">#<?= htmlspecialchars($art['id']) ?></td>
                            <td class="fw-bold"><?= htmlspecialchars($art['libelle']) ?></td>
                            <td><span class="badge bg-info text-dark"><?= htmlspecialchars($art['categorie_nom'] ?? 'N/A') ?></span></td>
                            <td><span class="badge bg-secondary"><?= htmlspecialchars($art['age']) ?></span></td>
                            <td><?= htmlspecialchars($art['etat'] ?? 'Non précisé') ?></td>
                            <td><?= htmlspecialchars($art['prix']) ?> €</td>
                            <td><?= htmlspecialchars($art['poids']) ?> g</td>
                            <td>
                                <button class="btn btn-sm btn-outline-secondary" disabled>Modifier</button>
                            </td>
                        </tr>
                        <?php endforeach; ?>
                    <?php else: ?>
                        <tr>
                            <td colspan="8" class="text-center py-4 text-muted">Aucun article dans la base de données.</td>
                        </tr>
                    <?php endif; ?>
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