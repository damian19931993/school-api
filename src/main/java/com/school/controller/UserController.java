package com.school.controller;

import com.school.dto.CreateUserRequest;
import com.school.dto.UserResponse;
import com.school.exception.UnauthorizedException;
import com.school.repository.UserRepository;
import com.school.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestBody CreateUserRequest request,
            HttpServletRequest httpRequest) {

        // 1. Extraer el email del token
        Object emailObj = httpRequest.getAttribute("email");

        if (emailObj == null) {
            log.warn("Intento de crear usuario sin autenticación");
            throw new UnauthorizedException("Se requiere autenticación para crear usuarios");
        }

        String email = (String) emailObj;

        // 2. Buscar usuario autenticado
        var authenticatedUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        // 3. Verificar que sea ADMIN
        if (!authenticatedUser.getRole().getName().equals("ROLE_ADMIN")) {
            log.warn("Usuario no admin intentó crear usuario: {}", email);
            throw new UnauthorizedException("Solo administradores pueden crear usuarios");
        }

        // 4. Crear usuario
        log.info("Usuario ADMIN {} creando nuevo usuario", email);
        UserResponse userResponse = userService.createUser(request);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }
}

