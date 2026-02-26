-- Script para insertar roles base en la tabla roles

INSERT INTO roles (name) VALUES ('ROLE_STUDENT');
INSERT INTO roles (name) VALUES ('ROLE_TEACHER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

-- Consulta para verificar que los roles se insertaron correctamente
SELECT * FROM roles;

