<?php
include 'config.php';
session_start();

//Sprawdzenie, czy użytkownik jest zalogowany
if (!isset($_SESSION['user_id'])) {
    die("Musisz być zalogowany, aby oddać książkę.");
}

// Sprawdzenie, czy użytkownik jest bibliotekarzem lub administratorem
if (!isset($_SESSION['rola']) || !in_array($_SESSION['rola'], ['bibliotekarz', 'administrator'])) {
    die("Tylko bibliotekarz lub administrator może oddawać książki.");
}

// Obsługa formularza (kliknięcie przycisku "Oddaj książkę")
if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['wypozyczenie_id'])) {
    $wypozyczenie_id = intval($_POST['wypozyczenie_id']); // Pobranie ID wypożyczenia i konwersja na liczbę

    // Pobranie ID książki, którą chcemy oddać
    $sql = "SELECT ksiazka_id FROM wypozyczenia WHERE id = ? AND data_zwrotu IS NULL";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $wypozyczenie_id);
    $stmt->execute();
    $result = $stmt->get_result();

    // Sprawdzenie, czy wypożyczenie istnieje
    if ($result->num_rows > 0) {
        $row = $result->fetch_assoc();
        $ksiazka_id = $row['ksiazka_id'];
        $stmt->close();

        // Zaktualizuj wypożyczenie: ustaw datę zwrotu i status na "oddana"
        $sql_update_wypozyczenie = "UPDATE wypozyczenia SET data_zwrotu = NOW(), status = 'oddana' WHERE id = ?";
        $stmt_update = $conn->prepare($sql_update_wypozyczenie);
        $stmt_update->bind_param("i", $wypozyczenie_id);
        $stmt_update->execute();
        $stmt_update->close();

        // Zwiększenie ilości dostępnych książek w tabeli `ksiazki`
        $sql_update_ksiazki = "UPDATE ksiazki SET ilosc = ilosc + 1 WHERE id = ?";
        $stmt_update_ksiazki = $conn->prepare($sql_update_ksiazki);
        $stmt_update_ksiazki->bind_param("i", $ksiazka_id);
        $stmt_update_ksiazki->execute();
        $stmt_update_ksiazki->close();

        $_SESSION['success'] = "✅ Książka została zwrócona.";
    } else {
        $_SESSION['error'] = "❌ Nie znaleziono wypożyczenia.";
    }
}

header("Location: wypozyczenia.php");
exit();
?>