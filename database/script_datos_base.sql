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
);