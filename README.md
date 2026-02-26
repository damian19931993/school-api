# 🚀 School Management API - Guía Rápida

## 📝 Resumen

Esta es una API REST para gestionar estudiantes, profesores y administradores en una escuela. Se implementó el método `createUser` con transacciones garantizadas, siguiendo principios SOLID.

---

## ⚙️ Requisitos

- ✅ Java 25
- ✅ Spring Boot 4.0.3
- ✅ MySQL 8.0+
- ✅ Maven
- ✅ IntelliJ IDEA (recomendado)

---

## 🔧 Instalación

### 1. Crear Base de Datos
```sql
-- En MySQL Workbench
CREATE SCHEMA school;
USE school;
```

### 2. Insertar Roles Base
```sql
INSERT INTO roles (name) VALUES ('ROLE_STUDENT');
INSERT INTO roles (name) VALUES ('ROLE_TEACHER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
```

O ejecuta el script:
```bash
mysql -u root -p5276 school < insert_roles.sql
```

### 3. Configuración de Base de Datos
El archivo `application.yaml` ya está configurado:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/school
    username: root
    password: 5276
```

### 4. Iniciar la Aplicación
```bash
cd C:\Users\Damian\Desktop\projects\school\backend\school\school
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

---

## 📡 API Endpoints

### Crear Usuario
```
POST /api/v1/users
Content-Type: application/json

{
  "email": "juan@example.com",
  "password": "Password123!",
  "firstName": "Juan",
  "lastName": "Pérez",
  "roleName": "ROLE_STUDENT"
}
```

**Response (201):**
```json
{
  "id": 1,
  "email": "juan@example.com",
  "firstName": "Juan",
  "lastName": "Pérez",
  "roleName": "ROLE_STUDENT"
}
```

---

## 🧪 Pruebas Rápidas

### Opción 1: Usando IntelliJ IDEA
1. Abre el archivo `api-requests.http`
2. Haz clic en el botón ▶️ al lado de cada request
3. Ver respuesta en la pestaña Response

### Opción 2: Usando cURL
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Pass123!",
    "firstName": "Test",
    "lastName": "User",
    "roleName": "ROLE_STUDENT"
  }'
```

### Opción 3: Usando Postman
1. New → Request
2. Method: POST
3. URL: http://localhost:8080/api/v1/users
4. Body (JSON):
```json
{
  "email": "test@example.com",
  "password": "Pass123!",
  "firstName": "Test",
  "lastName": "User",
  "roleName": "ROLE_STUDENT"
}
```

---

## 📊 Verificar Datos en MySQL

```sql
-- Ver usuarios
SELECT * FROM users;

-- Ver estudiantes
SELECT * FROM students;

-- Ver roles
SELECT * FROM roles;

-- Ver usuario con su rol y estudiante
SELECT u.id, u.email, r.name as role, s.first_name, s.last_name
FROM users u
LEFT JOIN roles r ON u.role_id = r.id
LEFT JOIN students s ON u.id = s.user_id;
```

---

## 🔒 Garantías de la Implementación

✅ **Transacción ACID**: Si algo falla, todo se revierte
✅ **Email Único**: No permite emails duplicados
✅ **Rol Validado**: Solo permite roles existentes
✅ **Sin Datos Huérfanos**: Usuario y Estudiante se crean juntos o no se crean
✅ **Manejo de Errores**: Respuestas claras con códigos HTTP apropiados
✅ **Logging Completo**: Rastrear todas las operaciones
✅ **Principios SOLID**: Código mantenible y escalable

---

## 📁 Estructura de Carpetas

```
src/main/java/com/school/
├── controller/           # Endpoints REST
│   └── UserController.java
├── service/              # Lógica de negocio
│   ├── UserService.java (interfaz)
│   └── impl/
│       └── UserServiceImpl.java
├── repository/           # Acceso a datos
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   └── StudentRepository.java
├── entity/               # Modelos de BD
│   ├── User.java
│   ├── Role.java
│   └── Student.java
├── dto/                  # Transferencia de datos
│   ├── CreateUserRequest.java
│   └── UserResponse.java
└── exception/            # Manejo de errores
    ├── GlobalExceptionHandler.java
    ├── RoleNotFoundException.java
    ├── UserAlreadyExistsException.java
    └── ErrorResponse.java
```

---

## 📚 Documentación Adicional

- **CREATE_USER_DOCUMENTATION.md** - Detalles del método createUser
- **ARCHITECTURE.md** - Diagramas de arquitectura
- **TEST_API.md** - Ejemplos de prueba detallados
- **IMPLEMENTATION_SUMMARY.md** - Resumen de implementación

---

## 🐛 Solución de Problemas

### Error: "El schema school no existe"
```sql
CREATE SCHEMA school;
USE school;
```

### Error: "El rol no existe"
```sql
INSERT INTO roles (name) VALUES ('ROLE_STUDENT');
```

### Error: "Email ya registrado"
- Es normal, crea un usuario con otro email

### Error: Conexión rechazada a MySQL
- Verifica que MySQL esté corriendo
- Verifica puerto 3306
- Verifica credenciales (root / 5276)

---

## ⚡ Próximos Pasos

1. **Encriptar contraseñas**
   ```java
   user.setPassword(passwordEncoder.encode(request.getPassword()));
   ```

2. **Agregar validaciones**
   ```java
   @Valid
   public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request)
   ```

3. **Implementar más endpoints**
   - GET /api/v1/users/{id}
   - PUT /api/v1/users/{id}
   - DELETE /api/v1/users/{id}

4. **Agregar seguridad**
   - Spring Security
   - JWT Tokens
   - CORS

---

## 🎯 Contacto y Soporte

Para más información, revisa los archivos de documentación en la raíz del proyecto.

---

**¡Listo para usar! ✨**

