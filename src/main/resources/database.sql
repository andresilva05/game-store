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

-- 2. Tabela de Plataformas (Módulo 1)
CREATE TABLE IF NOT EXISTS plataforma
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

-- 3. Tabela de Gift Cards / Produtos (Módulo 1)
CREATE TABLE IF NOT EXISTS produto
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    plataforma_id INT            NOT NULL,
    titulo        VARCHAR(150)   NOT NULL,
    valor_credito DECIMAL(10, 2) NOT NULL,
    preco_venda   DECIMAL(10, 2) NOT NULL,
    estoque       INT            NOT NULL DEFAULT 0,
    ativo         BOOLEAN        NOT NULL DEFAULT TRUE,
    FOREIGN KEY (plataforma_id) REFERENCES plataforma (id)
);

-- 4. Tabela de Pedidos (Módulo 4)
CREATE TABLE IF NOT EXISTS pedido
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario   INT            NOT NULL,
    date_compra  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valor_compra DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario (id)
);

-- 5. Tabela de Itens de Pedido (Módulo 4)
CREATE TABLE IF NOT EXISTS item_pedido
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id  INT            NOT NULL,
    preco      DECIMAL(10, 2) NOT NULL,
    produto_id INT            NOT NULL,
    pin        VARCHAR(50)    NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedido (id),
    FOREIGN KEY (produto_id) REFERENCES produto (id)
);

-- =============================================================================
-- DADOS INICIAIS (SEEDS)
-- =============================================================================

-- Usuários padrão com hashes BCrypt válidos (Módulo 2):
-- ADMIN:   admin@gamestore.com   / admin123
-- CLIENTE: cliente@gamestore.com / cliente123
INSERT IGNORE INTO usuario (nome, email, senha, papel)
VALUES ('Administrador GameStore', 'admin@gamestore.com', '$2a$10$qh04Zqs.q.x6KaWX1EDOaez1cgFri98577KV3AIEJDo.7nCCkdIEy', 'ADMIN'),
       ('Cliente Demonstração', 'cliente@gamestore.com', '$2a$10$e7gkPr/nSse9i/p6mVRgfuBaxeYemeDnwUOfeqZX8H4mehXXgeuJ.', 'CLIENTE');

-- Plataformas iniciais (Módulo 1):
INSERT IGNORE INTO plataforma (id, nome) VALUES
(1, 'Steam'),
(2, 'PlayStation'),
(3, 'Xbox'),
(4, 'Nintendo'),
(5, 'Riot Games');

-- Produtos de teste (Módulo 1):
INSERT IGNORE INTO produto (id, plataforma_id, titulo, valor_credito, preco_venda, estoque, ativo) VALUES
(1, 1, 'Steam Wallet R$ 50', 50.00, 48.00, 15, TRUE),
(2, 1, 'Steam Wallet R$ 100', 100.00, 95.00, 8, TRUE),
(3, 2, 'PlayStation Store R$ 60', 60.00, 58.50, 10, TRUE),
(4, 3, 'Xbox Game Pass Ultimate 1 Mês', 49.99, 44.90, 5, TRUE),
(5, 5, 'Cartão 1350 VP - Valorant', 40.00, 38.00, 20, TRUE);
