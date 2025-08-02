package com.example.demo.respositories;

import com.example.demo.models.entities.User;
import com.example.demo.models.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Znajdź wszystkich użytkowników o określonej roli
    List<User> findByRole(Role role);

    // Znajdź użytkownika po jego nazwie użytkownika (loginie)
    // Zwraca Optional – może być pusty, jeśli użytkownik nie istnieje
    Optional<User> findByUsername(String username);
}



