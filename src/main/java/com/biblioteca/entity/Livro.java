package com.biblioteca.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "livro")
public class Livro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Título é obrigatório")
    @Size(max = 200, message = "Título deve ter no máximo 200 caracteres")
    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;
    
    @NotBlank(message = "Autor é obrigatório")
    @Size(max = 100, message = "Autor deve ter no máximo 100 caracteres")
    @Column(name = "autor", nullable = false, length = 100)
    private String autor;
    
    @Size(max = 20, message = "ISBN deve ter no máximo 20 caracteres")
    @Column(name = "isbn", unique = true, length = 20)
    private String isbn;
    
    @Size(max = 50, message = "Gênero deve ter no máximo 50 caracteres")
    @Column(name = "genero", length = 50)
    private String genero;
    
    @Column(name = "ano_publicacao")
    private Integer anoPublicacao;
    
    @Size(max = 100, message = "Editora deve ter no máximo 100 caracteres")
    @Column(name = "editora", length = 100)
    private String editora;
    
    @NotNull(message = "Quantidade total é obrigatória")
    @Positive(message = "Quantidade total deve ser positiva")
    @Column(name = "quantidade_total", nullable = false)
    private Integer quantidadeTotal = 1;
    
    @NotNull(message = "Quantidade disponível é obrigatória")
    @Column(name = "quantidade_disponivel", nullable = false)
    private Integer quantidadeDisponivel = 1;
    
    @CreationTimestamp
    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;
    
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;
    
    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Emprestimo> emprestimos = new ArrayList<>();
    
    // Construtores
    public Livro() {}
    
    public Livro(String titulo, String autor, String isbn, String genero, 
                 Integer anoPublicacao, String editora, Integer quantidadeTotal) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.genero = genero;
        this.anoPublicacao = anoPublicacao;
        this.editora = editora;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeDisponivel = quantidadeTotal;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getAutor() {
        return autor;
    }
    
    public void setAutor(String autor) {
        this.autor = autor;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public String getGenero() {
        return genero;
    }
    
    public void setGenero(String genero) {
        this.genero = genero;
    }
    
    public Integer getAnoPublicacao() {
        return anoPublicacao;
    }
    
    public void setAnoPublicacao(Integer anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }
    
    public String getEditora() {
        return editora;
    }
    
    public void setEditora(String editora) {
        this.editora = editora;
    }
    
    public Integer getQuantidadeTotal() {
        return quantidadeTotal;
    }
    
    public void setQuantidadeTotal(Integer quantidadeTotal) {
        this.quantidadeTotal = quantidadeTotal;
    }
    
    public Integer getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }
    
    public void setQuantidadeDisponivel(Integer quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }
    
    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }
    
    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
    
    public Boolean getAtivo() {
        return ativo;
    }
    
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
    
    public List<Emprestimo> getEmprestimos() {
        return emprestimos;
    }
    
    public void setEmprestimos(List<Emprestimo> emprestimos) {
        this.emprestimos = emprestimos;
    }
    
    // Métodos auxiliares
    public boolean isDisponivel() {
        return quantidadeDisponivel > 0 && ativo;
    }
    
    public void decrementarDisponivel() {
        if (quantidadeDisponivel > 0) {
            quantidadeDisponivel--;
        }
    }
    
    public void incrementarDisponivel() {
        if (quantidadeDisponivel < quantidadeTotal) {
            quantidadeDisponivel++;
        }
    }
    
    @Override
    public String toString() {
        return "Livro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", isbn='" + isbn + '\'' +
                ", genero='" + genero + '\'' +
                ", quantidadeDisponivel=" + quantidadeDisponivel +
                ", ativo=" + ativo +
                '}';
    }
}
