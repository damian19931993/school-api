package com.school.service.impl;

import com.school.dto.LoginRequest;
import com.school.dto.LoginResponse;
import com.school.entity.User;
import com.school.exception.InvalidCredentialsException;
import com.school.repository.UserRepository;
import com.school.service.AuthService;
import com.school.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Intento de login con email: {}", request.getEmail());

        // 1. Buscar usuario por email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado: {}", request.getEmail());
                    throw new InvalidCredentialsException("Email o contraseña incorrectos");
                });

        // 2. Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Contraseña incorrecta para usuario: {}", request.getEmail());
            throw new InvalidCredentialsException("Email o contraseña incorrectos");
        }

        // 3. Generar token JWT
        String token = jwtService.generateToken(user.getEmail(), user.getId());
        log.info("Login exitoso para usuario: {} (ID: {})", user.getEmail(), user.getId());

        // 4. Retornar respuesta
        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                token,
                user.getRole().getName()
        );
    }
}

