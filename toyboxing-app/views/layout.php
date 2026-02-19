<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><?= $title ?? 'Toys Academy - ToyBoxing' ?></title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;600;700&display=swap" rel="stylesheet">
    
    <style>
        body {
            font-family: 'Nunito', sans-serif;
            background-color: #f4f7f6 !important;
            color: #212529 !important;
        }
        
        .navbar {
            background-color: #ffffff !important;
            border-bottom: 3px solid #38b2ac;
            box-shadow: 0 2px 10px rgba(0,0,0,0.04);
        }
        
        .navbar-brand, .nav-link {
            color: #2d3748 !important;
            font-weight: 600;
        }
        
        .nav-link:hover {
            color: #38b2ac !important;
        }

        .admin-link {
            color: #d97706 !important;
            background-color: #fef3c7;
            border-radius: 8px;
            padding: 8px 12px;
            transition: all 0.2s ease;
        }
        .admin-link:hover {
            background-color: #fde68a;
        }
        
        .card {
            background-color: #ffffff !important;
            border: 1px solid #edf2f7 !important;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.04) !important;
        }
        
        .btn-primary {
            background-color: #38b2ac;
            border-color: #38b2ac;
            border-radius: 8px;
            font-weight: 600;
            color: #ffffff;
        }
        .btn-primary:hover {
            background-color: #2c7a7b;
            border-color: #2c7a7b;
        }
        
        .form-control, .form-select {
            border-radius: 8px;
            border: 1px solid #cbd5e0;
            background-color: #ffffff !important;
            color: #4a5568 !important;
        }
        .form-control:focus, .form-select:focus {
            border-color: #38b2ac;
            box-shadow: 0 0 0 0.25rem rgba(56, 178, 172, 0.15);
        }
        
        h2, h5 {
            color: #2d3748;
            font-weight: 700;
        }
        
        .border-bottom {
            border-bottom: 1px solid #e2e8f0 !important;
        }
    </style>
</head>
<body>
    
    <nav class="navbar navbar-expand-lg">
        <div class="container">
            <a class="navbar-brand" href="/">Toys Academy</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto align-items-center">
                    <li class="nav-item"><a class="nav-link" href="/">S'inscrire</a></li>
                    <li class="nav-item"><a class="nav-link" href="/ma-box">Ma Box</a></li>
                    <li class="nav-item ms-lg-3 mt-2 mt-lg-0">
                        <a class="nav-link admin-link fw-bold" href="/admin/catalogue">⚙️ Back-office</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <main class="container my-5">
        <?= $content ?? '<p class="text-danger">Contenu introuvable.</p>' ?>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>