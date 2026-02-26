# Test API - createUser

## Antes de empezar

1. Asegúrate de que MySQL está corriendo en localhost:3306
2. Crea el schema `school` en MySQL Workbench
3. Ejecuta el script `insert_roles.sql` para agregar los roles:
   ```
   ROLE_STUDENT
   ROLE_TEACHER
   ROLE_ADMIN
   ```
4. Inicia la aplicación Spring Boot

## Endpoints de Prueba

### 1. Crear un Usuario Estudiante (Éxito)

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "juan.perez@example.com",
    "password": "Password123!",
    "firstName": "Juan",
    "lastName": "Pérez",
    "roleName": "ROLE_STUDENT"
  }'
```

**Response (201 Created):**
```json
{
  "id": 1,
  "email": "juan.perez@example.com",
  "firstName": "Juan",
  "lastName": "Pérez",
  "roleName": "ROLE_STUDENT"
}
```

---

### 2. Crear un Usuario Profesor (Éxito)

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "maria.garcia@example.com",
    "password": "SecurePass456!",
    "firstName": "María",
    "lastName": "García",
    "roleName": "ROLE_TEACHER"
  }'
```

**Response (201 Created):**
```json
{
  "id": 2,
  "email": "maria.garcia@example.com",
  "firstName": "María",
  "lastName": "García",
  "roleName": "ROLE_TEACHER"
}
```

---

### 3. Intentar Crear Usuario con Email Duplicado (Error)

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "juan.perez@example.com",
    "password": "OtherPassword789!",
    "firstName": "Otro",
    "lastName": "Usuario",
    "roleName": "ROLE_STUDENT"
  }'
```

**Response (409 Conflict):**
```json
{
  "timestamp": "2026-02-25T18:35:42.123",
  "status": 409,
  "error": "User Already Exists",
  "message": "El email juan.perez@example.com ya está registrado",
  "path": "/api/v1/users"
}
```

---

### 4. Intentar Crear Usuario con Rol Inexistente (Error)

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "pedro.lopez@example.com",
    "password": "Password123!",
    "firstName": "Pedro",
    "lastName": "López",
    "roleName": "ROLE_INVALID"
  }'
```

**Response (404 Not Found):**
```json
{
  "timestamp": "2026-02-25T18:35:50.456",
  "status": 404,
  "error": "Role Not Found",
  "message": "El rol ROLE_INVALID no existe",
  "path": "/api/v1/users"
}
```

---

## Verificación en MySQL Workbench

### Después de crear usuarios, ejecuta:

```sql
-- Ver todos los usuarios
SELECT * FROM users;

-- Ver todos los estudiantes
SELECT * FROM students;

-- Ver usuarios con sus roles y estudiantes
SELECT u.id, u.email, r.name as role, s.first_name, s.last_name
FROM users u
LEFT JOIN roles r ON u.role_id = r.id
LEFT JOIN students s ON u.id = s.user_id;
```

---

## Logs Esperados en Consola

```
2026-02-25T18:30:00.000-06:00 INFO  com.school.service.impl.UserServiceImpl : Iniciando creación de usuario con email: juan.perez@example.com
2026-02-25T18:30:00.050-06:00 DEBUG com.school.service.impl.UserServiceImpl : Rol encontrado: ROLE_STUDENT con ID: 1
2026-02-25T18:30:00.100-06:00 DEBUG com.school.service.impl.UserServiceImpl : Usuario guardado con ID: 1
2026-02-25T18:30:00.150-06:00 DEBUG com.school.service.impl.UserServiceImpl : Estudiante guardado con ID: 1
2026-02-25T18:30:00.200-06:00 INFO  com.school.service.impl.UserServiceImpl : Usuario creado exitosamente con ID: 1
```

---

## Comportamiento Transaccional

Si durante la creación ocurre un error en cualquier punto:
- ❌ El usuario NO se guardará
- ❌ El estudiante NO se guardará
- ✅ La base de datos quedará consistente
- ✅ Se lanzará una excepción apropiada

**Ejemplo**: Si falla al guardar el estudiante → Se revierte automáticamente el usuario también

---

## Testing Avanzado

### Con Postman

1. Crea una nueva Request
2. Método: **POST**
3. URL: `http://localhost:8080/api/v1/users`
4. Headers: `Content-Type: application/json`
5. Body (raw JSON):
   ```json
   {
     "email": "test@example.com",
     "password": "TestPass123!",
     "firstName": "Test",
     "lastName": "User",
     "roleName": "ROLE_STUDENT"
   }
   ```
6. Click en Send

### Con IntelliJ IDEA

1. Click derecho en el archivo de clase
2. Selecciona "Generate" → "Test"
3. O usa el archivo `TEST_EXAMPLES.http` (si lo creas)

