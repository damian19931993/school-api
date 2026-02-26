# ⚡ Referencia Rápida - Comandos y Snippets

## 🚀 Iniciar Proyecto

```bash
# Navegar a la carpeta del proyecto
cd C:\Users\Damian\Desktop\projects\school\backend\school\school

# Compilar
mvn clean compile

# Ejecutar la aplicación
mvn spring-boot:run

# Compilar y ejecutar
mvn clean compile spring-boot:run
```

---

## 🗄️ Comandos MySQL

### Crear Base de Datos
```sql
CREATE SCHEMA school;
USE school;
```

### Insertar Roles
```sql
INSERT INTO roles (name) VALUES ('ROLE_STUDENT');
INSERT INTO roles (name) VALUES ('ROLE_TEACHER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
```

### Ver Todos los Datos
```sql
-- Usuarios
SELECT * FROM users;

-- Estudiantes
SELECT * FROM students;

-- Roles
SELECT * FROM roles;

-- Vista completa
SELECT u.id, u.email, r.name as role, s.first_name, s.last_name
FROM users u
LEFT JOIN roles r ON u.role_id = r.id
LEFT JOIN students s ON u.id = s.user_id;
```

### Limpiar Datos (para pruebas)
```sql
-- Eliminar todas las relaciones
DELETE FROM students;
DELETE FROM users;
DELETE FROM roles;

-- Reinsertar roles
INSERT INTO roles (name) VALUES ('ROLE_STUDENT');
INSERT INTO roles (name) VALUES ('ROLE_TEACHER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
```

---

## 🌐 Endpoints REST

### Crear Usuario (POST)
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@example.com",
    "password": "Password123!",
    "firstName": "Nombre",
    "lastName": "Apellido",
    "roleName": "ROLE_STUDENT"
  }'
```

### Variante: Usuario Profesor
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "profesor@example.com",
    "password": "Pass123!",
    "firstName": "Nombre",
    "lastName": "Apellido",
    "roleName": "ROLE_TEACHER"
  }'
```

### Variante: Usuario Admin
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "AdminPass123!",
    "firstName": "Admin",
    "lastName": "Sistema",
    "roleName": "ROLE_ADMIN"
  }'
```

---

## 📊 Respuestas Esperadas

### ✅ Éxito (201)
```json
{
  "id": 1,
  "email": "usuario@example.com",
  "firstName": "Nombre",
  "lastName": "Apellido",
  "roleName": "ROLE_STUDENT"
}
```

### ❌ Email Duplicado (409)
```json
{
  "timestamp": "2026-02-25T18:30:00",
  "status": 409,
  "error": "User Already Exists",
  "message": "El email usuario@example.com ya está registrado",
  "path": "/api/v1/users"
}
```

### ❌ Rol no Existe (404)
```json
{
  "timestamp": "2026-02-25T18:30:00",
  "status": 404,
  "error": "Role Not Found",
  "message": "El rol ROLE_INVALID no existe",
  "path": "/api/v1/users"
}
```

---

## 📁 Archivos Importantes

```
src/main/java/com/school/
├── controller/UserController.java          → Endpoint
├── service/
│   ├── UserService.java                   → Interfaz
│   └── impl/UserServiceImpl.java           → Lógica (AQUÍ ESTÁ createUser)
├── repository/                             → Acceso a BD
├── entity/                                 → Modelos JPA
├── dto/                                    → Request/Response
└── exception/                              → Manejo de errores

src/main/resources/
└── application.yaml                        → Configuración (cambiar credenciales aquí)

pom.xml                                     → Dependencias Maven
```

---

## 🔧 Configuración (application.yaml)

```yaml
spring:
  application:
    name: school
  datasource:
    url: jdbc:mysql://localhost:3306/school
    username: root
    password: 5276
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true
```

**Cambiar si es necesario**:
- `url`: Puerto de MySQL (default 3306)
- `username`: Usuario MySQL (default root)
- `password`: Contraseña MySQL (default 5276)

---

## 🐛 Debugging

### Ver Logs en Consola
```bash
# La aplicación muestra logs automáticamente
# Busca líneas como:
# INFO ... Iniciando creación de usuario con email:
# DEBUG ... Rol encontrado:
# INFO ... Usuario creado exitosamente
```

### Habilitar SQL Logs
El archivo `application.yaml` ya tiene:
```yaml
show-sql: true
format_sql: true
```

Esto mostrará todas las queries SQL en la consola.

### Pausar Aplicación
- Presiona `Ctrl+C` en la terminal
- O detén en IntelliJ IDEA

---

## 🔑 Credenciales

### Base de Datos
```
Host: localhost
Port: 3306
User: root
Password: 5276
Schema: school
```

### Aplicación
```
URL: http://localhost:8080
Endpoint: POST /api/v1/users
```

---

## 📦 Estructura del JSON Request

```json
{
  "email": "string",           // Email único, requerido
  "password": "string",        // Contraseña, requerido
  "firstName": "string",       // Nombre, requerido
  "lastName": "string",        // Apellido, requerido
  "roleName": "string"         // Rol (ROLE_STUDENT/ROLE_TEACHER/ROLE_ADMIN)
}
```

---

## 🧪 Test Rápidos

### Test 1: Usuario Válido
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"email":"test1@example.com","password":"Pass123!","firstName":"Test","lastName":"User","roleName":"ROLE_STUDENT"}'
```
Esperado: **201 Created**

### Test 2: Mismo Email
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"email":"test1@example.com","password":"Pass456!","firstName":"Otro","lastName":"User","roleName":"ROLE_STUDENT"}'
```
Esperado: **409 Conflict**

### Test 3: Rol Inválido
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"email":"test2@example.com","password":"Pass123!","firstName":"Test","lastName":"User","roleName":"ROLE_INVALID"}'
```
Esperado: **404 Not Found**

---

## 📋 Checklist Rápido

- [ ] MySQL está corriendo
- [ ] Base de datos 'school' existe
- [ ] Roles insertados
- [ ] Aplicación iniciada en puerto 8080
- [ ] Endpoint responde a POST /api/v1/users
- [ ] Usuarios se crean en BD
- [ ] Estudiantes se crean en BD
- [ ] Errores retornan códigos HTTP correctos

---

## 🎯 Transacción Resumida

```
@Transactional
createUser(CreateUserRequest)
├─ ✅ Validar email único
├─ ✅ Buscar rol
├─ ✅ Crear usuario
├─ ✅ Crear estudiante
└─ ❌ Error? → ROLLBACK TODO
```

---

## 💡 Tips Útiles

### Usar Postman
1. New → Request
2. Method: POST
3. URL: http://localhost:8080/api/v1/users
4. Headers: Content-Type: application/json
5. Body (JSON): `{ "email": "...", ... }`
6. Send

### Usar IntelliJ IDEA
1. Abrir `api-requests.http`
2. Click en ▶️ al lado de cada request
3. Ver respuesta en pestaña Response

### Usar PowerShell (Windows)
```powershell
$json = @{
    email = "test@example.com"
    password = "Pass123!"
    firstName = "Test"
    lastName = "User"
    roleName = "ROLE_STUDENT"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/v1/users" `
  -Method POST `
  -Headers @{"Content-Type"="application/json"} `
  -Body $json
```

---

## 🚨 Errores Comunes

### Error: "Unable to resolve name [org.hibernate.dialect.MySQL8Dialect]"
**Solución**: Cambiar en `application.yaml`:
```yaml
dialect: org.hibernate.dialect.MySQLDialect
```
(Ya está hecho en la configuración)

### Error: "Connection refused"
**Solución**: Verificar que MySQL está corriendo
```bash
# En Windows
mysql -u root -p5276
```

### Error: "Unknown database 'school'"
**Solución**: Crear la base de datos
```sql
CREATE SCHEMA school;
```

### Error: "No role found"
**Solución**: Insertar roles
```sql
INSERT INTO roles (name) VALUES ('ROLE_STUDENT');
```

---

## 📞 Documentación Referencia

- `README.md` - Inicio rápido
- `CREATE_USER_DOCUMENTATION.md` - Detalles técnicos
- `ARCHITECTURE.md` - Diagramas
- `TEST_API.md` - Ejemplos prueba
- `CHECKLIST.md` - Verificación
- `FINAL_SUMMARY.md` - Resumen ejecutivo
- `INTERACTIVE_TESTING.md` - Pruebas paso a paso

---

**¡Referencia rápida lista! 🚀**

