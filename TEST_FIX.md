# ✅ Instrucciones para Probar el Fix

## 🔄 Cambios Realizados

Se modificó `UserServiceImpl.java`:

**Cambio 1**: En el método `createUser()` (línea 57)
```java
// ANTES
return mapToUserResponse(user);

// DESPUÉS
return mapToUserResponse(user, student, role);
```

**Cambio 2**: Firma del método `mapToUserResponse()` (línea 130)
```java
// ANTES
private UserResponse mapToUserResponse(User user)

// DESPUÉS
private UserResponse mapToUserResponse(User user, Student student, Role role)
```

---

## 🚀 Pasos para Probar

### 1. Reinicia la Aplicación

Si ya estaba corriendo, detén presionando `Ctrl+C`, luego:

```bash
cd C:\Users\Damian\Desktop\projects\school\backend\school\school
mvn spring-boot:run
```

Esperado en consola:
```
...Started SchoolApplication in ... seconds
```

### 2. Prueba el Endpoint

Usa cURL, Postman o el archivo `api-requests.http`:

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

### 3. Verifica la Respuesta

Deberías recibir (201 Created):
```json
{
  "id": 1,
  "email": "maria.garcia@example.com",
  "firstName": "María",
  "lastName": "García",
  "roleName": "ROLE_TEACHER"
}
```

**NO deberías ver**: NullPointerException ❌

### 4. Verifica en Base de Datos

```sql
-- Conecta a MySQL
USE school;

-- Verifica usuarios
SELECT * FROM users;

-- Verifica estudiantes
SELECT * FROM students;

-- Vista completa
SELECT u.id, u.email, r.name, s.first_name, s.last_name
FROM users u
LEFT JOIN roles r ON u.role_id = r.id
LEFT JOIN students s ON u.id = s.user_id;
```

Deberías ver:
```
id | email                    | name         | first_name | last_name
1  | maria.garcia@example.com | ROLE_TEACHER | María      | García
```

---

## 🧪 Casos de Prueba Completos

### Test 1: Crear Estudiante ✅
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "carlos.mendez@example.com",
    "password": "Password123!",
    "firstName": "Carlos",
    "lastName": "Méndez",
    "roleName": "ROLE_STUDENT"
  }'
```
Esperado: **201 Created** ✅

### Test 2: Crear Profesor ✅
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ana.rojas@example.com",
    "password": "TeacherPass456!",
    "firstName": "Ana",
    "lastName": "Rojas",
    "roleName": "ROLE_TEACHER"
  }'
```
Esperado: **201 Created** ✅

### Test 3: Crear Admin ✅
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin.sistema@example.com",
    "password": "AdminPass123!",
    "firstName": "Admin",
    "lastName": "Sistema",
    "roleName": "ROLE_ADMIN"
  }'
```
Esperado: **201 Created** ✅

### Test 4: Email Duplicado ❌
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "carlos.mendez@example.com",
    "password": "DifferentPass!",
    "firstName": "Otro",
    "lastName": "Usuario",
    "roleName": "ROLE_STUDENT"
  }'
```
Esperado: **409 Conflict** ✅

### Test 5: Rol Inválido ❌
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Password123!",
    "firstName": "Test",
    "lastName": "User",
    "roleName": "ROLE_INVALID"
  }'
```
Esperado: **404 Not Found** ✅

---

## ✅ Checklist de Verificación

- [ ] Aplicación reiniciada correctamente
- [ ] No hay errores en consola al iniciar
- [ ] Test 1 (Estudiante) devuelve 201 ✅
- [ ] Test 2 (Profesor) devuelve 201 ✅
- [ ] Test 3 (Admin) devuelve 201 ✅
- [ ] Test 4 (Duplicado) devuelve 409 ✅
- [ ] Test 5 (Rol inválido) devuelve 404 ✅
- [ ] Usuarios aparecen en base de datos ✅
- [ ] Estudiantes aparecen en base de datos ✅
- [ ] Relaciones están correctas en BD ✅
- [ ] Respuestas JSON contienen todos los datos ✅

---

## 🐛 Si Aún Tienes Problemas

### Error 1: "Connection refused"
```
Solución: Verifica que MySQL está corriendo
mysql -u root -p5276
```

### Error 2: "Unknown database 'school'"
```sql
Solución: Crear la base de datos
CREATE SCHEMA school;
```

### Error 3: "No role found"
```sql
Solución: Insertar roles
INSERT INTO roles (name) VALUES ('ROLE_STUDENT');
INSERT INTO roles (name) VALUES ('ROLE_TEACHER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
```

### Error 4: "Cannot start Application"
```bash
Solución: Limpiar y recompilar
mvn clean compile
mvn spring-boot:run
```

---

## 📝 Logs Esperados en Consola

```
INFO  c.school.service.impl.UserServiceImpl    : Iniciando creación de usuario con email: maria.garcia@example.com
DEBUG c.school.service.impl.UserServiceImpl    : Rol encontrado: ROLE_TEACHER con ID: 2
DEBUG c.school.service.impl.UserServiceImpl    : Usuario guardado con ID: 1
DEBUG c.school.service.impl.UserServiceImpl    : Estudiante guardado con ID: 1
INFO  c.school.service.impl.UserServiceImpl    : Usuario creado exitosamente con ID: 1
```

---

## 🎉 ¡Éxito!

Si completaste todo esto, el fix está funcionando correctamente.

El método `createUser` ahora:
✅ Crea usuarios sin errores
✅ Crea estudiantes relacionados
✅ Retorna respuestas JSON correctas
✅ Mantiene transacciones ACID
✅ Maneja errores apropiadamente

---

**Documentación de prueba completada ✅**

