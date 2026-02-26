# 🧪 Guía Interactiva de Testing - Método createUser

## 🎯 Objetivo

Validar que el método `createUser` funciona correctamente en todos los escenarios posibles.

---

## ⚙️ Prerequisitos

✅ MySQL corriendo en localhost:3306
✅ Base de datos 'school' creada
✅ Roles insertados en la tabla roles
✅ Aplicación Spring Boot iniciada en puerto 8080

---

## 🧪 Suite de Pruebas Interactivas

### TEST 1: Crear Usuario Estudiante (ÉXITO) ✅

**Objetivo**: Verificar que se puede crear un usuario estudiante exitosamente

**Datos de entrada**:
```json
{
  "email": "carlos.mendez@example.com",
  "password": "SecurePass123!",
  "firstName": "Carlos",
  "lastName": "Méndez",
  "roleName": "ROLE_STUDENT"
}
```

**Ejecución**:
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "carlos.mendez@example.com",
    "password": "SecurePass123!",
    "firstName": "Carlos",
    "lastName": "Méndez",
    "roleName": "ROLE_STUDENT"
  }'
```

**Respuesta esperada** (201):
```json
{
  "id": 1,
  "email": "carlos.mendez@example.com",
  "firstName": "Carlos",
  "lastName": "Méndez",
  "roleName": "ROLE_STUDENT"
}
```

**Verificación en BD**:
```sql
-- Verificar usuario creado
SELECT * FROM users WHERE email = 'carlos.mendez@example.com';

-- Verificar estudiante creado
SELECT * FROM students WHERE user_id = 1;

-- Verificar relación
SELECT u.id, u.email, r.name, s.first_name, s.last_name
FROM users u
LEFT JOIN roles r ON u.role_id = r.id
LEFT JOIN students s ON u.id = s.user_id
WHERE u.email = 'carlos.mendez@example.com';
```

**Logs esperados**:
```
INFO ... Iniciando creación de usuario con email: carlos.mendez@example.com
DEBUG ... Rol encontrado: ROLE_STUDENT con ID: 1
DEBUG ... Usuario guardado con ID: 1
DEBUG ... Estudiante guardado con ID: 1
INFO ... Usuario creado exitosamente con ID: 1
```

**Checklist**:
- [ ] Respuesta es 201 Created
- [ ] JSON tiene los datos correctos
- [ ] Usuario existe en BD con role_id=1
- [ ] Estudiante existe en BD con user_id=1
- [ ] first_name = "Carlos"
- [ ] last_name = "Méndez"
- [ ] Logs muestran todo el proceso

---

### TEST 2: Crear Usuario Profesor (ÉXITO) ✅

**Objetivo**: Verificar que se pueden crear usuarios con diferentes roles

**Datos de entrada**:
```json
{
  "email": "ana.rojas@example.com",
  "password": "TeacherPass456!",
  "firstName": "Ana",
  "lastName": "Rojas",
  "roleName": "ROLE_TEACHER"
}
```

**Ejecución**:
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

**Respuesta esperada** (201):
```json
{
  "id": 2,
  "email": "ana.rojas@example.com",
  "firstName": "Ana",
  "lastName": "Rojas",
  "roleName": "ROLE_TEACHER"
}
```

**Verificación en BD**:
```sql
SELECT * FROM users WHERE role_id = 2;
```

**Checklist**:
- [ ] Respuesta es 201 Created
- [ ] role_id es 2 (ROLE_TEACHER)
- [ ] Usuario y estudiante están relacionados

---

### TEST 3: Email Duplicado (ERROR 409) ❌

**Objetivo**: Verificar que no se permite crear usuarios con email duplicado

**Datos de entrada** (Intentar crear con email existente):
```json
{
  "email": "carlos.mendez@example.com",
  "password": "DifferentPass789!",
  "firstName": "Otro",
  "lastName": "Usuario",
  "roleName": "ROLE_STUDENT"
}
```

**Ejecución**:
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "carlos.mendez@example.com",
    "password": "DifferentPass789!",
    "firstName": "Otro",
    "lastName": "Usuario",
    "roleName": "ROLE_STUDENT"
  }'
```

**Respuesta esperada** (409):
```json
{
  "timestamp": "2026-02-25T18:35:00.000",
  "status": 409,
  "error": "User Already Exists",
  "message": "El email carlos.mendez@example.com ya está registrado",
  "path": "/api/v1/users"
}
```

**Verificación en BD**:
```sql
-- No debe haber usuario duplicado
SELECT COUNT(*) as count FROM users WHERE email = 'carlos.mendez@example.com';
-- Debe retornar 1, no 2
```

**Logs esperados**:
```
WARN ... Intento de crear usuario con email duplicado: carlos.mendez@example.com
```

**Checklist**:
- [ ] Status Code es 409 Conflict
- [ ] Mensaje menciona que email ya está registrado
- [ ] Base de datos no tiene usuario duplicado
- [ ] Transacción fue revertida

---

### TEST 4: Rol Inexistente (ERROR 404) ❌

**Objetivo**: Verificar que no se permite crear usuarios con roles que no existen

**Datos de entrada** (Con rol inválido):
```json
{
  "email": "pedro.torres@example.com",
  "password": "InvalidRolePass123!",
  "firstName": "Pedro",
  "lastName": "Torres",
  "roleName": "ROLE_INVALID"
}
```

**Ejecución**:
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "pedro.torres@example.com",
    "password": "InvalidRolePass123!",
    "firstName": "Pedro",
    "lastName": "Torres",
    "roleName": "ROLE_INVALID"
  }'
```

**Respuesta esperada** (404):
```json
{
  "timestamp": "2026-02-25T18:35:10.000",
  "status": 404,
  "error": "Role Not Found",
  "message": "El rol ROLE_INVALID no existe",
  "path": "/api/v1/users"
}
```

**Verificación en BD**:
```sql
-- No debe haber usuario creado
SELECT * FROM users WHERE email = 'pedro.torres@example.com';
-- Debe estar vacío

-- No debe haber estudiante creado
SELECT * FROM students WHERE user_id NOT IN (
  SELECT id FROM users WHERE email != 'pedro.torres@example.com'
);
```

**Logs esperados**:
```
ERROR ... Rol no encontrado: ROLE_INVALID
```

**Checklist**:
- [ ] Status Code es 404 Not Found
- [ ] Mensaje menciona que rol no existe
- [ ] Usuario NO fue creado
- [ ] Estudiante NO fue creado
- [ ] Base de datos está consistente

---

### TEST 5: Transacción y Rollback (VERIFICACIÓN ACID) ✅

**Objetivo**: Verificar que la transacción es ACID y hace rollback ante errores

**Escenario**: Simular error durante creación de estudiante

**Análisis**:
El sistema está configurado para:
1. Crear usuario exitosamente
2. Si ocurre error al crear estudiante → Rollback automático
3. Usuario también se revierte de la BD

**Verificación**:
```sql
-- Después de intentar crear con rol inválido
SELECT COUNT(*) as usuarios FROM users;
SELECT COUNT(*) as estudiantes FROM students;

-- Los conteos no deberían incluir registros fallidos
```

**Checklist**:
- [ ] Rollback ocurre automáticamente
- [ ] Usuario no se crea si estudiante falla
- [ ] Base de datos vuelve al estado anterior
- [ ] Transacción es ACID

---

### TEST 6: Crear Usuario Admin (ÉXITO) ✅

**Objetivo**: Crear usuario con rol ADMIN para verificar funcionalidad completa

**Datos de entrada**:
```json
{
  "email": "admin.sistema@example.com",
  "password": "AdminSecure123!",
  "firstName": "Admin",
  "lastName": "Sistema",
  "roleName": "ROLE_ADMIN"
}
```

**Ejecución**:
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin.sistema@example.com",
    "password": "AdminSecure123!",
    "firstName": "Admin",
    "lastName": "Sistema",
    "roleName": "ROLE_ADMIN"
  }'
```

**Respuesta esperada** (201):
```json
{
  "id": 3,
  "email": "admin.sistema@example.com",
  "firstName": "Admin",
  "lastName": "Sistema",
  "roleName": "ROLE_ADMIN"
}
```

**Checklist**:
- [ ] Respuesta es 201 Created
- [ ] role_id es 3 (ROLE_ADMIN)

---

## 📊 Resumen de Pruebas

| Test | Descripción | Resultado Esperado | Estado |
|------|-------------|-------------------|--------|
| 1 | Crear usuario exitoso | 201 Created | ✅ |
| 2 | Crear con rol diferente | 201 Created | ✅ |
| 3 | Email duplicado | 409 Conflict | ✅ |
| 4 | Rol inexistente | 404 Not Found | ✅ |
| 5 | Transacción ACID | Rollback | ✅ |
| 6 | Crear usuario admin | 201 Created | ✅ |

---

## 🔍 Verificación Final en BD

Después de todas las pruebas, ejecuta:

```sql
-- Contar usuarios
SELECT COUNT(*) as total_usuarios FROM users;

-- Contar estudiantes
SELECT COUNT(*) as total_estudiantes FROM students;

-- Ver todos con detalle
SELECT u.id, u.email, r.name as role, s.first_name, s.last_name
FROM users u
LEFT JOIN roles r ON u.role_id = r.id
LEFT JOIN students s ON u.id = s.user_id
ORDER BY u.id;
```

Resultado esperado:
```
Total usuarios: 5 (3 exitosos + 2 fallidos que no se guardaron)
Total estudiantes: 5 (correlacionados con usuarios)
Todos los usuarios tienen su rol y estudiante asociados
```

---

## 📝 Registro de Pruebas

### Fecha: _______________

| Test | Resultado | Observaciones |
|------|-----------|---------------|
| 1 - Crear Estudiante | [ ] PASS [ ] FAIL | |
| 2 - Crear Profesor | [ ] PASS [ ] FAIL | |
| 3 - Email Duplicado | [ ] PASS [ ] FAIL | |
| 4 - Rol Inválido | [ ] PASS [ ] FAIL | |
| 5 - Transacción ACID | [ ] PASS [ ] FAIL | |
| 6 - Crear Admin | [ ] PASS [ ] FAIL | |

**Resultado General**: [ ] TODO CORRECTO ✅

---

## 🎓 Aprendizajes

Al completar estos tests habrás validado:
✅ Transacciones ACID funcionan
✅ Validaciones de negocio funcionan
✅ Manejo de errores es correcto
✅ Base de datos mantiene integridad
✅ Respuestas HTTP son apropiadas
✅ Logs son informativos

---

**¡Tests completados! ✨**

