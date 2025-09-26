package com.biblioteca.repository;

import com.biblioteca.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Buscar por email
    Optional<Usuario> findByEmail(String email);
    
    // Buscar por nome (case insensitive)
    List<Usuario> findByNomeContainingIgnoreCase(String nome);
    
    // Buscar usuários ativos
    List<Usuario> findByAtivoTrue();
    
    // Buscar usuários ativos com paginação
    Page<Usuario> findByAtivoTrue(Pageable pageable);
    
    // Buscar por nome e ativo
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND u.ativo = true")
    Page<Usuario> findByNomeContainingIgnoreCaseAndAtivoTrue(@Param("nome") String nome, Pageable pageable);
    
    // Verificar se email existe (excluindo o próprio usuário)
    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.email = :email AND (:id IS NULL OR u.id != :id)")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);
    
    // Contar usuários ativos
    long countByAtivoTrue();
    
    // Buscar usuários com empréstimos ativos
    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.emprestimos e WHERE e.status = 'ATIVO' AND u.ativo = true")
    List<Usuario> findUsuariosComEmprestimosAtivos();
    
    // Buscar usuários com empréstimos atrasados
    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.emprestimos e WHERE e.status = 'ATIVO' AND e.dataPrevistaDevolucao < CURRENT_DATE AND u.ativo = true")
    List<Usuario> findUsuariosComEmprestimosAtrasados();
}
