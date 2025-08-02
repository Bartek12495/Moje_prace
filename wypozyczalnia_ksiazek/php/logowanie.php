

<?php
include 'config.php';
session_start();

$komunikat = ""; // Zmienna na komunikaty o błędach lub sukcesie

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $email = trim($_POST['email']);
    $haslo = trim($_POST['haslo']);

    // Sprawdzenie czy pola nie są puste
    if (empty($email) || empty($haslo)) {
        $komunikat = "Wypełnij wszystkie pola!";
    } else {
        $stmt = $conn->prepare("SELECT id, imie, haslo, rola FROM uzytkownicy WHERE email = ?");
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $stmt->store_result();
        $stmt->bind_result($id, $imie, $hashed_password, $rola);

        if ($stmt->num_rows > 0) {
            $stmt->fetch();
            if (password_verify($haslo, $hashed_password)) {
                $_SESSION['user_id'] = $id;
                $_SESSION['imie'] = $imie;
                $_SESSION['rola'] = $rola;

                // Przekierowanie na stronę główną po zalogowaniu
                header("Location: ../index.php");
                exit();
            } else {
                $komunikat = "Błędne hasło!";
            }
        } else {
            $komunikat = "Nie znaleziono użytkownika!";
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
    <title>Logowanie</title>
    <link rel="stylesheet" href="../css/style.css">
</head>
<body>

<div class="form-container">
    <h2>Logowanie</h2>

    <?php if (!empty($komunikat)): ?>
        <p class="error"><?= htmlspecialchars($komunikat) ?></p>
    <?php endif; ?>

    <form method="POST">
        <input type="email" name="email" placeholder="Adres email" required>
        <input type="password" name="haslo" placeholder="Hasło" required>
        <button type="submit">Zaloguj się</button>
    </form>

    <p>Nie masz konta? <a href="rejestracja.php">Zarejestruj się!</a></p>
</div>

</body>
</html>