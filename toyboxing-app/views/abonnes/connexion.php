<div class="row justify-content-center">
    <div class="col-12 col-md-6 col-lg-4 mt-5">
        <div class="card shadow-sm border-0">
            <div class="card-body p-4">
                <h2 class="text-center text-primary mb-4">Se connecter</h2>
                
                <?php if (!empty($error)): ?>
                    <div class="alert alert-danger text-center small fw-bold">
                        <?= htmlspecialchars($error) ?>
                    </div>
                <?php endif; ?>

                <form action="/connexion" method="POST">
                    <div class="mb-4">
                        <label for="email" class="form-label fw-bold">Adresse email</label>
                        <input type="email" class="form-control" id="email" name="email" required placeholder="votre@email.com">
                    </div>
                    
                    <button type="submit" class="btn btn-primary w-100 mb-3">Retrouver ma box</button>
                    
                    <div class="text-center mt-3">
                        <a href="/" class="text-muted small text-decoration-none">Je n'ai pas encore de compte</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>