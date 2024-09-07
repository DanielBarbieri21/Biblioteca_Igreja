import java.util.Date;

public class Emprestimo {
    private Livro livro;
    private Usuario usuario;
    private Date dataEmprestimo;
    private Date dataDevolucao;

    public Emprestimo(Livro livro, Usuario usuario) {
        this.livro = livro;
        this.usuario = usuario;
        this.dataEmprestimo = new Date();
        this.dataDevolucao = null;
        livro.setDisponivel(false); // Marca o livro como emprestado
        usuario.adicionarEmprestimo();
    }

    public void devolverLivro() {
        this.dataDevolucao = new Date();
        livro.setDisponivel(true); // Marca o livro como disponível
        usuario.removerEmprestimo();
    }

    @Override
    public String toString() {
        return "Empréstimo [Livro=" + livro.getTitulo() + ", Usuário=" + usuario.getNome() + ", Data do Empréstimo="
                + dataEmprestimo + ", Data da Devolução=" + (dataDevolucao != null ? dataDevolucao : "Ainda não devolvido") + "]";
    }
}
