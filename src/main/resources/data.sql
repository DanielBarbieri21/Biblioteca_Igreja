-- Inserir usuário admin com senha criptografada
-- Senha: admin123 (criptografada com BCrypt)
INSERT INTO usuario (nome, email, telefone, endereco, senha, role, ativo, data_cadastro) VALUES
('Administrador', 'admin@biblioteca.com', '(32) 99999-0000', 'Sistema', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'ADMIN', true, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;
