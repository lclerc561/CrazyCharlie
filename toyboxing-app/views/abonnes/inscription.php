<div class="row justify-content-center">
    <div class="col-12 col-md-8 col-lg-6">
        <div class="card shadow-sm border-0 mt-4">
            <div class="card-body p-4">
                <h2 class="card-title text-center text-primary mb-4">Demander ma Toy Box</h2>
                <p class="text-muted text-center mb-4">Inscrivez-vous pour recevoir une box de jouets reconditionnés adaptée à votre enfant.</p>

                <?php if (!empty($error)): ?>
                    <div class="alert alert-danger fw-bold text-center">
                        ⚠️ <?= htmlspecialchars($error) ?>
                    </div>
                <?php endif; ?>
                <form action="/inscription" method="POST">
                    
                    <h5 class="mb-3 border-bottom pb-2">Vos informations</h5>
                    <div class="row g-3 mb-3">
                        <div class="col-6">
                            <label for="prenom" class="form-label">Prénom</label>
                            <input type="text" class="form-control" id="prenom" name="prenom" required>
                        </div>
                        <div class="col-6">
                            <label for="nom" class="form-label">Nom</label>
                            <input type="text" class="form-control" id="nom" name="nom" required>
                        </div>
                    </div>
                    
                    <div class="mb-4">
                        <label for="email" class="form-label">Adresse email</label>
                        <input type="email" class="form-control" id="email" name="email" placeholder="pour retrouver votre box plus tard..." required>
                    </div>

                    <h5 class="mb-3 border-bottom pb-2">Votre enfant</h5>
                    <div class="mb-4">
                        <label for="tranche_age" class="form-label">Tranche d'âge</label>
                        <select class="form-select" id="tranche_age" name="tranche_age" required>
                            <option value="">Choisir une tranche d'âge...</option>
                            <option value="BB">Bébé (0-3 ans)</option>
                            <option value="PE">Petit enfant (3-6 ans)</option>
                            <option value="EN">Enfant (6-10 ans)</option>
                            <option value="AD">Adolescent (10+ ans)</option>
                        </select>
                    </div>

                    <h5 class="mb-3 border-bottom pb-2">Vos préférences</h5>
                    <p class="small text-muted mb-3">Classez les catégories de la plus souhaitée (Choix 1) à la moins souhaitée (Choix 6).</p>
                    
                    <?php 
                    $categories = [
                        'SOC' => 'Jeux de société',
                        'FIG' => 'Figurines et poupées',
                        'CON' => 'Jeux de construction',
                        'EXT' => 'Jeux d\'extérieur',
                        'EVL' => 'Jeux d\'éveil et éducatifs',
                        'LIV' => 'Livres jeunesse'
                    ];
                    
                    for ($i = 1; $i <= 6; $i++): 
                    ?>
                        <div class="mb-2 row align-items-center">
                            <label for="pref_<?= $i ?>" class="col-4 col-form-label text-end">Choix <?= $i ?></label>
                            <div class="col-8">
                                <select class="form-select form-select-sm" id="pref_<?= $i ?>" name="preferences[]" required>
                                    <option value="">Sélectionner...</option>
                                    <?php foreach ($categories as $code => $label): ?>
                                        <option value="<?= $code ?>"><?= $label ?> (<?= $code ?>)</option>
                                    <?php endforeach; ?>
                                </select>
                            </div>
                        </div>
                    <?php endfor; ?>

                    <div class="d-grid gap-2 mt-5">
                        <button type="submit" class="btn btn-primary btn-lg">Valider mes préférences</button>
                    </div>

                </form>
            </div>
        </div>
    </div>
</div>