package com.example.demo.respositories;

import com.example.demo.models.entities.Car;
import com.example.demo.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Repozytorium – służy do połączenia z bazą danych
@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {
    //Ta metoda automatycznie generuje zapytanie SQL, które znajduje wszystkie samochody przypisane do konkretnego właściciela (User)
    List<Car> findByOwner(User ownerId);
}