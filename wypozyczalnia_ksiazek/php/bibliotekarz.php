<?php
include 'config.php';
session_start();

// === Sprawdzenie uprawnień użytkownika ===
if (!isset($_SESSION['user_id']) || !isset($_SESSION['rola']) || $_SESSION['rola'] !== 'bibliotekarz') {
    die("❌ Nie masz uprawnień do tej strony.");
}

// === Obsługa usuwania książki ===
if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['usun_ksiazke'])) {
    $ksiazka_id = intval($_POST['ksiazka_id']); // Konwersja na int dla bezpieczeństwa

    $stmt = $conn->prepare("DELETE FROM ksiazki WHERE id = ?");
    $stmt->bind_param("i", $ksiazka_id);

    if ($stmt->execute()) {
        $_SESSION['success'] = "✅ Książka została usunięta.";
    } else {
        $_SESSION['error'] = "❌ Błąd: " . $stmt->error;
    }

    $stmt->close();
    header("Location: bibliotekarz.php");
    exit;
}

// ===  Pobranie listy książek ===
$ksiazki = $conn->query("SELECT * FROM ksiazki");
?>

<!DOCTYPE html>
<html lang="pl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel bibliotekarza</title>
    <link rel="stylesheet" href="../css/style.css">
</head>
<body>

<div class="wrapper">  
    <header>
        <div class="header-container">
            <img src="../img/logo.svg"  alt="Logo" class="logo">
            <h1>📚 Panel bibliotekarza</h1>
        </div>
    </header>

    <main>
        <!--  Wyświetlanie komunikatów (sukces/błąd) -->
        <?php if (isset($_SESSION['success'])): ?>
            <p class="success"><?= $_SESSION['success']; ?></p>
            <?php unset($_SESSION['success']); ?>
        <?php endif; ?>
    
        <?php if (isset($_SESSION['error'])): ?>
            <p class="error"><?= $_SESSION['error']; ?></p>
            <?php unset($_SESSION['error']); ?>
        <?php endif; ?>

        <h2>📖 Książki w systemie</h2>

        <!--Prezentacja książek w tabeli -->
        <table>
            <thead>
                <tr>
                    <th>Tytuł</th>
                    <th>Autor</th>
                    <th>Ilość</th>
                    <th>Akcje</th>
                </tr>
            </thead>
            <tbody>
                <?php while ($row = $ksiazki->fetch_assoc()): ?>
                    <tr>
                        <td><?= htmlspecialchars($row['tytul']); ?></td>
                        <td><?= htmlspecialchars($row['autor']); ?></td>
                        <td><?= $row['ilosc']; ?></td>
                        <td>
                            <!-- 🔹 Przycisk edycji -->
                            <form action="edytuj_ksiazke.php" method="GET" style="display:inline;">
                                <input type="hidden" name="ksiazka_id" value="<?= $row['id']; ?>">
                                <button type="submit">✏ Edytuj</button>
                            </form>

                            <!-- 🔹 Przycisk usuwania z potwierdzeniem -->
                            <form method="POST" style="display:inline;">
                                <input type="hidden" name="ksiazka_id" value="<?= $row['id']; ?>">
                                <button type="submit" name="usun_ksiazke" onclick="return confirm('Czy na pewno chcesz usunąć tę książkę?');">
                                    ❌ Usuń
                                </button>
                            </form>
                        </td>
                    </tr>
                <?php endwhile; ?>
            </tbody>
        </table>

        <br>
        <a href="../index.php">⬅ Powrót do strony głównej</a>
    </main>

    <footer>
        <p>&copy; 2025 Wypożyczalnia książek</p>
    </footer>
</div>
</body>
</html>
