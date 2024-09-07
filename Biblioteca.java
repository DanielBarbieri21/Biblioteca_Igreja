import java.util.ArrayList;
import java.util.List;

public class Biblioteca {
    private List<Livro> livros;
    private List<Usuario> usuarios;
    private List<Emprestimo> emprestimos;

    public Biblioteca() {
        this.livros = new ArrayList<>();
        this.usuarios = new ArrayList<>();
        this.emprestimos = new ArrayList<>();
    }

    // Adicionar Livro à Biblioteca
    public void adicionarLivro(Livro livro) {
        livros.add(livro);
    }

    // Registrar Usuário na Biblioteca
    public void registrarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    // Realizar Empréstimo
    public void emprestarLivro(String isbn, String idUsuario) {
        Livro livro = encontrarLivro(isbn);
        Usuario usuario = encontrarUsuario(idUsuario);

        if (livro != null && livro.isDisponivel() && usuario != null) {
            Emprestimo emprestimo = new Emprestimo(livro, usuario);
            emprestimos.add(emprestimo);
            System.out.println("Empréstimo realizado: " + emprestimo);
        } else {
            System.out.println("Erro ao realizar empréstimo. Verifique se o livro está disponível e o usuário é válido.");
        }
    }

    // Devolver Livro
    public void devolverLivro(String isbn, String idUsuario) {
        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.livro.getIsbn().equals(isbn) && emprestimo.usuario.getId().equals(idUsuario)) {
                emprestimo.devolverLivro();
                System.out.println("Livro devolvido: " + emprestimo);
                return;
            }
        }
        System.out.println("Empréstimo não encontrado.");
    }

    // Encontrar Livro pelo ISBN
    private Livro encontrarLivro(String isbn) {
        for (Livro livro : livros) {
            if (livro.getIsbn().equals(isbn)) {
                return livro;
            }
        }
        return null;
    }

    // Encontrar Usuário pelo ID
    private Usuario encontrarUsuario(String idUsuario) {
        for (Usuario usuario : usuarios) {
            if (usuario.getId().equals(idUsuario)) {
                return usuario;
            }
        }
        return null;
    }

    // Relatório de Livros
    public void listarLivros() {
        for (Livro livro : livros) {
            System.out.println(livro);
        }
    }

    // Relatório de Usuários
    public void listarUsuarios() {
        for (Usuario usuario : usuarios) {
            System.out.println(usuario);
        }
    }

    // Relatório de Empréstimos
    public void listarEmprestimos() {
        for (Emprestimo emprestimo : emprestimos) {
            System.out.println(emprestimo);
        }
    }
}
