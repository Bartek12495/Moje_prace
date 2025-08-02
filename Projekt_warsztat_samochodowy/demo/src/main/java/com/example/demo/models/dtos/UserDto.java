package com.example.demo.models.dtos;

import com.example.demo.models.entities.User;
import com.example.demo.models.enums.Role;

public class UserDto {
    private Integer id;
    private String username;
    private String email;
    private String password;
    private Role role;


    // --- GETTERY I SETTERY ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public User toEntity() {
        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setRole(this.role);
        return user;
    }

    // --- KONWERSJE ---
    public static UserDto fromEntity(User user) {
        if (user == null) return null;
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        // NIE ustawiam hasła w DTO wychodzącym, dla bezpieczeństwa!
        return dto;
    }
}
