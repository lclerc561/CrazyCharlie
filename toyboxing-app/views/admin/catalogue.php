<?php
$currentCategorie = $currentCategorie ?? '';
$currentAge = $currentAge ?? '';
$currentSort = $currentSort ?? '';
$currentOrder = $currentOrder ?? 'ASC';

$queryParams = $_GET ?? [];
?>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h2>Catalogue des articles</h2>
    <div>
        <a href="/admin/campagne" class="btn btn-warning fw-bold text-dark me-2">🚀 Gérer la Campagne</a>
        <a href="/admin/article/ajouter" class="btn btn-primary">+ Ajouter un article</a>
    </div>
</div>

<div class="card">
    <div class="card-body">

        <!-- FILTRES -->
        <form method="GET" class="row g-3 mb-4">

            <div class="col-md-4">
                <label class="form-label">Catégorie</label>
                <select name="categorie" class="form-select">
                    <option value="">Toutes</option>
                    <option value="SOC" <?= $currentCategorie === 'SOC' ? 'selected' : '' ?>>SOC</option>
                    <option value="FIG" <?= $currentCategorie === 'FIG' ? 'selected' : '' ?>>FIG</option>
                    <option value="CON" <?= $currentCategorie === 'CON' ? 'selected' : '' ?>>CON</option>
                    <option value="EXT" <?= $currentCategorie === 'EXT' ? 'selected' : '' ?>>EXT</option>
                    <option value="EVL" <?= $currentCategorie === 'EVL' ? 'selected' : '' ?>>EVL</option>
                    <option value="LIV" <?= $currentCategorie === 'LIV' ? 'selected' : '' ?>>LIV</option>
                </select>
            </div>

            <div class="col-md-4">
                <label class="form-label">Tranche d'âge</label>
                <select name="age" class="form-select">
                    <option value="">Toutes</option>
                    <option value="BB" <?= $currentAge === 'BB' ? 'selected' : '' ?>>BB</option>
                    <option value="PE" <?= $currentAge === 'PE' ? 'selected' : '' ?>>PE</option>
                    <option value="EN" <?= $currentAge === 'EN' ? 'selected' : '' ?>>EN</option>
                    <option value="AD" <?= $currentAge === 'AD' ? 'selected' : '' ?>>AD</option>
                </select>
            </div>

            <div class="col-md-4 d-flex align-items-end">
                <button type="submit" class="btn btn-primary me-2">Filtrer</button>
                <a href="/admin/catalogue" class="btn btn-outline-secondary">Réinitialiser</a>
            </div>

        </form>

        <!-- TABLE -->
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Désignation</th>

                        <?php
                        function sortLink($column, $label, $currentSort, $currentOrder, $queryParams) {
                            $queryParams['sort'] = $column;
                            $queryParams['order'] =
                                ($currentSort === $column && $currentOrder === 'ASC') ? 'DESC' : 'ASC';

                            $arrow = '';
                            if ($currentSort === $column) {
                                $arrow = $currentOrder === 'ASC' ? ' ▲' : ' ▼';
                            }

                            return '<a href="?' . http_build_query($queryParams) . '">' . $label . $arrow . '</a>';
                        }
                        ?>

                        <th><?= sortLink('categorie', 'Catégorie', $currentSort, $currentOrder, $queryParams) ?></th>
                        <th><?= sortLink('age', 'Âge', $currentSort, $currentOrder, $queryParams) ?></th>
                        <th><?= sortLink('etat', 'État', $currentSort, $currentOrder, $queryParams) ?></th>
                        <th><?= sortLink('prix', 'Prix', $currentSort, $currentOrder, $queryParams) ?></th>
                        <th><?= sortLink('poids', 'Poids', $currentSort, $currentOrder, $queryParams) ?></th>

                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>

                    <?php if (!empty($articles)): ?>
                        <?php foreach ($articles as $art): ?>
                        <tr>
                            <td class="text-muted fw-bold">#<?= htmlspecialchars($art['id']) ?></td>
                            <td class="fw-bold"><?= htmlspecialchars($art['libelle']) ?></td>
                            <td>
                                <span class="badge bg-info text-dark">
                                    <?= htmlspecialchars($art['categorie_nom'] ?? 'N/A') ?>
                                </span>
                            </td>
                            <td>
                                <span class="badge bg-secondary">
                                    <?= htmlspecialchars($art['age']) ?>
                                </span>
                            </td>
                            <td><?= htmlspecialchars($art['etat'] ?? 'Non précisé') ?></td>
                            <td><?= htmlspecialchars($art['prix']) ?> €</td>
                            <td><?= htmlspecialchars($art['poids']) ?> g</td>
                            <td>
                                <a href="/admin/article/modifier?id=<?= $art['id'] ?>" 
                                    class="btn btn-sm btn-outline-secondary">
                                        Modifier
                                </a>
                                <a href="/admin/article/supprimer?id=<?= $art['id'] ?>" 
                                    class="btn btn-sm btn-outline-danger"
                                    onclick="return confirm('Voulez-vous vraiment supprimer cet article ?');">
                                        supprimer
                                </a>
                            </td>
                        </tr>
                        <?php endforeach; ?>
                    <?php else: ?>
                        <tr>
                            <td colspan="8" class="text-center py-4 text-muted">
                                Aucun article dans la base de données.
                            </td>
                        </tr>
                    <?php endif; ?>

                </tbody>
            </table>
        </div>

    </div>
</div>
