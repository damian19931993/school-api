## Documentación - Método createUser

### Descripción
Crea un nuevo usuario con su perfil de estudiante de forma transaccional. Si alguna operación falla, toda la transacción se revierte, evitando datos huérfanos.

### Principios SOLID Aplicados

#### 1. **Single Responsibility Principle (SRP)**
- `UserService`: Solo responsable de la lógica de usuarios
- `UserController`: Solo maneja solicitudes HTTP
- Métodos privados en `UserServiceImpl` cada uno con una responsabilidad específica

#### 2. **Open/Closed Principle (OCP)**
- `UserService` es una interfaz que puede extenderse sin modificar el código existente
- Fácil agregar nuevas implementaciones

#### 3. **Liskov Substitution Principle (LSP)**
- `UserServiceImpl` implementa correctamente el contrato de `UserService`
- Puede ser reemplazada por otras implementaciones sin romper el código

#### 4. **Interface Segregation Principle (ISP)**
- `UserService` tiene un contrato pequeño y específico
- Los clientes no dependen de métodos que no usan

#### 5. **Dependency Inversion Principle (DIP)**
- `UserServiceImpl` depende de abstracciones (repositorios) no de implementaciones concretas
- Inyección de dependencias mediante `@RequiredArgsConstructor`

### Características de Transacción

```java
@Transactional
public UserResponse createUser(CreateUserRequest request)
```

- Si ocurre un error en cualquier paso, TODA la operación se revierte
- Garantiza consistencia: usuario + estudiante se crean juntos o no se crean
- Evita datos huérfanos

### Flujo de Ejecución

```
1. Validar email no existe
   ↓
2. Buscar rol por nombre
   ↓
3. Crear usuario (persistencia)
   ↓
4. Crear estudiante asociado (persistencia)
   ↓
5. Retornar respuesta
```

Si cualquier paso falla → Rollback automático de toda la transacción

### Ejemplo de Uso

#### Request JSON
```json
{
  "email": "juan@example.com",
  "password": "miPassword123",
  "firstName": "Juan",
  "lastName": "Pérez",
  "roleName": "ROLE_STUDENT"
}
```

#### Response (201 Created)
```json
{
  "id": 1,
  "email": "juan@example.com",
  "firstName": "Juan",
  "lastName": "Pérez",
  "roleName": "ROLE_STUDENT"
}
```

#### Errors Posibles

**409 Conflict** - Email ya existe
```json
{
  "timestamp": "2026-02-25T18:30:00",
  "status": 409,
  "error": "User Already Exists",
  "message": "El email juan@example.com ya está registrado",
  "path": "/api/v1/users"
}
```

**404 Not Found** - Rol no existe
```json
{
  "timestamp": "2026-02-25T18:30:00",
  "status": 404,
  "error": "Role Not Found",
  "message": "El rol ROLE_INVALID no existe",
  "path": "/api/v1/users"
}
```

### Seguridad - TODO

⚠️ **Pendiente**: Implementar encriptación de contraseña
- Agregar Spring Security
- Usar BCryptPasswordEncoder
- Cambiar la línea: `user.setPassword(request.getPassword());`
  Por: `user.setPassword(passwordEncoder.encode(request.getPassword()));`

### Logging

El servicio incluye logs en diferentes niveles:
- `INFO`: Inicio y fin de operaciones
- `DEBUG`: Detalles de búsquedas y guardados
- `WARN`: Intentos de crear usuarios duplicados
- `ERROR`: Errores de negocio (rol no encontrado)

### Manejo de Excepciones

- `UserAlreadyExistsException` → 409 Conflict
- `RoleNotFoundException` → 404 Not Found
- Otras excepciones → 500 Internal Server Error

Todo manejado por `GlobalExceptionHandler`

