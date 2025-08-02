<?php
session_start();

// Sprawdzenie, czy użytkownik jest zalogowany
$userLoggedIn = isset($_SESSION['user_id']);

// Sprawdzenie ról użytkownika (czy jest administratorem lub bibliotekarzem)
$isAdmin = ($userLoggedIn && isset($_SESSION['rola']) && $_SESSION['rola'] === 'administrator');
$isLibrarian = ($userLoggedIn && isset($_SESSION['rola']) && $_SESSION['rola'] === 'bibliotekarz');

// Pobranie imienia użytkownika, jeśli jest zalogowany
$userName = $userLoggedIn ? $_SESSION['imie'] : null;
?>

<!DOCTYPE html>
<html lang="pl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Wypożyczalnia książek</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="wrapper">
    <!-- === Nagłówek strony === -->
    <header>
        <div class="header-container">
            <img src="img/logo.svg" alt="Logo" class="logo">
            <h1>Wypożyczalnia książek</h1>
        </div>
        
        <!-- === Nawigacja === -->
        <nav>
            <ul>
                <li><a href="index.php">🏠 Strona główna</a></li>
                <li><a href="php/przegladanie.php">📖  Przeglądaj książki</a></li>

                <?php if ($userLoggedIn): ?>
                    <!-- Link do wypożyczeń dostępny tylko po zalogowaniu -->
                    <li><a href="php/wypozyczenia.php">📂 Wypożyczenia</a></li>

                    <!-- Dostępne dla administratorów i bibliotekarzy -->
                    <?php if ($isAdmin || $isLibrarian): ?>
                        <li><a href="php/dodaj_ksiazke.php">➕ Dodaj książkę</a></li>
                    <?php endif; ?>

                    <!-- Panel administratora - tylko dla administratora -->
                    <?php if ($isAdmin): ?>
                        <li><a href="php/administrator.php">⚙️ Panel administratora</a></li>
                    <?php endif; ?>

                    <!-- Panel bibliotekarza - tylko dla bibliotekarza -->
                    <?php if ($isLibrarian): ?>
                        <li><a href="php/bibliotekarz.php">📋 Panel bibliotekarza</a></li>
                    <?php endif; ?>

                    <!-- Opcja wylogowania -->
                    <li><a href="php/wylogowanie.php">🚪 Wyloguj się (<?= htmlspecialchars($userName); ?>)</a></li>
                <?php else: ?>
                    <!-- Linki dla użytkowników niezalogowanych -->
                    <li><a href="php/logowanie.php">🔑 Logowanie</a></li>
                    <li><a href="php/rejestracja.php">📝 Rejestracja</a></li>
                <?php endif; ?>
            </ul>
        </nav>
    </header>

    <!-- ===  Główna treść strony === -->
    <main>
        <section>
            <h2>Witaj w wypożyczalni książek</h2>
            <p>Przeglądaj i rezerwuj książki online!</p>
            <a class="button" href="php/przegladanie.php">Przeglądaj książki</a>

            <?php if ($userLoggedIn): ?>
                <!-- Informacja o zalogowanym użytkowniku -->
                <p>Zalogowany jako: <strong><?= htmlspecialchars($userName); ?></strong></p>
            <?php endif; ?>
        </section>
    </main>

    <!-- === Stopka strony === -->
    <footer>
        <p>&copy; <?= date('Y'); ?> Wypożyczalnia książek</p>
    </footer>
</div>
</body>
</html>