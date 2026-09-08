package com.hugo.mabibli.service;

import com.hugo.mabibli.dto.RegisterRequest;
import com.hugo.mabibli.dto.LoginRequest;
import com.hugo.mabibli.dto.AuthResponse;
import com.hugo.mabibli.entity.User;
import com.hugo.mabibli.exception.UsernameAlreadyExistsException;
import com.hugo.mabibli.repository.UserRepository;
import com.hugo.mabibli.security.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Locale;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        String username = normalizeUsername(request.username());

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new UsernameAlreadyExistsException(username);
        }

        LocalDate now = LocalDate.now();

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token, "Bearer");
    }

    public AuthResponse login(LoginRequest request) {
        String username = normalizeUsername(request.username());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.password())
        );

        String token = jwtService.generateToken(username);
        return new AuthResponse(token, "Bearer");
    }

    private String normalizeUsername (String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }
}
