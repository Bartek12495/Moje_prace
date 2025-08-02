<?php
include 'config.php';
session_start();

// ✅ Sprawdzenie, czy użytkownik ma odpowiednie uprawnienia (bibliotekarz lub administrator)
if (!isset($_SESSION['rola']) || !in_array($_SESSION['rola'], ['bibliotekarz', 'administrator'])) {
    die("❌ Nie masz uprawnień do dodawania książek!");
}
// ===  Obsługa formularza po wysłaniu ===
if ($_SERVER["REQUEST_METHOD"] == "POST") {  
    $tytul = $_POST['tytul'];
    $autor = $_POST['autor'];
    $opis = $_POST['opis'];
    $ilosc = (int) $_POST['ilosc'];
    $dodal_uzytkownik_id = $_SESSION['user_id'];

    // === Sprawdzenie, czy dane są poprawne ===
    if (!$tytul || !$autor || !$opis) {
        echo "❌ Wszystkie pola muszą być wypełnione!";
    } elseif ($ilosc === false || $ilosc < 1) {
        echo "❌ Ilość książek musi być liczbą większą niż 0!";
    } else {
        // === Dodanie książki do bazy danych ===
        $stmt = $conn->prepare("INSERT INTO ksiazki (tytul, autor, opis, ilosc, dodal_uzytkownik_id) VALUES (?, ?, ?, ?, ?)");
        $stmt->bind_param("sssii", $tytul, $autor, $opis, $ilosc, $dodal_uzytkownik_id);

        if ($stmt->execute()) {
            echo "✅ Książka została dodana pomyślnie!";
        } else {
            echo "❌ Błąd podczas dodawania książki: " . $stmt->error;
        }
        $stmt->close();
    }
}
?>

<!DOCTYPE html>
<html lang="pl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dodaj książkę</title>
    <link rel="stylesheet" href="../css/style.css">
</head>
<body>

<div class="wrapper">  
    <header>
        <div class="header-container">
            <img src="../img/logo.svg"  alt="Logo" class="logo">
            <h1>📖 Dodaj nową książkę</h1>
        </div>
    </header>

    <main>
        <form method="POST">
            <label for="tytul">Tytuł:</label><br>
            <input type="text" id="tytul" name="tytul" required><br>

            <label for="autor">Autor:</label><br>
            <input type="text" id="autor" name="autor" required><br>

            <label for="opis">Opis:</label><br>
            <textarea id="opis" name="opis" required></textarea><br>

            <label for="ilosc">Ilość:</label><br>
            <input type="number" id="ilosc" name="ilosc" min="1" required><br>

            <button type="submit">➕ Dodaj książkę</button>
        </form>
        <br>
        <a href="przegladanie.php">⬅ Powrót do listy książek</a>
    </main>

    <footer>
        <p>&copy; 2025 Wypożyczalnia książek</p>
    </footer>
</div>

</body>
</html>
