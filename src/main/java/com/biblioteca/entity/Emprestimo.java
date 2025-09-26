package com.biblioteca.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "emprestimo")
public class Emprestimo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Usuário é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @NotNull(message = "Livro é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;
    
    @CreationTimestamp
    @Column(name = "data_emprestimo", nullable = false, updatable = false)
    private LocalDateTime dataEmprestimo;
    
    @NotNull(message = "Data prevista de devolução é obrigatória")
    @Column(name = "data_prevista_devolucao", nullable = false)
    private LocalDate dataPrevistaDevolucao;
    
    @Column(name = "data_devolucao")
    private LocalDateTime dataDevolucao;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusEmprestimo status = StatusEmprestimo.ATIVO;
    
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;
    
    @Column(name = "multa", precision = 10, scale = 2)
    private BigDecimal multa = BigDecimal.ZERO;
    
    // Construtores
    public Emprestimo() {}
    
    public Emprestimo(Usuario usuario, Livro livro, LocalDate dataPrevistaDevolucao) {
        this.usuario = usuario;
        this.livro = livro;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.status = StatusEmprestimo.ATIVO;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public Livro getLivro() {
        return livro;
    }
    
    public void setLivro(Livro livro) {
        this.livro = livro;
    }
    
    public LocalDateTime getDataEmprestimo() {
        return dataEmprestimo;
    }
    
    public void setDataEmprestimo(LocalDateTime dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }
    
    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }
    
    public void setDataPrevistaDevolucao(LocalDate dataPrevistaDevolucao) {
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
    }
    
    public LocalDateTime getDataDevolucao() {
        return dataDevolucao;
    }
    
    public void setDataDevolucao(LocalDateTime dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }
    
    public StatusEmprestimo getStatus() {
        return status;
    }
    
    public void setStatus(StatusEmprestimo status) {
        this.status = status;
    }
    
    public String getObservacoes() {
        return observacoes;
    }
    
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    
    public BigDecimal getMulta() {
        return multa;
    }
    
    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }
    
    // Métodos auxiliares
    public boolean isAtrasado() {
        return status == StatusEmprestimo.ATIVO && 
               LocalDate.now().isAfter(dataPrevistaDevolucao);
    }
    
    public void devolver() {
        this.dataDevolucao = LocalDateTime.now();
        this.status = StatusEmprestimo.DEVOLVIDO;
        this.livro.incrementarDisponivel();
    }
    
    public void marcarComoAtrasado() {
        if (status == StatusEmprestimo.ATIVO && isAtrasado()) {
            this.status = StatusEmprestimo.ATRASADO;
        }
    }
    
    public long getDiasAtraso() {
        if (status == StatusEmprestimo.ATIVO && isAtrasado()) {
            return LocalDate.now().toEpochDay() - dataPrevistaDevolucao.toEpochDay();
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return "Emprestimo{" +
                "id=" + id +
                ", usuario=" + (usuario != null ? usuario.getNome() : "null") +
                ", livro=" + (livro != null ? livro.getTitulo() : "null") +
                ", dataEmprestimo=" + dataEmprestimo +
                ", dataPrevistaDevolucao=" + dataPrevistaDevolucao +
                ", status=" + status +
                '}';
    }
}
