CREATE DATABASE IF NOT EXISTS gamestore;
USE gamestore;

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

    FOREIGN KEY (pedido_id) REFERENCES pedido (id)
    -- FOREIGN KEY (produto_id) REFERENCES produto (id)
);