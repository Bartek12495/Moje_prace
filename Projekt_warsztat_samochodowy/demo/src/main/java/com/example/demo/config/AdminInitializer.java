//Tworzenie autoamtycznie konta Admin
package com.example.demo.config;

import com.example.demo.models.entities.User;
import com.example.demo.models.enums.Role;
import com.example.demo.respositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String defaultAdminUsername = "Admin";

            if (userRepository.findByUsername(defaultAdminUsername).isEmpty()) {
                User admin = new User();
                admin.setUsername(defaultAdminUsername);
                admin.setEmail("admin@example.com");
                admin.setPassword(passwordEncoder.encode("admin123")); // 👈 hasło startowe
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);
                System.out.println("✅ Utworzono konto admina: Admin / admin123");
            } else {
                System.out.println("ℹ️ Konto Admina już istnieje.");
            }
        };
    }
}
