
package com.example.demo.controllers;

import com.example.demo.models.dtos.UserDto;
import org.springframework.security.core.Authentication;
import com.example.demo.models.enums.Role;
import com.example.demo.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    // 1. Tworzenie użytkownika  REJESTRACJA  – każdy nowy user ⇒ rola KLIENT

    @PostMapping
    public ResponseEntity<UserDto> register(@RequestBody UserDto userDto) {

        /*  Ignorujemy rolę przesłaną w JSON-ie:  */
        userDto.setRole(Role.KLIENT);

        UserDto created = userService.create(userDto);
        return ResponseEntity.ok(created);
    }


    // 2. Pobieranie wszystkich użytkowników
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAll());
    }


    // 3. Pobieranie użytkownika po ID
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getById(id));
    }


    // 4. Pobieranie użytkowników po roli
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDto>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.getByRole(role.toUpperCase()));
    }


      //5. ZMIANA ROLI (ADMIN): PATCH "role": "MECHANIK"  lub  "ADMIN"

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/role")
    public ResponseEntity<UserDto> changeRole(@PathVariable Integer id,
                                              @RequestBody RoleWrapper body) {

        Role newRole = Role.valueOf(body.getRole().toUpperCase());
        UserDto updated = userService.changeUserRole(id, newRole);
        return ResponseEntity.ok(updated);
    }

    /* klasa pomocnicza do PATCH /role ------- */
    private static class RoleWrapper {
        private String role;
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    //6.Usuwanie użytkownika
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    //7.AKTUALNIE ZALOGOWANY UŻYTKOWNIK -potrzebny do frontend
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication auth) {
        String username = auth.getName();                       // poprawny getter
        return ResponseEntity.ok(userService.getByUsername(username));
    }
}