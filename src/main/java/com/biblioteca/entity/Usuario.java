package com.biblioteca.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 100, message = "Email deve ter no máximo 100 caracteres")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    @Column(name = "telefone", length = 20)
    private String telefone;
    
    @Column(name = "endereco", columnDefinition = "TEXT")
    private String endereco;
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    @Column(name = "senha", nullable = false)
    private String senha;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;
    
    @CreationTimestamp
    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;
    
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Emprestimo> emprestimos = new ArrayList<>();
    
    // Construtores
    public Usuario() {}
    
    public Usuario(String nome, String email, String telefone, String endereco) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelefone() {
        return telefone;
    }
    
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
    public String getEndereco() {
        return endereco;
    }
    
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    
    public String getSenha() {
        return senha;
    }
    
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
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
    public int getQuantidadeEmprestimosAtivos() {
        return (int) emprestimos.stream()
                .filter(e -> e.getStatus() == StatusEmprestimo.ATIVO)
                .count();
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}
