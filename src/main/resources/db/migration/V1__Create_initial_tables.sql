-- Criação das tabelas iniciais da biblioteca

-- Tabela de usuários
CREATE TABLE IF NOT EXISTS usuario (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telefone VARCHAR(20),
    endereco TEXT,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN DEFAULT TRUE
);

-- Tabela de livros
CREATE TABLE IF NOT EXISTS livro (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    autor VARCHAR(100) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    genero VARCHAR(50),
    ano_publicacao INTEGER,
    editora VARCHAR(100),
    quantidade_total INTEGER DEFAULT 1,
    quantidade_disponivel INTEGER DEFAULT 1,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN DEFAULT TRUE
);

-- Tabela de empréstimos
CREATE TABLE IF NOT EXISTS emprestimo (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id),
    livro_id BIGINT NOT NULL REFERENCES livro(id),
    data_emprestimo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_prevista_devolucao DATE NOT NULL,
    data_devolucao TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ATIVO' CHECK (status IN ('ATIVO', 'DEVOLVIDO', 'ATRASADO')),
    observacoes TEXT,
    multa DECIMAL(10,2) DEFAULT 0.00
);

-- Índices para melhor performance
CREATE INDEX IF NOT EXISTS idx_emprestimo_usuario ON emprestimo(usuario_id);
CREATE INDEX IF NOT EXISTS idx_emprestimo_livro ON emprestimo(livro_id);
CREATE INDEX IF NOT EXISTS idx_emprestimo_status ON emprestimo(status);
CREATE INDEX IF NOT EXISTS idx_livro_titulo ON livro(titulo);
CREATE INDEX IF NOT EXISTS idx_livro_autor ON livro(autor);
CREATE INDEX IF NOT EXISTS idx_usuario_email ON usuario(email);
