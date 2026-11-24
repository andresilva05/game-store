CREATE DATABASE IF NOT EXISTS gamestore;
USE gamestore;

CREATE TABLE IF NOT EXISTS plataforma (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS produto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    plataforma_id INT NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    valor_credito DECIMAL(10, 2) NOT NULL,
    preco_venda DECIMAL(10, 2) NOT NULL,
    estoque INT NOT NULL DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (plataforma_id) REFERENCES plataforma (id)
);

CREATE TABLE pedido
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario   INT            NOT NULL,
    date_compra  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valor_compra DECIMAL(10, 2) NOT NULL

    -- FOREIGN KEY (id_usuario) REFERENCES usuario (id)
);

CREATE TABLE item_pedido
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id  INT            NOT NULL,
    preco      DECIMAL(10, 2) NOT NULL,
    produto_id INT            NOT NULL,
    pin        VARCHAR(50)    NOT NULL,

    FOREIGN KEY (pedido_id) REFERENCES pedido (id),
    FOREIGN KEY (produto_id) REFERENCES produto (id)
);