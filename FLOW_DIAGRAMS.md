# 🎨 Flujos Visuales - Método createUser

## 1️⃣ Flujo Principal de Creación

```
┌─────────────────────────────────────────────────────────────┐
│                   CLIENTE (Postman/cURL)                    │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           │ JSON POST
                           ↓
┌─────────────────────────────────────────────────────────────┐
│              UserController.createUser()                     │
│                    @PostMapping                             │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ↓ CreateUserRequest
┌─────────────────────────────────────────────────────────────┐
│           UserServiceImpl.createUser()                        │
│              @Transactional                                 │
└──────────────────────────┬──────────────────────────────────┘
                           │
                ┌──────────┼──────────┐
                │          │          │
                ↓          ↓          ↓
        ┌──────────┐ ┌──────────┐ ┌──────────┐
        │Validate  │ │  Find    │ │  Create  │
        │  Email   │ │   Role   │ │   User   │
        └────┬─────┘ └────┬─────┘ └────┬─────┘
             │            │             │
             └────────────┼─────────────┘
                          │
                          ↓
                ┌──────────────────┐
                │ CREATE STUDENT   │
                └────┬─────────────┘
                     │
                     ↓
                ┌──────────────────┐
                │ MAP TO RESPONSE  │
                └────┬─────────────┘
                     │
                     ↓
            ┌─────────────────────┐
            │  201 CREATED        │
            │  + JSON Response    │
            └────────────────────┘
```

---

## 2️⃣ Flujo de Validaciones

```
          INPUT: CreateUserRequest
                     │
                     ↓
    ┌────────────────────────────────┐
    │ ¿EMAIL YA EXISTE?               │
    └────────┬───────────────┬────────┘
             │ SÍ            │ NO
             ↓               ↓
        ❌ ERROR         CONTINÚA
        409 Conflict        │
                            ↓
              ┌─────────────────────────┐
              │ ¿EXISTE EL ROL?         │
              └────┬──────────┬─────────┘
                   │ NO       │ SÍ
                   ↓          ↓
              ❌ ERROR    CONTINÚA
              404 Not Found │
                           ↓
                   ✅ CREAR USUARIO
                           │
                           ↓
                   ✅ CREAR ESTUDIANTE
                           │
                           ↓
                   ✅ RETORNAR 201
```

---

## 3️⃣ Flujo de Transacción ACID

```
INICIO TRANSACCIÓN
    │
    ├─ SAVEPOINT 1: Validar email
    │   └─ ❌ Error? → ROLLBACK a punto antes
    │
    ├─ SAVEPOINT 2: Buscar rol
    │   └─ ❌ Error? → ROLLBACK a punto antes
    │
    ├─ SAVEPOINT 3: INSERT usuario
    │   └─ ❌ Error? → ROLLBACK a punto antes
    │       └─ El usuario NO se guardó
    │
    ├─ SAVEPOINT 4: INSERT estudiante
    │   └─ ❌ Error? → ROLLBACK a punto antes
    │       └─ El usuario tampoco se guardó (cascada)
    │       └─ Base de datos = CONSISTENTE ✅
    │
    └─ COMMIT
        └─ ✅ Todo guardado exitosamente
            └─ Usuario + Estudiante
```

---

## 4️⃣ Flujo de Errores y Respuestas

```
createUser()
    │
    ├─ VALIDACIÓN FALLIDA
    │  └─ UserAlreadyExistsException
    │     └─ GlobalExceptionHandler.handleUserAlreadyExistsException()
    │        └─ 409 Conflict
    │           ├─ timestamp: 2026-02-25T18:30:00
    │           ├─ status: 409
    │           ├─ error: "User Already Exists"
    │           └─ message: "El email ... ya está registrado"
    │
    ├─ ROL NO ENCONTRADO
    │  └─ RoleNotFoundException
    │     └─ GlobalExceptionHandler.handleRoleNotFoundException()
    │        └─ 404 Not Found
    │           ├─ timestamp: 2026-02-25T18:30:00
    │           ├─ status: 404
    │           ├─ error: "Role Not Found"
    │           └─ message: "El rol ... no existe"
    │
    ├─ ERROR DE BD
    │  └─ DataIntegrityViolationException
    │     └─ ROLLBACK automático
    │     └─ GlobalExceptionHandler.handleGlobalException()
    │        └─ 500 Internal Server Error
    │
    └─ ✅ ÉXITO
       └─ 201 Created
          ├─ id: 1
          ├─ email: "user@example.com"
          ├─ firstName: "Juan"
          ├─ lastName: "Pérez"
          └─ roleName: "ROLE_STUDENT"
```

---

## 5️⃣ Flujo de Logging

```
createUser(CreateUserRequest) →┐
                               │
                   INFO ────────┤─→ "Iniciando creación de usuario"
                               │
validateEmailNotExists()      │
      ├─ Email NO existe     │
      └─ DEBUG ──────────────┤─→ "Email validado"
                               │
findRoleByName()              │
      ├─ Rol encontrado       │
      └─ DEBUG ──────────────┤─→ "Rol encontrado con ID: X"
                               │
createAndSaveUser()           │
      ├─ INSERT usuarios       │
      └─ DEBUG ──────────────┤─→ "Usuario guardado con ID: Y"
                               │
createAndSaveStudent()        │
      ├─ INSERT estudiantes    │
      └─ DEBUG ──────────────┤─→ "Estudiante guardado con ID: Z"
                               │
                   INFO ────────┤─→ "Usuario creado exitosamente con ID: Y"
                               │
Return UserResponse          ←┘
```

---

## 6️⃣ Flujo de Base de Datos

```
MYSQL DATABASE
    │
    ├─ TABLA: roles
    │  ├─ id: 1, name: "ROLE_STUDENT"
    │  ├─ id: 2, name: "ROLE_TEACHER"
    │  └─ id: 3, name: "ROLE_ADMIN"
    │
    ├─ TABLA: users
    │  ├─ INSERT
    │  │ id: AUTO_INCREMENT
    │  │ email: "juan@example.com"
    │  │ password: "Password123!"
    │  │ role_id: 1 (FK a roles)
    │  │
    │  └─ RESULTADO: user_id = 123
    │
    └─ TABLA: students
       └─ INSERT
         id: AUTO_INCREMENT
         user_id: 123 (FK a users)
         first_name: "Juan"
         last_name: "Pérez"
         
         RESULTADO: student_id = 456
```

---

## 7️⃣ Flujo de Inyección de Dependencias

```
@Service
UserServiceImpl
    │
    ├─ @RequiredArgsConstructor
    │  └─ Genera constructor automático
    │
    └─ private final UserRepository userRepository;
       private final RoleRepository roleRepository;
       private final StudentRepository studentRepository;
       
       Spring Framework inyecta automáticamente:
       ├─ @Repository interface → Implementación JpaRepository
       ├─ @Repository interface → Implementación JpaRepository
       └─ @Repository interface → Implementación JpaRepository
```

---

## 8️⃣ Ciclo de Vida de una Petición

```
1. CLIENTE ENVÍA JSON
   ↓
2. SPRING RECIBE @PostMapping
   ↓
3. @RequestBody DESERIALIZA a CreateUserRequest
   ↓
4. VALIDA @Transactional
   ↓
5. EJECUTA PASO A PASO (atomicidad)
   ↓
6. SI ERROR EN CUALQUIER PASO
   ├─ MARCA TRANSACCIÓN PARA ROLLBACK
   └─ LANZA EXCEPCIÓN
   ↓
7. GlobalExceptionHandler CAPTURA EXCEPCIÓN
   ↓
8. FORMATEA ErrorResponse
   ↓
9. RETORNA HTTP CODE + JSON
   ↓
10. CLIENTE RECIBE RESPUESTA
```

---

## 9️⃣ Comparación: Con vs Sin Transacción

### ❌ SIN TRANSACCIÓN (Problema)
```
createUser()
├─ ✅ Usuario creado en BD
│
├─ ❌ ERROR: Rol no encontrado
│  └─ Excepción lanzada
│
└─ 📊 ESTADO: Usuario HUÉRFANO en BD
            Estudiante NO creado
            Inconsistencia de datos ⚠️
```

### ✅ CON TRANSACCIÓN (Solución)
```
@Transactional
createUser()
├─ ✅ Usuario creado en transacción
│
├─ ❌ ERROR: Rol no encontrado
│  └─ Excepción lanzada
│  └─ ROLLBACK automático
│
└─ 📊 ESTADO: Usuario ELIMINADO de BD
            Nada se guardó
            BD consistente ✅
```

---

## 🔟 Estados Finales

```
┌─────────────────────────────────────────┐
│        ESTADOS POSIBLES                  │
└──────────────┬──────────────────────────┘
               │
        ┌──────┴──────┬──────────┬──────────┐
        │             │          │          │
        ↓             ↓          ↓          ↓
    ✅ ÉXITO    ❌ EMAIL     ❌ ROL   ❌ ERROR
    201 Created  DUPLICADO   NO EXISTE  INTERNO
                 409         404        500
                 
                 ROLLBACK    ROLLBACK   ROLLBACK
                 (Todos)     (Todos)    (Todos)
```

---

**Visualización completada ✨**

