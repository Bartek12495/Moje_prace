package com.example.demo.services;

import com.example.demo.models.dtos.UserDto;
import com.example.demo.models.entities.User;
import com.example.demo.models.enums.Role;
import com.example.demo.respositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public UserDto create(UserDto dto){
        User user = dto.toEntity();
        user.setRole(Role.KLIENT);  //Ustawienie dla zalogowanego użytkownika roli:KLIENT
        user.setPassword(passwordEncoder.encode(user.getPassword()));//  zakoduj hasło przed zapisem
        userRepository.save(user);
        return UserDto.fromEntity(user);
    }

    // 2. Pobieranie użytkownika po ID
    public UserDto getById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserDto.fromEntity(user);
    }

    // 3. Pobieranie wszystkich użytkowników
    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 4. Pobieranie użytkowników po roli (np. MECHANIK, KLIENT)
    public List<UserDto> getByRole(String role) {
        Role roleEnum = Role.valueOf(role); // Rzuca wyjątek jeśli nie istnieje
        return userRepository.findByRole(roleEnum)
                .stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }
    //5. ZMIANA ROLI (ADMIN): PATCH "role": "MECHANIK"  lub  "ADMIN"
    public UserDto changeUserRole(Integer id, Role newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(newRole);
        userRepository.save(user);
        return UserDto.fromEntity(user);
    }

    //6.Usuniecie uzytkownika
    public void deleteUserById(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Użytkownik o ID " + id + " nie istnieje");
        }
        userRepository.deleteById(id);
    }
    //7. Pobranie po username
    public UserDto getByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
