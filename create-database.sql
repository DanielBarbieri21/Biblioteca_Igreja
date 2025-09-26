-- Script para criar o banco de dados da Biblioteca Igreja
-- Execute este script no PostgreSQL

-- Criar banco de dados
CREATE DATABASE biblioteca_comunitaria;

-- Conectar ao banco criado
\c biblioteca_comunitaria;

-- Criar usuário (se não existir)
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'postgres') THEN
        CREATE USER postgres WITH PASSWORD '246895';
    END IF;
END
$$;

-- Dar permissões
GRANT ALL PRIVILEGES ON DATABASE biblioteca_comunitaria TO postgres;

-- Verificar se foi criado
SELECT 'Banco biblioteca_comunitaria criado com sucesso!' as status;
