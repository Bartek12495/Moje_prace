<?php
include 'config.php';

$komunikat = ""; // Zmienna na komunikaty o błędach lub sukcesie

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $imie = trim($_POST['imie']);
    $email = trim($_POST['email']);
    $haslo = trim($_POST['haslo']);
    $rola = 'uzytkownik'; 

    // Sprawdzenie, czy pola są wypełnione
    if (empty($imie) || empty($email) || empty($haslo)) {
        $komunikat = "Wypełnij wszystkie pola!";
    } elseif (strlen($haslo) < 8) {
        $komunikat = "Hasło musi mieć co najmniej 8 znaków!";
    } else {
        // Sprawdzenie, czy email już istnieje
        $check_email = $conn->prepare("SELECT id FROM uzytkownicy WHERE email = ?");
        $check_email->bind_param("s", $email);
        $check_email->execute();
        $check_email->store_result();

        if ($check_email->num_rows > 0) {
            $komunikat = "Email jest już zajęty!";
        } else {
            // Hashowanie hasła i zapis do bazy
            $hashed_password = password_hash($haslo, PASSWORD_DEFAULT);
            $stmt = $conn->prepare("INSERT INTO uzytkownicy (imie, email, haslo, rola) VALUES (?, ?, ?, ?)");
            $stmt->bind_param("ssss", $imie, $email, $hashed_password, $rola);

            if ($stmt->execute()) {
                // Przekierowanie na stronę logowania
                header("Location: logowanie.php?zarejestrowano=1");
                exit();
            } else {
                $komunikat = "Błąd podczas rejestracji!";
            }

            $stmt->close();
        }

        $check_email->close();
    }
}
?>

<!DOCTYPE html>
<html lang="pl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Rejestracja</title>
    <link rel="stylesheet" href="../css/style.css">
</head>
<body>

<div class="form-container">
    <h2>Rejestracja</h2>

    <?php if (!empty($komunikat)): ?>
        <p class="error"><?= htmlspecialchars($komunikat) ?></p>
    <?php endif; ?>

    <form method="POST">
        <input type="text" name="imie" placeholder="Imię" required>
        <input type="email" name="email" placeholder="Adres email" required>
        <input type="password" name="haslo" placeholder="Hasło (min. 8 znaków)" required>
        <button type="submit">Zarejestruj się</button>
    </form>

    <p>Masz już konto? <a href="logowanie.php">Zaloguj się!</a></p>
</div>

</body>
</html>