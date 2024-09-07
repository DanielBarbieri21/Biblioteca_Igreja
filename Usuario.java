public class Usuario {
    private String nome;
    private String id;
    private int livrosEmprestados;

    public Usuario(String nome, String id) {
        this.nome = nome;
        this.id = id;
        this.livrosEmprestados = 0; // Inicia com 0 livros emprestados
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public String getId() {
        return id;
    }

    public int getLivrosEmprestados() {
        return livrosEmprestados;
    }

    public void adicionarEmprestimo() {
        this.livrosEmprestados++;
    }

    public void removerEmprestimo() {
        if (livrosEmprestados > 0) {
            this.livrosEmprestados--;
        }
    }

    @Override
    public String toString() {
        return "Usuário [Nome=" + nome + ", ID=" + id + ", Livros Emprestados=" + livrosEmprestados + "]";
    }
}
