-- Script SQL com dados fictícios completos para testar todas as funcionalidades
-- da Biblioteca da Igreja

-- Limpar dados existentes (opcional - descomente se necessário)
-- DELETE FROM emprestimo;
-- DELETE FROM livro;
-- DELETE FROM usuario;

-- Inserir usuários fictícios
INSERT INTO usuario (nome, email, telefone, endereco, ativo) VALUES
('João Silva Santos', 'joao.silva@igreja.com', '(11) 99999-1111', 'Rua das Flores, 123 - Centro', true),
('Maria Santos Oliveira', 'maria.santos@igreja.com', '(11) 99999-2222', 'Av. Principal, 456 - Bairro Novo', true),
('Pedro Oliveira Costa', 'pedro.oliveira@igreja.com', '(11) 99999-3333', 'Rua da Paz, 789 - Vila Esperança', true),
('Ana Costa Ferreira', 'ana.costa@igreja.com', '(11) 99999-4444', 'Rua da Esperança, 321 - Jardim', true),
('Carlos Ferreira Lima', 'carlos.ferreira@igreja.com', '(11) 99999-5555', 'Av. Central, 654 - Centro', true),
('Lucia Lima Rodrigues', 'lucia.lima@igreja.com', '(11) 99999-6666', 'Rua da Liberdade, 987 - Vila Nova', true),
('Roberto Rodrigues Alves', 'roberto.rodrigues@igreja.com', '(11) 99999-7777', 'Av. Brasil, 147 - Centro', true),
('Fernanda Alves Pereira', 'fernanda.alves@igreja.com', '(11) 99999-8888', 'Rua da Amizade, 258 - Bairro Alto', true),
('Marcos Pereira Souza', 'marcos.pereira@igreja.com', '(11) 99999-9999', 'Av. Paulista, 369 - Centro', true),
('Juliana Souza Martins', 'juliana.souza@igreja.com', '(11) 99999-0000', 'Rua da Fé, 741 - Vila Fé', true);

-- Inserir livros fictícios com diferentes gêneros e quantidades
INSERT INTO livro (titulo, autor, isbn, genero, ano_publicacao, editora, quantidade_total, quantidade_disponivel, ativo) VALUES
-- Livros Religiosos
('A Bíblia Sagrada - Nova Versão Internacional', 'Vários Autores', '978-85-311-0001-1', 'Religioso', 2020, 'SBB', 10, 8, true),
('O Peregrino', 'John Bunyan', '978-85-311-0002-2', 'Religioso', 1678, 'Vida Nova', 5, 4, true),
('Cristianismo Puro e Simples', 'C.S. Lewis', '978-85-311-0003-3', 'Religioso', 1952, 'Thomas Nelson', 3, 2, true),
('A Imitação de Cristo', 'Tomás de Kempis', '978-85-311-0004-4', 'Religioso', 1418, 'Paulus', 4, 3, true),

-- Literatura Brasileira
('Dom Casmurro', 'Machado de Assis', '978-85-311-0005-5', 'Literatura Brasileira', 1899, 'Ática', 6, 5, true),
('O Cortiço', 'Aluísio Azevedo', '978-85-311-0006-6', 'Literatura Brasileira', 1890, 'Ática', 4, 3, true),
('Capitães da Areia', 'Jorge Amado', '978-85-311-0007-7', 'Literatura Brasileira', 1937, 'Record', 5, 4, true),
('Vidas Secas', 'Graciliano Ramos', '978-85-311-0008-8', 'Literatura Brasileira', 1938, 'Record', 3, 2, true),

-- Literatura Infantil
('O Pequeno Príncipe', 'Antoine de Saint-Exupéry', '978-85-311-0009-9', 'Infantil', 1943, 'Agir', 8, 6, true),
('Alice no País das Maravilhas', 'Lewis Carroll', '978-85-311-0010-0', 'Infantil', 1865, 'Zahar', 4, 3, true),
('O Mágico de Oz', 'L. Frank Baum', '978-85-311-0011-1', 'Infantil', 1900, 'Zahar', 5, 4, true),

-- Literatura Estrangeira
('Cem Anos de Solidão', 'Gabriel García Márquez', '978-85-311-0012-2', 'Literatura Estrangeira', 1967, 'Record', 3, 2, true),
('1984', 'George Orwell', '978-85-311-0013-3', 'Ficção Científica', 1949, 'Companhia das Letras', 4, 3, true),
('A Revolução dos Bichos', 'George Orwell', '978-85-311-0014-4', 'Ficção', 1945, 'Companhia das Letras', 5, 4, true),
('O Senhor dos Anéis', 'J.R.R. Tolkien', '978-85-311-0015-5', 'Fantasia', 1954, 'Martins Fontes', 3, 2, true),

-- Filosofia e Autoajuda
('A Arte da Guerra', 'Sun Tzu', '978-85-311-0016-6', 'Filosofia', 500, 'Martins Fontes', 4, 3, true),
('O Alquimista', 'Paulo Coelho', '978-85-311-0017-7', 'Ficção', 1988, 'Rocco', 6, 5, true),
('Os Sete Hábitos das Pessoas Altamente Eficazes', 'Stephen Covey', '978-85-311-0018-8', 'Autoajuda', 1989, 'Best Business', 3, 2, true),

-- História e Biografia
('A História do Brasil', 'Boris Fausto', '978-85-311-0019-9', 'História', 1994, 'Edusp', 2, 1, true),
('Memórias Póstumas de Brás Cubas', 'Machado de Assis', '978-85-311-0020-0', 'Literatura Brasileira', 1881, 'Ática', 3, 2, true);

-- Inserir empréstimos fictícios para testar funcionalidades
INSERT INTO emprestimo (usuario_id, livro_id, data_emprestimo, data_prevista_devolucao, data_devolucao, status, observacoes, multa) VALUES
-- Empréstimos ativos (não devolvidos)
(1, 1, '2024-09-20 10:00:00', '2024-10-05', NULL, 'ATIVO', 'Empréstimo para estudo bíblico', 0.00),
(2, 5, '2024-09-22 14:30:00', '2024-10-07', NULL, 'ATIVO', 'Leitura para escola dominical', 0.00),
(3, 9, '2024-09-25 09:15:00', '2024-10-10', NULL, 'ATIVO', 'Leitura com os filhos', 0.00),
(4, 12, '2024-09-26 16:45:00', '2024-10-11', NULL, 'ATIVO', 'Estudo de literatura', 0.00),

-- Empréstimos vencendo em breve (próximos 7 dias)
(5, 2, '2024-09-15 11:20:00', '2024-09-30', NULL, 'ATIVO', 'Leitura espiritual', 0.00),
(6, 6, '2024-09-18 13:10:00', '2024-10-03', NULL, 'ATIVO', 'Estudo acadêmico', 0.00),
(7, 10, '2024-09-19 15:30:00', '2024-10-04', NULL, 'ATIVO', 'Leitura recreativa', 0.00),

-- Empréstimos atrasados
(8, 3, '2024-09-05 08:45:00', '2024-09-20', NULL, 'ATIVO', 'Estudo teológico', 0.00),
(9, 7, '2024-09-08 12:00:00', '2024-09-23', NULL, 'ATIVO', 'Pesquisa escolar', 0.00),
(10, 11, '2024-09-10 14:15:00', '2024-09-25', NULL, 'ATIVO', 'Leitura familiar', 0.00),

-- Empréstimos já devolvidos (histórico)
(1, 4, '2024-08-15 10:00:00', '2024-08-30', '2024-08-28 16:30:00', 'DEVOLVIDO', 'Devolvido no prazo', 0.00),
(2, 8, '2024-08-20 14:00:00', '2024-09-04', '2024-09-02 11:45:00', 'DEVOLVIDO', 'Devolvido antecipadamente', 0.00),
(3, 13, '2024-08-25 09:30:00', '2024-09-09', '2024-09-10 14:20:00', 'DEVOLVIDO', 'Devolvido com 1 dia de atraso', 2.00),
(4, 15, '2024-09-01 15:00:00', '2024-09-16', '2024-09-18 10:15:00', 'DEVOLVIDO', 'Devolvido com 2 dias de atraso', 4.00),
(5, 16, '2024-09-03 11:00:00', '2024-09-18', '2024-09-20 13:30:00', 'DEVOLVIDO', 'Devolvido com multa', 4.00),

-- Mais empréstimos para estatísticas
(6, 17, '2024-08-10 08:00:00', '2024-08-25', '2024-08-23 17:00:00', 'DEVOLVIDO', 'Devolvido no prazo', 0.00),
(7, 18, '2024-08-12 13:30:00', '2024-08-27', '2024-08-26 15:45:00', 'DEVOLVIDO', 'Devolvido no prazo', 0.00),
(8, 19, '2024-08-18 16:00:00', '2024-09-02', '2024-09-01 12:00:00', 'DEVOLVIDO', 'Devolvido no prazo', 0.00),
(9, 20, '2024-08-22 10:30:00', '2024-09-06', '2024-09-05 14:30:00', 'DEVOLVIDO', 'Devolvido no prazo', 0.00),
(10, 1, '2024-08-28 12:00:00', '2024-09-12', '2024-09-11 16:00:00', 'DEVOLVIDO', 'Devolvido no prazo', 0.00);

-- Atualizar quantidades disponíveis dos livros baseado nos empréstimos ativos
UPDATE livro SET quantidade_disponivel = quantidade_total - (
    SELECT COUNT(*) FROM emprestimo 
    WHERE emprestimo.livro_id = livro.id 
    AND emprestimo.status = 'ATIVO'
) WHERE id IN (
    SELECT DISTINCT livro_id FROM emprestimo WHERE status = 'ATIVO'
);

-- Verificar dados inseridos
SELECT 'Usuários inseridos:' as info, COUNT(*) as total FROM usuario WHERE ativo = true;
SELECT 'Livros inseridos:' as info, COUNT(*) as total FROM livro WHERE ativo = true;
SELECT 'Empréstimos ativos:' as info, COUNT(*) as total FROM emprestimo WHERE status = 'ATIVO';
SELECT 'Empréstimos devolvidos:' as info, COUNT(*) as total FROM emprestimo WHERE status = 'DEVOLVIDO';
SELECT 'Empréstimos atrasados:' as info, COUNT(*) as total FROM emprestimo WHERE status = 'ATIVO' AND data_prevista_devolucao < CURRENT_DATE;
