<div class="row justify-content-center">
    <div class="col-12 col-md-8">
        
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>Ajouter un article</h2>
            <a href="/admin/catalogue" class="btn btn-outline-secondary">Retour au catalogue</a>
        </div>

        <div class="card p-4">
            <form action="/admin/article/ajouter" method="POST">
                
                <div class="mb-4">
                    <label for="designation" class="form-label fw-bold">Désignation de l'article</label>
                    <input type="text" class="form-control" id="designation" name="designation" placeholder="Ex: Monopoly Junior, Peluche Ours..." required>
                </div>

                <div class="row g-3 mb-4">
                    <div class="col-12 col-md-6">
                        <label for="categorie" class="form-label fw-bold">Catégorie</label>
                        <select class="form-select" id="categorie" name="categorie" required>
                            <option value="">Sélectionner...</option>
                            <option value="1">Jeux de société (SOC)</option>
                            <option value="2">Figurines et poupées (FIG)</option>
                            <option value="3">Jeux de construction (CON)</option>
                            <option value="4">Jeux d'extérieur (EXT)</option>
                            <option value="5">Jeux d'éveil et éducatifs (EVL)</option>
                            <option value="6">Livres jeunesse (LIV)</option>
                        </select>
                    </div>
                    
                    <div class="col-12 col-md-6">
                        <label for="tranche_age" class="form-label fw-bold">Tranche d'âge cible</label>
                        <select class="form-select" id="tranche_age" name="tranche_age" required>
                            <option value="">Sélectionner...</option>
                            <option value="BB">Bébé (0-3 ans) - BB</option>
                            <option value="PE">Petit enfant (3-6 ans) - PE</option>
                            <option value="EN">Enfant (6-10 ans) - EN</option>
                            <option value="AD">Adolescent (10+ ans) - AD</option>
                        </select>
                    </div>
                </div>

                <div class="row g-3 mb-4">
                    <div class="col-12 col-md-4">
                        <label for="etat" class="form-label fw-bold">État</label>
                        <select class="form-select" id="etat" name="etat" required>
                            <option value="">Sélectionner...</option>
                            <option value="N">Neuf (N)</option>
                            <option value="TB">Très bon état (TB)</option>
                            <option value="B">Bon état (B)</option>
                        </select>
                    </div>

                    <div class="col-12 col-md-4">
                        <label for="prix" class="form-label fw-bold">Prix estimé (€)</label>
                        <input type="number" step="1" min="0" class="form-control" id="prix" name="prix" placeholder="0" required>
                    </div>

                    <div class="col-12 col-md-4">
                        <label for="poids" class="form-label fw-bold">Poids (grammes)</label>
                        <input type="number" step="1" min="0" class="form-control" id="poids" name="poids" placeholder="Ex: 400" required>
                    </div>
                </div>

                <div class="d-grid gap-2 mt-4">
                    <button type="submit" class="btn btn-primary btn-lg">Enregistrer l'article dans le stock</button>
                </div>
                
            </form>
        </div>
    </div>
</div>