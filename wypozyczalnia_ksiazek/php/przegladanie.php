<?php
include 'config.php';
session_start();

// ===  Pobranie wszystkich książek z bazy danych ===
$sql = "SELECT * FROM ksiazki";
$result = $conn->query($sql);

?>

<!DOCTYPE html>
<html lang="pl">
<head>
    
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Przeglądaj książki</title>
    <link rel="stylesheet" href="../css/style.css">
</head>
<body>

<div class="wrapper"> 
    <!-- === Nagłówek i nawigacja === -->
    <header>
        <div class="header-container">
            <img src="../img/logo.svg"  alt="Logo" class="logo">
            <h1>Przeglądaj książki</h1>
        </div>

        <nav>
            <ul>
                <li><a href="../index.php">🏠 Strona główna</a></li>
                <li><a href="przegladanie.php">📖 Przeglądaj książki</a></li>
                
                <!--  Menu dla zalogowanych użytkowników -->
                <?php if (isset($_SESSION['user_id'])): ?>
                    <li><a href="wypozyczenia.php">📂 Wypożyczenia</a></li>

                    <!--  Dostęp dla bibliotekarzy i administratorów -->
                    <?php if ($_SESSION['rola'] === 'administrator' || $_SESSION['rola'] === 'bibliotekarz'): ?>
                        <li><a href="dodaj_ksiazke.php">➕ Dodaj książkę</a></li>
                    <?php endif; ?>

                    <!--  Panel administratora -->
                    <?php if ($_SESSION['rola'] === 'administrator'): ?>
                        <li><a href="administrator.php">⚙️ Panel administratora</a></li>
                    <?php endif; ?>

                    <!--  Panel bibliotekarza -->
                    <?php if ($_SESSION['rola'] === 'bibliotekarz'): ?>
                        <li><a href="bibliotekarz.php">📋 Panel bibliotekarza</a></li>
                    <?php endif; ?>

                    <!-- Wylogowanie -->
                    <li><a href="wylogowanie.php">🚪 Wyloguj się (<?= htmlspecialchars($_SESSION['imie']); ?>)</a></li>
                <?php else: ?>
                    <li><a href="logowanie.php">🔑 Logowanie</a></li>
                    <li><a href="rejestracja.php">📝 Rejestracja</a></li>
                <?php endif; ?>
            </ul>
        </nav>
    </header>

    <!-- === Główna zawartość strony === -->
    <main>
        <h2>📚 Dostępne książki</h2>
        <hr>

        <?php if ($result->num_rows > 0): ?>
            <!--  Wyświetlenie listy książek -->
            <?php while ($row = $result->fetch_assoc()): ?>
                <h3><?= htmlspecialchars($row['tytul']); ?></h3>
                <p><strong>Autor:</strong> <?= htmlspecialchars($row['autor']); ?></p>
                <p><?= nl2br(htmlspecialchars($row['opis'])); ?></p>
                <p><strong>Ilość dostępna:</strong> <?= (int)$row['ilosc']; ?></p>

                <!--  Sekcja wypożyczania książki -->
                <?php if ($row['ilosc'] > 0): ?>
                    <?php if (isset($_SESSION['user_id'])): ?>
                        <form action="wypozycz.php" method="POST">
                            <input type="hidden" name="ksiazka_id" value="<?= (int)$row['id']; ?>">
                            
                            <!--  Dodatkowe pole dla bibliotekarza -->
                            <?php if ($_SESSION['rola'] === 'bibliotekarz'): ?>
                                <label for="email_uzytkownika">📧 E-mail użytkownika:</label>
                                <input type="email" id="email_uzytkownika" name="email_uzytkownika" required>
                            <?php endif; ?>

                            <button type="submit">📖 Wypożycz</button>
                        </form>
                    <?php else: ?>
                        <p><strong>🔐 Musisz być zalogowany, aby wypożyczyć książkę.</strong></p>
                    <?php endif; ?>
                <?php else: ?>
                    <p><strong>⛔ Brak dostępnych egzemplarzy.</strong></p>
                <?php endif; ?>
                
                <hr> <!--  Oddzielenie książek -->
            <?php endwhile; ?>
        <?php else: ?>
            <p>❌ Brak dostępnych książek w bazie.</p>
        <?php endif; ?>
    </main>

    <!-- ===  Stopka strony === -->
    <footer>
        <p>&copy; <?= date('Y'); ?> Wypożyczalnia książek</p>
    </footer>
</div>

</body>
</html>