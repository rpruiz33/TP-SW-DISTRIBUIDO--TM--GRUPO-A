 use empuje_comunitario;
insert into role ( name_rol) values ("Presidente"),("Vocal"),("Coordinador"),("Voluntario");

INSERT INTO user(activate,id_role,phone,username,email,last_name,name,password) 
VALUES (
    1,                  -- activate = true
    1,                  -- id_role = 1, ejemplo administrador
    '1234567890',       -- phone, ejemplo
    'admin',            -- username
    'admin@example.com',-- email
    'Admin',            -- last_name
    'Admin',            -- name
    '$2a$10$m0f1HfQq57TkXpZOQm2P2O41mwuS.qaavBc0L8/EUSTOd/8IGI/7i'              -- hash para 1234
),
  (b'1', 2, '222222222', 'user1', 'user1@demo.com', 'Perez', 'Juan', '$2a$10$m0f1HfQq57TkXpZOQm2P2O41mwuS.qaavBc0L8/EUSTOd/8IGI/7i' ),
  (b'1', 3, '333333333', 'user2', 'user2@demo.com', 'Gomez', 'Maria', '$2a$10$m0f1HfQq57TkXpZOQm2P2O41mwuS.qaavBc0L8/EUSTOd/8IGI/7i' );

-- ==============================================
-- Insertar Eventos
-- ==============================================
INSERT INTO events (date_registration, name_event, description_event) VALUES
  -- Eventos pasados
  (DATE_SUB(NOW(), INTERVAL 10 DAY), 'Evento Solidario Pasado 1', 'Recolección de alimentos en el barrio'),
  (DATE_SUB(NOW(), INTERVAL 5 DAY), 'Evento Solidario Pasado 2', 'Campaña de donación de ropa'),
  (DATE_SUB(NOW(), INTERVAL 1 DAY), 'Evento Solidario Pasado 3', 'Entrega de juguetes para niños'),
  
  -- Eventos futuros
  (DATE_ADD(NOW(), INTERVAL 1 DAY), 'Evento Solidario Futuro 1', 'Jornada de vacunación'),
  (DATE_ADD(NOW(), INTERVAL 5 DAY), 'Evento Solidario Futuro 2', 'Colecta general de insumos médicos'),
  (DATE_ADD(NOW(), INTERVAL 10 DAY), 'Evento Solidario Futuro 3', 'Campaña de reforestación');

-- ==============================================
-- Insertar Donaciones
-- ==============================================
INSERT INTO donation (amount, id_user_modification, id_user_registration, removed, date_modification, date_registration, description, category)
VALUES
  (10, NULL, 1, b'0', NULL, NOW(), 'Donación de arroz y fideos', 'ALIMENTO'),
  (5, NULL, 2, b'0', NULL, NOW(), 'Donación de juguetes nuevos', 'JUGUETE'),
  (20, NULL, 1, b'0', NULL, NOW(), 'Donación de camisetas y abrigos', 'ROPA'),
  (15, NULL, 3, b'0', NULL, NOW(), 'Lapices de colores básicos', 'UTIL_ESCOLAR'),
  (8, NULL, 2, b'0', NULL, NOW(), 'Caja de alimentos secos', 'ALIMENTO'),
  (12, NULL, 3, b'0', NULL, NOW(), 'Zapatos para niños', 'ROPA'),
  (7, NULL, 1, b'0', NULL, NOW(), 'Juguetes de segunda mano', 'JUGUETE'),
  (25, NULL, 3, b'0', NULL, NOW(), 'Mochilas negras', 'UTIL_ESCOLAR'),
  (6, NULL, 2, b'0', NULL, NOW(), 'Caja de leche en polvo', 'ALIMENTO'),
  (4, NULL, 1, b'0', NULL, NOW(), 'Bufandas y gorros de lana', 'ROPA');

-- ==============================================
-- Asociar Donaciones a Eventos
-- ==============================================
INSERT INTO donations_at_events (id_donation, id_event, quantity_delivered)
VALUES
  (1, 1, 5),
  (2, 3, 5),
  (3, 2, 10),
  (4, 4, 15),
  (5, 1, 8),
  (6, 5, 12),
  (7, 3, 7),
  (8, 4, 25),
  (9, 2, 6),
  (10, 5, 4);
  
INSERT INTO member_at_event (user_id, event_id)
VALUES
  (1, 1),
  (2, 1),
  (3, 1 ),
  (1, 2 ),
  (2, 2 ),
  (1, 3 ),
  (3, 3);
