<?php
include 'config.php';
session_start();

// Sprawdzenie uprawnień
if (!isset($_SESSION['user_id']) || !in_array($_SESSION['rola'], ['bibliotekarz', 'administrator'])) {
    header("Location: logowanie.php");
    exit();
}

// Sprawdzenie przesłanych danych
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['wypozyczenie_id'], $_POST['status'])) {
    $wypozyczenie_id = intval($_POST['wypozyczenie_id']);
    $status = $_POST['status'];

    //Dozwolone wartości 
    $dozwolone_statusy = ['zarezerwowana', 'wypozyczona'];
    if (!in_array($status, $dozwolone_statusy)) {
        die("❌ Nieprawidłowy status.");
    }

    //Aktualizuj tylko status
    $stmt = $conn->prepare("UPDATE wypozyczenia SET status = ? WHERE id = ?");
    $stmt->bind_param("si", $status, $wypozyczenie_id);

    if ($stmt->execute()) {
        header("Location: wypozyczenia.php");
        exit();
    } else {
        echo "❌ Błąd przy aktualizacji: " . $stmt->error;
    }

    $stmt->close();
} else {
    echo "❌ Nieprawidłowe dane.";
}
?>
