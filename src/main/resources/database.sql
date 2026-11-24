CREATE DATABASE IF NOT EXISTS gamestore;
USE gamestore;

-- 1. Tabela de Usuários (Módulo 2)
CREATE TABLE IF NOT EXISTS usuario
(
    id    INT AUTO_INCREMENT PRIMARY KEY,
    nome  VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    papel ENUM('CLIENTE', 'ADMIN') NOT NULL DEFAULT 'CLIENTE'
);

-- 2. Tabela de Pedidos (Módulo 4)
CREATE TABLE IF NOT EXISTS pedido
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario   INT            NOT NULL,
    date_compra  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valor_compra DECIMAL(10, 2) NOT NULL,

    FOREIGN KEY (id_usuario) REFERENCES usuario (id)
);

-- 3. Tabela de Itens do Pedido (Módulo 4)
CREATE TABLE IF NOT EXISTS item_pedido
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id  INT            NOT NULL,
    preco      DECIMAL(10, 2) NOT NULL,
    produto_id INT            NOT NULL,
    pin        VARCHAR(50)    NOT NULL,

    FOREIGN KEY (pedido_id) REFERENCES pedido (id)
    -- FOREIGN KEY (produto_id) REFERENCES produto (id)
);

-- Dados iniciais (Seed) com hashes BCrypt válidos:
-- ADMIN:   admin@gamestore.com   / admin123
-- CLIENTE: cliente@gamestore.com / cliente123
INSERT IGNORE INTO usuario (nome, email, senha, papel)
VALUES ('Administrador GameStore', 'admin@gamestore.com', '$2a$10$qh04Zqs.q.x6KaWX1EDOaez1cgFri98577KV3AIEJDo.7nCCkdIEy', 'ADMIN'),
       ('Cliente Demonstração', 'cliente@gamestore.com', '$2a$10$e7gkPr/nSse9i/p6mVRgfuBaxeYemeDnwUOfeqZX8H4mehXXgeuJ.', 'CLIENTE');
