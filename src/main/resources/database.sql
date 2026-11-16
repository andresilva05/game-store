-- Criação do banco de dados para Game Store
DROP DATABASE IF EXISTS gamestore;
CREATE DATABASE gamestore;
USE gamestore;

-- 1. Tabela de Usuários (Módulo 2)
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    papel ENUM('CLIENTE', 'ADMIN') NOT NULL DEFAULT 'CLIENTE'
);

