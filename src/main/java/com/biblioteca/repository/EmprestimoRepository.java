package com.biblioteca.repository;

import com.biblioteca.entity.Emprestimo;
import com.biblioteca.entity.StatusEmprestimo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    
    // Buscar empréstimos por status
    List<Emprestimo> findByStatus(StatusEmprestimo status);
    
    // Buscar empréstimos por status com paginação
    Page<Emprestimo> findByStatus(StatusEmprestimo status, Pageable pageable);
    
    // Buscar empréstimos por usuário
    List<Emprestimo> findByUsuarioId(Long usuarioId);
    
    // Buscar empréstimos por usuário e status
    List<Emprestimo> findByUsuarioIdAndStatus(Long usuarioId, StatusEmprestimo status);
    
    // Buscar empréstimos por livro
    List<Emprestimo> findByLivroId(Long livroId);
    
    // Buscar empréstimos ativos por usuário
    @Query("SELECT e FROM Emprestimo e WHERE e.usuario.id = :usuarioId AND e.status = 'ATIVO'")
    List<Emprestimo> findEmprestimosAtivosPorUsuario(@Param("usuarioId") Long usuarioId);
    
    // Buscar empréstimos atrasados
    @Query("SELECT e FROM Emprestimo e WHERE e.status = 'ATIVO' AND e.dataPrevistaDevolucao < CURRENT_DATE")
    List<Emprestimo> findEmprestimosAtrasados();
    
    // Buscar empréstimos atrasados com paginação
    @Query("SELECT e FROM Emprestimo e WHERE e.status = 'ATIVO' AND e.dataPrevistaDevolucao < CURRENT_DATE")
    Page<Emprestimo> findEmprestimosAtrasados(Pageable pageable);
    
    // Buscar empréstimos por período
    @Query("SELECT e FROM Emprestimo e WHERE e.dataEmprestimo BETWEEN :dataInicio AND :dataFim")
    List<Emprestimo> findEmprestimosPorPeriodo(@Param("dataInicio") LocalDateTime dataInicio, 
                                             @Param("dataFim") LocalDateTime dataFim);
    
    // Contar empréstimos ativos
    long countByStatus(StatusEmprestimo status);
    
    // Contar empréstimos ativos por usuário
    @Query("SELECT COUNT(e) FROM Emprestimo e WHERE e.usuario.id = :usuarioId AND e.status = 'ATIVO'")
    long countEmprestimosAtivosPorUsuario(@Param("usuarioId") Long usuarioId);
    
    // Buscar empréstimos que vencem em X dias
    @Query("SELECT e FROM Emprestimo e WHERE e.status = 'ATIVO' AND e.dataPrevistaDevolucao = :dataVencimento")
    List<Emprestimo> findEmprestimosVencendoEm(@Param("dataVencimento") LocalDate dataVencimento);
    
    // Buscar empréstimos que vencem nos próximos X dias
    @Query("SELECT e FROM Emprestimo e WHERE e.status = 'ATIVO' AND e.dataPrevistaDevolucao BETWEEN CURRENT_DATE AND :dataLimite")
    List<Emprestimo> findEmprestimosVencendoNosProximosDias(@Param("dataLimite") LocalDate dataLimite);
    
    // Estatísticas de empréstimos por mês
    @Query("SELECT YEAR(e.dataEmprestimo), MONTH(e.dataEmprestimo), COUNT(e) " +
           "FROM Emprestimo e " +
           "WHERE e.dataEmprestimo >= :dataInicio " +
           "GROUP BY YEAR(e.dataEmprestimo), MONTH(e.dataEmprestimo) " +
           "ORDER BY YEAR(e.dataEmprestimo), MONTH(e.dataEmprestimo)")
    List<Object[]> findEstatisticasEmprestimosPorMes(@Param("dataInicio") LocalDateTime dataInicio);
    
    // Buscar empréstimos com multa
    @Query("SELECT e FROM Emprestimo e WHERE e.multa > 0")
    List<Emprestimo> findEmprestimosComMulta();
    
    // Verificar se usuário tem empréstimo ativo do livro
    @Query("SELECT COUNT(e) > 0 FROM Emprestimo e WHERE e.usuario.id = :usuarioId AND e.livro.id = :livroId AND e.status = 'ATIVO'")
    boolean existsEmprestimoAtivoPorUsuarioELivro(@Param("usuarioId") Long usuarioId, @Param("livroId") Long livroId);
    
    // Buscar empréstimos recentes
    @Query("SELECT e FROM Emprestimo e ORDER BY e.dataEmprestimo DESC")
    Page<Emprestimo> findEmprestimosRecentes(Pageable pageable);
}
