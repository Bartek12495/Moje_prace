<?php
include 'config.php';
session_start();

// === Sprawdzenie uprawnień użytkownika ===
if (!isset($_SESSION['user_id']) || ($_SESSION['rola'] !== 'administrator' && $_SESSION['rola'] !== 'bibliotekarz')) {
    die("❌ Nie masz uprawnień do tej strony.");
}

// === Pobranie ID książki i sprawdzenie poprawności ===
$ksiazka_id = filter_input(INPUT_GET, 'ksiazka_id', FILTER_VALIDATE_INT);
if (!$ksiazka_id) {
    die("❌ Nieprawidłowe ID książki.");
}

// ===  Pobranie danych książki do edycji ===
$stmt = $conn->prepare("SELECT * FROM ksiazki WHERE id = ?");
$stmt->bind_param("i", $ksiazka_id);
$stmt->execute();
$result = $stmt->get_result();
$ksiazka = $result->fetch_assoc();

if (!$ksiazka) {
    die("❌ Nie znaleziono książki.");
}

// === Obsługa edycji książki ===
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $ksiazka_id = $_POST['ksiazka_id'];
    $tytul = $_POST['tytul'];
    $autor = $_POST['autor'];
    $opis = $_POST['opis'];
    $ilosc = (int) $_POST['ilosc'];

    // Sprawdzenie, czy pola nie są puste
    if (!$ksiazka_id || !$tytul || !$autor || !$opis || $ilosc === false || $ilosc < 1) {
        die("❌ Błąd: Wprowadź poprawne dane.");
    }

    // Aktualizacja książki w bazie
    $stmt = $conn->prepare("UPDATE ksiazki SET tytul = ?, autor = ?, opis = ?, ilosc = ? WHERE id = ?");
    $stmt->bind_param("sssii", $tytul, $autor, $opis, $ilosc, $ksiazka_id);

    if ($stmt->execute()) {
        echo "✅ Książka została zaktualizowana!";
    } else {
        echo "❌ Błąd: " . $stmt->error;
    }
}
?>

<!DOCTYPE html>
<html lang="pl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edytuj książkę</title>
    <link rel="stylesheet" href="../css/style.css">
</head>
<body>

<div class="wrapper">  
    <header>
        <h1>Edytuj książkę</h1>
    </header>

    <main>
        <form method="POST">
            <input type="hidden" name="ksiazka_id" value="<?= $ksiazka['id']; ?>">

            <label>Tytuł:</label><br>
            <input type="text" name="tytul" value="<?= htmlspecialchars($ksiazka['tytul']); ?>" required><br>

            <label>Autor:</label><br>
            <input type="text" name="autor" value="<?= htmlspecialchars($ksiazka['autor']); ?>" required><br>

            <label>Opis:</label><br>
            <textarea name="opis" required><?= htmlspecialchars($ksiazka['opis']); ?></textarea><br>

            <label>Ilość:</label><br>
            <input type="number" name="ilosc" value="<?= htmlspecialchars($ksiazka['ilosc']); ?>" min="1" required><br>

            <button type="submit">💾 Zapisz zmiany</button>
        </form>
        <br>
        <a href="<?= $_SESSION['rola'] === 'administrator' ? 'administrator.php' : 'bibliotekarz.php'; ?>">⬅ Powrót do panelu</a>
    </main>

    <footer>
        <p>&copy; 2025 Wypożyczalnia książek</p>
    </footer>

</div>

</body>
</html>