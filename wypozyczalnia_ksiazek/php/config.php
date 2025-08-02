<?php
// === KONFIGURACJA BAZY DANYCH ===
$host = "localhost"; 
$user = "root"; 
$pass = ""; 
$db = "wypozyczalnia_db"; 

// === NAWIĄZANIE POŁĄCZENIA ===
$conn = new mysqli($host, $user, $pass, $db);

// === SPRAWDZENIE BŁĘDÓW POŁĄCZENIA ===
if ($conn->connect_error) {
    die("Błąd połączenia: " . $conn->connect_error);
}

// === USTAWIENIE KODOWANIA ZNAKÓW ===
$conn->set_charset("utf8");

?>