//Spring Security korzysta z tej klasy, żeby podczas logowania sprawdzić, czy użytkownik istnieje i jakie ma uprawnienia.
package com.example.demo.services;

import com.example.demo.models.entities.User;
import com.example.demo.respositories.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String roleName = "ROLE_" + user.getRole().name(); // np. ROLE_KLIENT

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(roleName))
        );
    }
}
