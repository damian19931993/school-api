package com.school.service.impl;

import com.school.dto.CreateUserRequest;
import com.school.dto.UserResponse;
import com.school.entity.Role;
import com.school.entity.Student;
import com.school.entity.User;
import com.school.exception.RoleNotFoundException;
import com.school.exception.UserAlreadyExistsException;
import com.school.repository.RoleRepository;
import com.school.repository.StudentRepository;
import com.school.repository.UserRepository;
import com.school.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Crea un nuevo usuario con su perfil de estudiante de forma transaccional.
     * Si alguna operación falla, toda la transacción se revierte.
     *
     * @param request DTO con los datos del usuario y estudiante
     * @return UserResponse con los datos del usuario creado
     * @throws UserAlreadyExistsException si el email ya existe
     * @throws RoleNotFoundException si el rol no existe
     */
    @Transactional
    @Override
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Iniciando creación de usuario con email: {}", request.getEmail());

        // 1. Validar que el email no exista
        validateEmailNotExists(request.getEmail());

        // 2. Buscar el rol
        Role role = findRoleByName(request.getRoleName());

        // 3. Crear y persistir el usuario
        User user = createAndSaveUser(request, role);

        // 4. Crear y persistir el estudiante
        Student student = createAndSaveStudent(request, user);

        log.info("Usuario creado exitosamente con ID: {}", user.getId());

        // 5. Retornar la respuesta (pasar estudiante directamente para evitar lazy loading)
        return mapToUserResponse(user, student, role);
    }

    /**
     * Valida que el email no exista en la base de datos.
     *
     * @param email email a validar
     * @throws UserAlreadyExistsException si el email ya existe
     */
    private void validateEmailNotExists(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            log.warn("Intento de crear usuario con email duplicado: {}", email);
            throw new UserAlreadyExistsException("El email " + email + " ya está registrado");
        });
    }

    /**
     * Busca un rol por nombre.
     *
     * @param roleName nombre del rol
     * @return el objeto Role
     * @throws RoleNotFoundException si el rol no existe
     */
    private Role findRoleByName(String roleName) {
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            log.error("Rol no encontrado: {}", roleName);
            throw new RoleNotFoundException("El rol " + roleName + " no existe");
        }
        log.debug("Rol encontrado: {} con ID: {}", roleName, role.getId());
        return role;
    }

    /**
     * Crea y persiste un nuevo usuario.
     *
     * @param request datos del usuario
     * @param role rol del usuario
     * @return el usuario creado
     */
    private User createAndSaveUser(CreateUserRequest request, Role role) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        User savedUser = userRepository.save(user);
        log.debug("Usuario guardado con ID: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * Crea y persiste un nuevo estudiante asociado al usuario.
     *
     * @param request datos del estudiante
     * @param user usuario asociado
     * @return el estudiante creado
     */
    private Student createAndSaveStudent(CreateUserRequest request, User user) {
        Student student = new Student();
        student.setUser(user);
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());

        Student savedStudent = studentRepository.save(student);
        log.debug("Estudiante guardado con ID: {}", savedStudent.getId());
        return savedStudent;
    }

    /**
     * Mapea un User a UserResponse.
     *
     * @param user usuario a mapear
     * @param student estudiante asociado
     * @param role rol del usuario
     * @return DTO de respuesta
     */
    private UserResponse mapToUserResponse(User user, Student student, Role role) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                student.getFirstName(),
                student.getLastName(),
                role.getName()
        );
    }
}

