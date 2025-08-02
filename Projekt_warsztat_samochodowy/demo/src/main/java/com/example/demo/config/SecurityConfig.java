//Ten kod konfiguruje Spring Security tak, że endpointy /auth/** i /users są publiczne, reszta wymaga uwierzytelnienia przez HTTP Basic,
// hasła są szyfrowane BCrypt, a ochrona CSRF jest wyłączona.
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                /* ⬇⬇⬇  WŁĄCZAMY CORS  ⬇⬇⬇ */
                .cors(withDefaults())   // Włącza obsługę CORS (Cross-Origin Resource Sharing np.np. dla komunikacji między różnymi domenami)
                .csrf(csrf -> csrf.disable())   // Wyłącza CSRF (przydatne przy API, które nie korzysta z sesji)
                .authorizeHttpRequests(auth -> auth // Pozwala na dostęp bez autoryzacji do tych endpointów
                        .requestMatchers("/auth/**", "/users").permitAll()  //te endpoint /auth i /users bez autoryzacji reszta z autoryzacja
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
