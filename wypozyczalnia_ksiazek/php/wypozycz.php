<?php
include 'config.php';
session_start();

// === Sprawdzenie, czy użytkownik jest zalogowany ===
if (!isset($_SESSION['user_id'])) {
    die("❌ Musisz być zalogowany, aby wypożyczyć książkę.");
}

// === Obsługa formularza ===
if ($_SERVER["REQUEST_METHOD"] === "POST") {
    // Pobranie danych z formularza
    $ksiazka_id = (int)$_POST['ksiazka_id']; 
    $uzytkownik_id = $_SESSION['user_id']; // domyślnie wypożyczający to aktualny użytkownik

    // 🔄 Sprawdzenie, czy bibliotekarz podał e-mail innego użytkownika
    if ($_SESSION['rola'] === 'bibliotekarz' && !empty($_POST['email_uzytkownika'])) {
        $email = trim($_POST['email_uzytkownika']);

        // Szukamy ID użytkownika po adresie e-mail
        $sql_user = "SELECT id FROM uzytkownicy WHERE email = ?";
        $stmt_user = $conn->prepare($sql_user);
        $stmt_user->bind_param("s", $email);
        $stmt_user->execute();
        $result_user = $stmt_user->get_result();

        if ($row_user = $result_user->fetch_assoc()) {
            $uzytkownik_id = $row_user['id']; // zmiana na ID znalezionego użytkownika
        } else {
            die("❌ Nie znaleziono użytkownika o podanym adresie e-mail.");
        }
        $stmt_user->close();
    }

    // Sprawdzenie, czy książka jest dostępna
    $sql = "SELECT ilosc FROM ksiazki WHERE id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $ksiazka_id);
    $stmt->execute();
    $stmt->store_result();
    $stmt->bind_result($ilosc);
    $stmt->fetch();

    if ($ilosc > 0) {
        // Dodanie wypożyczenia
        $sql_insert = "INSERT INTO wypozyczenia (ksiazka_id, uzytkownik_id, data_wypozyczenia) VALUES (?, ?, NOW())";
        $stmt_insert = $conn->prepare($sql_insert);
        $stmt_insert->bind_param("ii", $ksiazka_id, $uzytkownik_id);
        $stmt_insert->execute();
        $stmt_insert->close();

        // Zmniejszenie ilości dostępnych egzemplarzy
        $sql_update = "UPDATE ksiazki SET ilosc = ilosc - 1 WHERE id = ?";
        $stmt_update = $conn->prepare($sql_update);
        $stmt_update->bind_param("i", $ksiazka_id);
        $stmt_update->execute();
        $stmt_update->close();

        echo "✅ Książka została wypożyczona! <a href='wypozyczenia.php'>Przejdź do wypożyczeń</a>";
    } else {
        echo "❌ Brak dostępnych egzemplarzy!";
    }

    $stmt->close();
}
?>
