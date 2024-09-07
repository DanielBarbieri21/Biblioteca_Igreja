import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Biblioteca {
    private static final String URL = "jdbc:postgresql://localhost:5432/biblioteca_comunitaria";
    private static final String USER = "Postgres";  
    private static final String PASSWORD = "246895";  

    // Método para conectar ao banco de dados PostgreSQL
    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexão com o banco de dados estabelecida!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // Método para inserir um novo usuário no banco de dados
    public void adicionarUsuario(String nome, String endereco, String telefone, String email) {
        String sql = "INSERT INTO usuario(nome, endereco, telefone, email) VALUES(?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome);
            pstmt.setString(2, endereco);
            pstmt.setString(3, telefone);
            pstmt.setString(4, email);

            pstmt.executeUpdate();
            System.out.println("Usuário adicionado com sucesso!");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Método para consultar os livros disponíveis no banco de dados
    public void listarLivrosDisponiveis() {
        String sql = "SELECT id, titulo, autor, genero, ano_publicacao FROM livro WHERE status = 'DISPONIVEL'";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Título: " + rs.getString("titulo"));
                System.out.println("Autor: " + rs.getString("autor"));
                System.out.println("Gênero: " + rs.getString("genero"));
                System.out.println("Ano: " + rs.getInt("ano_publicacao"));
                System.out.println();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Método para registrar um empréstimo
    public void registrarEmprestimo(int usuarioId, int livroId, String dataPrevistaDevolucao) {
        String sql = "INSERT INTO emprestimo(usuario_id, livro_id, data_prevista_devolucao) VALUES(?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, usuarioId);
            pstmt.setInt(2, livroId);
            pstmt.setDate(3, java.sql.Date.valueOf(dataPrevistaDevolucao));

            pstmt.executeUpdate();
            System.out.println("Empréstimo registrado com sucesso!");

            // Atualizar o status do livro para 'EMPRESTADO'
            String updateSql = "UPDATE livro SET status = 'EMPRESTADO' WHERE id = ?";
            try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
                updatePstmt.setInt(1, livroId);
                updatePstmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Exemplo de uso
    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca();
        biblioteca.adicionarUsuario("João Silva", "Rua A, 123", "99999-9999", "joao@email.com");
        biblioteca.listarLivrosDisponiveis();
        biblioteca.registrarEmprestimo(1, 2, "2024-09-15");
    }
}
