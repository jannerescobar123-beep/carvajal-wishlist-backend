-- =============================================================================
-- SCRIPT DE SEMILLA DE DATOS (SEED DATA) - CARVAJAL WISHLIST
-- Compatible con PostgreSQL 14+ / 16+
-- =============================================================================

-- 1. Usuarios Iniciales (Password: superpassword123)
-- Hash BCrypt generado con Spring Security Crypto
INSERT INTO users (username, email, password, role, created_at, updated_at) VALUES
('admin_janner', 'admin@carvajal.com', '$2a$10$FLEqgrLT2in5vRMX3M5GfeOQrnb6uJ3Yb7ztP4jEmzW4yx.jy9MwC', 'ADMIN', NOW(), NOW()),
('cliente_prueba', 'cliente@carvajal.com', '$2a$10$FLEqgrLT2in5vRMX3M5GfeOQrnb6uJ3Yb7ztP4jEmzW4yx.jy9MwC', 'CLIENT', NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

-- 2. Catálogo Maestro de Productos Carvajal (Artículos Escolares, Oficina y Papelería)
-- Incluye intencionalmente productos con stock alto, bajo y 0 (Agotado) para validar la regla RF-3.
INSERT INTO products (name, description, price, stock, is_active, created_at, updated_at) VALUES
('Cuaderno Norma Jean Book', 'Cuaderno argollado 100 hojas cuadriculado', 15500.00, 150, true, NOW(), NOW()),
('Lápiz Mirado No. 2', 'Caja x 12 lápices de grafito HB con borrador', 12000.00, 300, true, NOW(), NOW()),
('Marcadores Sharpie', 'Set de 8 marcadores permanentes colores surtidos', 35000.00, 50, true, NOW(), NOW()),
('Resma Papel Reprograf', 'Resma tamaño carta 500 hojas bond 75g', 22000.00, 100, true, NOW(), NOW()),
('Carpeta Fuelle Norma', 'Carpeta plástica expandible de 13 divisiones', 18500.00, 80, true, NOW(), NOW()),
('Bolígrafo Kilométrico', 'Caja x 24 bolígrafos tinta negra trazo fino', 24000.00, 200, true, NOW(), NOW()),
('Calculadora Casio fx-82', 'Calculadora científica estándar para colegio y universidad', 85000.00, 30, true, NOW(), NOW()),
('Cinta Pegante Tesa', 'Cinta transparente 12mm x 40m', 3500.00, 400, true, NOW(), NOW()),
('Borrador Nata Pelikan (Agotado)', 'Borrador de nata grande blanco (Sin existencias para pruebas de notificación)', 1500.00, 0, true, NOW(), NOW()),
('Morral Totto Universitario', 'Morral ergonómico porta PC de 14 pulgadas', 145000.00, 15, true, NOW(), NOW());
