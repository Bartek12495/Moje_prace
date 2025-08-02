<?php
include 'config.php';
session_start();

// === Sprawdzenie, czy użytkownik jest zalogowany ===
if (!isset($_SESSION['user_id'])) {
    header("Location: logowanie.php");
    exit();
}

// Pobranie danych użytkownika
$user_id = $_SESSION['user_id'];
$rola = $_SESSION['rola'] ?? 'uzytkownik';
$isLibrarian = ($rola === 'bibliotekarz');
$isAdmin = ($rola === 'administrator');

// Przygotowanie zapytania SQL w zależności od roli
if ($isAdmin) {
    // Administrator widzi WSZYSTKIE wypożyczenia (bez ograniczeń)
    $sql = "SELECT 
                w.id AS wypozyczenie_id, 
                k.tytul, 
                k.autor, 
                w.data_wypozyczenia,
                w.data_zwrotu,
                w.status,
                u.imie AS wypozyczajacy,
                u.email AS wypozyczajacy_email
            FROM wypozyczenia w
            JOIN ksiazki k ON w.ksiazka_id = k.id
            JOIN uzytkownicy u ON w.uzytkownik_id = u.id
            ORDER BY w.data_wypozyczenia DESC";

} elseif ($isLibrarian) {
    // Bibliotekarz widzi tylko wypożyczenia, które nie zostały zwrócone
    $sql = "SELECT 
                w.id AS wypozyczenie_id, 
                k.tytul, 
                k.autor, 
                w.data_wypozyczenia,
                w.data_zwrotu,
                w.status,
                u.imie AS wypozyczajacy,
                u.email AS wypozyczajacy_email
            FROM wypozyczenia w
            JOIN ksiazki k ON w.ksiazka_id = k.id
            JOIN uzytkownicy u ON w.uzytkownik_id = u.id
            WHERE w.data_zwrotu IS NULL
            ORDER BY w.data_wypozyczenia DESC";

} else {
    // Użytkownik widzi tylko swoje wypożyczenia
    $sql = "SELECT 
                w.id AS wypozyczenie_id, 
                k.tytul, 
                k.autor, 
                w.data_wypozyczenia,
                w.data_zwrotu,
                w.status
            FROM wypozyczenia w
            JOIN ksiazki k ON w.ksiazka_id = k.id
            WHERE w.uzytkownik_id = ? 
            ORDER BY w.data_wypozyczenia DESC";
}

$stmt = $conn->prepare($sql);
if (!$stmt) {
    die("❌ Błąd zapytania: " . $conn->error);
}

if (!$isAdmin && !$isLibrarian) {
    $stmt->bind_param("i", $user_id);
}

$stmt->execute();
$result = $stmt->get_result();
?>

<!DOCTYPE html>
<html lang="pl">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Wypożyczenia</title>
    <link rel="stylesheet" href="../css/style.css" />
</head>
<body>
<div class="wrapper">

<header>
    <div class="header-container">
        <img src="../img/logo.svg" alt="Logo" class="logo" />
        <h1><?= ($isLibrarian || $isAdmin) ? 'Wszystkie wypożyczenia' : 'Moje wypożyczenia'; ?></h1>
    </div>
</header>

<main>
    <?php if ($result->num_rows > 0): ?>
        <?php while ($row = $result->fetch_assoc()): ?>
            <div class="book">
                <h3>📚 <?= htmlspecialchars($row['tytul']); ?></h3>
                <p><strong>Autor:</strong> <?= htmlspecialchars($row['autor']); ?></p>
                <p><strong>Data wypożyczenia:</strong> <?= htmlspecialchars($row['data_wypozyczenia']); ?></p>

                <?php if ($isLibrarian || $isAdmin): ?>
                    <p><strong>Wypożyczający:</strong> <?= htmlspecialchars($row['wypozyczajacy']); ?></p>
                    <p><strong>Email wypożyczającego:</strong> <?= htmlspecialchars($row['wypozyczajacy_email']); ?></p>
                <?php endif; ?>

                <p><strong>Data zwrotu:</strong> <?= $row['data_zwrotu'] ? htmlspecialchars($row['data_zwrotu']) : 'Niezwrócona'; ?></p>

                <p><strong>Status:</strong> <?= htmlspecialchars($row['status']); ?></p>

                <?php if ($isLibrarian || $isAdmin): ?>
                    <form action="zmien_status.php" method="POST" style="margin-bottom: 10px;">
                        <input type="hidden" name="wypozyczenie_id" value="<?= htmlspecialchars($row['wypozyczenie_id']); ?>">
                        <label for="status-<?= $row['wypozyczenie_id']; ?>">Status:</label>
                        <select name="status" id="status-<?= $row['wypozyczenie_id']; ?>">
                            <option value="zarezerwowana" <?= $row['status'] === 'zarezerwowana' ? 'selected' : '' ?>>Zarezerwowana</option>
                            <option value="wypozyczona" <?= $row['status'] === 'wypozyczona' ? 'selected' : '' ?>>Wypożyczona</option>
                            <!-- Usunięto status "oddana" -->
                        </select>
                        <button type="submit">Zapisz</button>
                    </form>

                    <!-- Przycisk "Oddaj książkę" tylko dla bibliotekarza, jeśli książka nie jest jeszcze zwrócona -->
                    <?php if (($isLibrarian || $isAdmin) && !$row['data_zwrotu']): ?>
                        <form action="oddaj_ksiazke.php" method="POST">
                            <input type="hidden" name="wypozyczenie_id" value="<?= htmlspecialchars($row['wypozyczenie_id']); ?>">
                            <button type="submit">Oddaj książkę</button>
                        </form>
                    <?php endif; ?>
                <?php endif; ?>
            </div>
            <hr>
        <?php endwhile; ?>
    <?php else: ?>
        <p>Brak wypożyczonych książek.</p>
    <?php endif; ?>

    <a href="../index.php" class="btn">Wróć do strony głównej</a>
</main>

<footer>
    <p>&copy; 2025 Wypożyczalnia książek</p>
</footer>

</div>
</body>
</html>