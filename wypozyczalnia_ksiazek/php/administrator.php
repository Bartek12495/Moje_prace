<?php
include 'config.php';
session_start();


// Sprawdzenie uprawnień
if (!isset($_SESSION['user_id']) || !isset($_SESSION['rola']) || $_SESSION['rola'] !== 'administrator') {
    die("❌ Nie masz uprawnień do tej strony.");
}

//  Obsługa usuwania książek
if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['usun_ksiazke'])) {
    $ksiazka_id = intval($_POST['ksiazka_id']);
    $stmt = $conn->prepare("DELETE FROM ksiazki WHERE id = ?");
    $stmt->bind_param("i", $ksiazka_id);
    if ($stmt->execute()) {
        $_SESSION['success'] = "✅ Książka została usunięta.";
    } else {
        $_SESSION['error'] = "Błąd: " . $stmt->error;
    }
    $stmt->close();
    header("Location: administrator.php");
    exit();
}

//  Obsługa zmiany roli użytkownika
if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['zmien_rola'])) {
    $user_id = intval($_POST['user_id']);
    $new_role = $_POST['rola'];

    //  Walidacja roli
    $dozwolone_role = ['uzytkownik', 'bibliotekarz', 'administrator'];
    if (!in_array($new_role, $dozwolone_role)) {
        die("Błąd: Nieprawidłowa rola użytkownika.");
    }

    $stmt = $conn->prepare("UPDATE uzytkownicy SET rola = ? WHERE id = ?");
    $stmt->bind_param("si", $new_role, $user_id);
    
    if ($stmt->execute()) {
        $_SESSION['success'] = "✅ Rola użytkownika została zmieniona.";
    } else {
        $_SESSION['error'] = "Błąd: " . $stmt->error;
    }
    $stmt->close();
    header("Location: administrator.php");
    exit();
}

// Pobranie listy książek i użytkowników
$ksiazki = $conn->query("SELECT * FROM ksiazki");
$uzytkownicy = $conn->query("SELECT id, imie, email, rola FROM uzytkownicy");
?>

<!DOCTYPE html>
<html lang="pl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel administratora</title>
    <link rel="stylesheet" href="../css/style.css">
</head>
<body>

<div class="wrapper">  
    <header>
        <div class="header-container">
            <img src="../img/logo.svg"  alt="Logo" class="logo">
            <h1>Panel administratora</h1>
        </div> 
    </header>

    <main>
        <!-- 🔹 Wyświetlanie komunikatów (sukces/błąd) -->
        <?php if (isset($_SESSION['success'])): ?>
            <p class="success"><?= $_SESSION['success']; ?></p>
            <?php unset($_SESSION['success']); ?>
        <?php endif; ?>
    
        <?php if (isset($_SESSION['error'])): ?>
            <p class="error"><?= $_SESSION['error']; ?></p>
            <?php unset($_SESSION['error']); ?>
        <?php endif; ?>

        <h2>Książki w systemie</h2>
        <?php while ($row = $ksiazki->fetch_assoc()): ?>
            <h3><?= htmlspecialchars($row['tytul']); ?></h3>
            <p><strong>Autor:</strong> <?= htmlspecialchars($row['autor']); ?></p>
            <p><strong>Ilość:</strong> <?= $row['ilosc']; ?></p>

            <!-- 🔹 Formularz do edycji książki -->
            <form action="edytuj_ksiazke.php" method="GET">
                <input type="hidden" name="ksiazka_id" value="<?= $row['id']; ?>">
                <button type="submit">📖 Edytuj książkę</button>
            </form>

            <!-- 🔹 Formularz do usuwania książki -->
            <form method="POST">
                <input type="hidden" name="ksiazka_id" value="<?= $row['id']; ?>">
                <button type="submit" name="usun_ksiazke">❌ Usuń książkę</button>
            </form>
        <hr>
        <?php endwhile; ?>

        <h2>Lista użytkowników</h2>
        <table border="1">
            <tr>
                <th>ID</th>
                <th>Imię</th>
                <th>Email</th>
                <th>Rola</th>
                <th>Zmiana roli</th>
            </tr>
            <?php while ($row = $uzytkownicy->fetch_assoc()): ?>
                <tr>
                    <td><?= $row['id']; ?></td>
                    <td><?= htmlspecialchars($row['imie']); ?></td>
                    <td><?= htmlspecialchars($row['email']); ?></td>
                    <td><?= htmlspecialchars($row['rola']); ?></td>
                    <td>
                        <!-- 🔹 Formularz do zmiany roli użytkownika -->
                        <form method="POST">
                            <input type="hidden" name="user_id" value="<?= $row['id']; ?>">
                            <select name="rola">
                                <option value="uzytkownik" <?= $row['rola'] == 'uzytkownik' ? 'selected' : ''; ?>>Użytkownik</option>
                                <option value="bibliotekarz" <?= $row['rola'] == 'bibliotekarz' ? 'selected' : ''; ?>>Bibliotekarz</option>
                                <option value="administrator" <?= $row['rola'] == 'administrator' ? 'selected' : ''; ?>>Administrator</option>
                            </select>
                            <button type="submit" name="zmien_rola">🔄 Zmień rolę</button>
                        </form>
                    </td>
                </tr>
            <?php endwhile; ?>
        </table>

        <br>
        <a href="../index.php">Powrót do strony głównej</a>
    </main>

    <footer>
        <p>&copy; 2025 Wypożyczalnia książek</p>
    </footer>
</div>
</body>
</html>