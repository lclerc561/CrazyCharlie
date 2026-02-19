<div class="row justify-content-center">
    <div class="col-12 col-lg-8">
        
        <?php if (!isset($abonne) || empty($abonne)): ?>
            <div class="text-center py-5">
                <h1 class="display-1">🧸</h1>
                <h2 class="mt-3">Oups, on ne se connaît pas encore !</h2>
                <p class="text-muted">Il semble que vous n'ayez pas encore demandé de Toy Box, ou que vous utilisiez un autre navigateur.</p>
                <a href="/" class="btn btn-primary mt-3">S'inscrire et demander ma box</a>
            </div>

        <?php else: ?>
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2>Bonjour <?= htmlspecialchars($abonne['prenom']) ?> !</h2>
                <span class="badge bg-primary fs-6">Enfant : <?= htmlspecialchars($abonne['tranche_age']) ?></span>
            </div>

            <?php if (empty($articles)): ?>
                <div class="card border-0 shadow-sm text-center py-5">
                    <div class="card-body">
                        <h1 class="display-4 mb-3">⏳</h1>
                        <h4 class="text-primary">Votre box est en cours de préparation...</h4>
                        <p class="text-muted mb-0">Nos équipes (et notre algorithme) travaillent dur pour sélectionner les meilleurs jouets en fonction de vos préférences. Revenez vérifier un peu plus tard !</p>
                    </div>
                </div>

            <?php else: ?>
                <div class="card border-0 shadow-sm mb-4">
                    <div class="card-header bg-white border-bottom-0 pt-4 pb-0">
                        <h4 class="text-success mb-0">Votre Toy Box est prête !</h4>
                        <p class="text-muted small">Voici les trésors que nous avons sélectionnés pour vous.</p>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th class="ps-4">Jouet</th>
                                        <th>Catégorie</th>
                                        <th>État</th>
                                        <th>Valeur est.</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <?php 
                                    $poidsTotal = 0;
                                    $prixTotal = 0;
                                    foreach ($articles as $art): 
                                        $poidsTotal += $art['poids'];
                                        $prixTotal += $art['prix'];
                                    ?>
                                    <tr>
                                        <td class="ps-4 fw-bold"><?= htmlspecialchars($art['libelle']) ?></td>
                                        <td><span class="badge bg-info text-dark"><?= htmlspecialchars($art['categorie_nom']) ?></span></td>
                                        <td><?= htmlspecialchars($art['etat']) ?></td>
                                        <td><?= htmlspecialchars($art['prix']) ?> €</td>
                                    </tr>
                                    <?php endforeach; ?>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div class="card-footer bg-light p-3 d-flex justify-content-around rounded-bottom">
                        <span class="text-muted"><strong>Poids de la box :</strong> <?= $poidsTotal ?> g</span>
                        <span class="text-muted"><strong>Valeur totale :</strong> <?= $prixTotal ?> €</span>
                    </div>
                </div>
                
                <div class="text-center">
                    <button class="btn btn-outline-success">Confirmer la réception</button>
                </div>
            <?php endif; ?>

        <?php endif; ?>

    </div>
</div>