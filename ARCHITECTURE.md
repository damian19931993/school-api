# Arquitectura - Método createUser

## 📦 Estructura de Clases

```
┌─────────────────────────────────────────────────────────────┐
│                      UserController                         │
│                  @RestController                            │
│              POST /api/v1/users                             │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ↓
┌─────────────────────────────────────────────────────────────┐
│                      UserService                            │
│                    (Interface)                              │
│         + createUser(CreateUserRequest)                     │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ↓ (implements)
┌─────────────────────────────────────────────────────────────┐
│                   UserServiceImpl                            │
│                @Service @Transactional                      │
│  - validateEmailNotExists()                                 │
│  - findRoleByName()                                         │
│  - createAndSaveUser()                                      │
│  - createAndSaveStudent()                                   │
│  - mapToUserResponse()                                      │
└──────────┬──────────────┬──────────────┬────────────────────┘
           │              │              │
     ┌─────▼──┐    ┌─────▼──┐    ┌─────▼──┐
     │ UserRep│    │RoleRep │    │StudRep │
     │ository │    │ository │    │ository │
     └─────┬──┘    └─────┬──┘    └─────┬──┘
           │              │              │
           └──────────────┴──────────────┘
                    ↓
            ┌───────────────────┐
            │      MySQL        │
            │    (Transacción)  │
            └───────────────────┘
```

## 🔄 Flujo Transaccional

```
INICIO TRANSACCIÓN
    │
    ├─ 1. validateEmailNotExists(email)
    │      └─ Si existe → Lanzar UserAlreadyExistsException
    │
    ├─ 2. findRoleByName(roleName)
    │      └─ Si no existe → Lanzar RoleNotFoundException
    │
    ├─ 3. createAndSaveUser(request, role)
    │      └─ INSERT INTO users (email, password, role_id)
    │
    ├─ 4. createAndSaveStudent(request, user)
    │      └─ INSERT INTO students (user_id, first_name, last_name)
    │
    ├─ 5. mapToUserResponse(user)
    │      └─ Preparar respuesta JSON
    │
    └─ RETORNAR RESPUESTA
    
SI OCURRE ERROR EN CUALQUIER PASO:
    │
    └─ ROLLBACK AUTOMÁTICO
       └─ Se revierten TODOS los cambios
```

## 📊 Diagrama de Entidades

```
┌────────────────┐         ┌────────────────┐
│     Role       │         │      User      │
├────────────────┤         ├────────────────┤
│ id (PK)        │◄────────│ id (PK)        │
│ name           │ 1    *  │ email (UNIQUE) │
└────────────────┘         │ password       │
                           │ role_id (FK)   │
                           └────────┬────────┘
                                    │ 1
                                    │
                                    │ *
                           ┌────────▼────────┐
                           │    Student     │
                           ├────────────────┤
                           │ id (PK)        │
                           │ user_id (FK)   │
                           │ first_name     │
                           │ last_name      │
                           └────────────────┘
```

## 🔐 Validaciones

```
CreateUserRequest
    │
    ├─ email ─→ ¿Email único?
    │            ├─ NO  → UserAlreadyExistsException (409)
    │            └─ SÍ  → Continuar
    │
    ├─ roleName ─→ ¿Existe el rol?
    │               ├─ NO  → RoleNotFoundException (404)
    │               └─ SÍ  → Continuar
    │
    ├─ password ─→ TODO: Validar complejidad y encriptar
    │
    ├─ firstName ─→ (Validación a nivel BD si lo deseas)
    │
    └─ lastName ──→ (Validación a nivel BD si lo deseas)
```

## 📝 Inyección de Dependencias

```java
// Todas las dependencias se inyectan automáticamente
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;      // Generada automáticamente
    private final RoleRepository roleRepository;      // Generada automáticamente
    private final StudentRepository studentRepository; // Generada automáticamente
}
```

## 🔒 Transacción ACID

```
ATOMICITY (Atomicidad):
    └─ Todo se guarda o nada se guarda

CONSISTENCY (Consistencia):
    └─ Los datos siempre son consistentes
    └─ El usuario siempre tiene su estudiante asociado

ISOLATION (Aislamiento):
    └─ Las transacciones no interfieren entre sí

DURABILITY (Durabilidad):
    └─ Los datos se persisten en MySQL
```

## 🎯 Principios SOLID Implementados

1. **SRP**: Cada clase tiene una responsabilidad única
2. **OCP**: Extensible sin modificar código existente
3. **LSP**: Implementaciones intercambiables
4. **ISP**: Interfaces pequeñas y específicas
5. **DIP**: Depende de abstracciones, no de implementaciones

