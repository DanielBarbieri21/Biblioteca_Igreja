package com.biblioteca.repository;

import com.biblioteca.entity.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
    
    // Buscar por ISBN
    Optional<Livro> findByIsbn(String isbn);
    
    // Buscar por título (case insensitive)
    List<Livro> findByTituloContainingIgnoreCase(String titulo);
    
    // Buscar por autor (case insensitive)
    List<Livro> findByAutorContainingIgnoreCase(String autor);
    
    // Buscar por gênero
    List<Livro> findByGenero(String genero);
    
    // Buscar livros disponíveis
    List<Livro> findByQuantidadeDisponivelGreaterThanAndAtivoTrue(int quantidade);
    
    // Buscar livros disponíveis com paginação
    Page<Livro> findByQuantidadeDisponivelGreaterThanAndAtivoTrue(int quantidade, Pageable pageable);
    
    // Buscar todos os livros ativos
    List<Livro> findByAtivoTrue();
    
    // Buscar todos os livros ativos com paginação
    Page<Livro> findByAtivoTrue(Pageable pageable);
    
    // Busca avançada por múltiplos critérios
    @Query("SELECT l FROM Livro l WHERE " +
           "(:titulo IS NULL OR LOWER(l.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))) AND " +
           "(:autor IS NULL OR LOWER(l.autor) LIKE LOWER(CONCAT('%', :autor, '%'))) AND " +
           "(:genero IS NULL OR LOWER(l.genero) LIKE LOWER(CONCAT('%', :genero, '%'))) AND " +
           "(:ano IS NULL OR l.anoPublicacao = :ano) AND " +
           "(:disponivel IS NULL OR l.quantidadeDisponivel > 0) AND " +
           "l.ativo = true")
    Page<Livro> findByCriterios(@Param("titulo") String titulo, 
                               @Param("autor") String autor, 
                               @Param("genero") String genero, 
                               @Param("ano") Integer ano, 
                               @Param("disponivel") Boolean disponivel, 
                               Pageable pageable);
    
    // Verificar se ISBN existe (excluindo o próprio livro)
    @Query("SELECT COUNT(l) > 0 FROM Livro l WHERE l.isbn = :isbn AND (:id IS NULL OR l.id != :id)")
    boolean existsByIsbnAndIdNot(@Param("isbn") String isbn, @Param("id") Long id);
    
    // Contar livros disponíveis
    @Query("SELECT COUNT(l) FROM Livro l WHERE l.quantidadeDisponivel > 0 AND l.ativo = true")
    long countLivrosDisponiveis();
    
    // Contar total de livros
    @Query("SELECT COUNT(l) FROM Livro l WHERE l.ativo = true")
    long countLivrosAtivos();
    
    // Buscar livros mais emprestados
    @Query("SELECT l FROM Livro l LEFT JOIN l.emprestimos e WHERE l.ativo = true " +
           "GROUP BY l.id ORDER BY COUNT(e.id) DESC")
    List<Livro> findLivrosMaisEmprestados(Pageable pageable);
    
    // Buscar livros por gênero com contagem
    @Query("SELECT l.genero, COUNT(l) FROM Livro l WHERE l.ativo = true GROUP BY l.genero ORDER BY COUNT(l) DESC")
    List<Object[]> countLivrosPorGenero();
}
