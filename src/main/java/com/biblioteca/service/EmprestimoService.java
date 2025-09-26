package com.biblioteca.service;

import com.biblioteca.entity.Emprestimo;
import com.biblioteca.entity.Livro;
import com.biblioteca.entity.StatusEmprestimo;
import com.biblioteca.entity.Usuario;
import com.biblioteca.repository.EmprestimoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmprestimoService {
    
    @Autowired
    private EmprestimoRepository emprestimoRepository;
    
    @Autowired
    private LivroService livroService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    private static final int DIAS_EMPRESTIMO = 15; // 15 dias para devolução
    private static final BigDecimal VALOR_MULTA_DIA = new BigDecimal("2.00"); // R$ 2,00 por dia de atraso
    
    public Emprestimo realizarEmprestimo(Long usuarioId, Long livroId, String observacoes) {
        // Verificar se usuário existe
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(usuarioId);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado com ID: " + usuarioId);
        }
        
        // Verificar se livro existe
        Optional<Livro> livroOpt = livroService.buscarPorId(livroId);
        if (livroOpt.isEmpty()) {
            throw new RuntimeException("Livro não encontrado com ID: " + livroId);
        }
        
        Usuario usuario = usuarioOpt.get();
        Livro livro = livroOpt.get();
        
        // Verificar se livro está disponível
        if (!livro.isDisponivel()) {
            throw new RuntimeException("Livro não está disponível para empréstimo");
        }
        
        // Verificar se usuário já tem empréstimo ativo deste livro
        if (emprestimoRepository.existsEmprestimoAtivoPorUsuarioELivro(usuarioId, livroId)) {
            throw new RuntimeException("Usuário já possui empréstimo ativo deste livro");
        }
        
        // Verificar limite de empréstimos por usuário (opcional - pode ser configurável)
        long emprestimosAtivos = emprestimoRepository.countEmprestimosAtivosPorUsuario(usuarioId);
        if (emprestimosAtivos >= 3) { // Limite de 3 livros por usuário
            throw new RuntimeException("Usuário atingiu o limite máximo de empréstimos ativos (3 livros)");
        }
        
        // Criar empréstimo
        LocalDate dataPrevistaDevolucao = LocalDate.now().plusDays(DIAS_EMPRESTIMO);
        Emprestimo emprestimo = new Emprestimo(usuario, livro, dataPrevistaDevolucao);
        emprestimo.setObservacoes(observacoes);
        
        // Salvar empréstimo
        emprestimo = emprestimoRepository.save(emprestimo);
        
        // Decrementar quantidade disponível do livro
        livroService.decrementarDisponivel(livroId);
        
        return emprestimo;
    }
    
    public Emprestimo devolverLivro(Long emprestimoId, String observacoes) {
        Optional<Emprestimo> emprestimoOpt = emprestimoRepository.findById(emprestimoId);
        if (emprestimoOpt.isEmpty()) {
            throw new RuntimeException("Empréstimo não encontrado com ID: " + emprestimoId);
        }
        
        Emprestimo emprestimo = emprestimoOpt.get();
        
        if (emprestimo.getStatus() != StatusEmprestimo.ATIVO) {
            throw new RuntimeException("Empréstimo já foi devolvido");
        }
        
        // Calcular multa se houver atraso
        BigDecimal multa = calcularMulta(emprestimo);
        emprestimo.setMulta(multa);
        
        // Devolver livro
        emprestimo.devolver();
        emprestimo.setObservacoes(observacoes);
        
        // Incrementar quantidade disponível do livro
        livroService.incrementarDisponivel(emprestimo.getLivro().getId());
        
        return emprestimoRepository.save(emprestimo);
    }
    
    @Transactional(readOnly = true)
    public List<Emprestimo> listarTodos() {
        return emprestimoRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Page<Emprestimo> listarTodos(Pageable pageable) {
        return emprestimoRepository.findEmprestimosRecentes(pageable);
    }
    
    @Transactional(readOnly = true)
    public Optional<Emprestimo> buscarPorId(Long id) {
        return emprestimoRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Emprestimo> buscarPorUsuario(Long usuarioId) {
        return emprestimoRepository.findByUsuarioId(usuarioId);
    }
    
    @Transactional(readOnly = true)
    public List<Emprestimo> buscarEmprestimosAtivos() {
        return emprestimoRepository.findByStatus(StatusEmprestimo.ATIVO);
    }
    
    @Transactional(readOnly = true)
    public Page<Emprestimo> buscarEmprestimosAtivos(Pageable pageable) {
        return emprestimoRepository.findByStatus(StatusEmprestimo.ATIVO, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Emprestimo> buscarEmprestimosAtrasados() {
        return emprestimoRepository.findEmprestimosAtrasados();
    }
    
    @Transactional(readOnly = true)
    public Page<Emprestimo> buscarEmprestimosAtrasados(Pageable pageable) {
        return emprestimoRepository.findEmprestimosAtrasados(pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Emprestimo> buscarEmprestimosVencendoEm(LocalDate data) {
        return emprestimoRepository.findEmprestimosVencendoEm(data);
    }
    
    @Transactional(readOnly = true)
    public List<Emprestimo> buscarEmprestimosVencendoNosProximosDias(int dias) {
        LocalDate dataLimite = LocalDate.now().plusDays(dias);
        return emprestimoRepository.findEmprestimosVencendoNosProximosDias(dataLimite);
    }
    
    @Transactional(readOnly = true)
    public List<Emprestimo> buscarEmprestimosComMulta() {
        return emprestimoRepository.findEmprestimosComMulta();
    }
    
    public void atualizarStatusEmprestimosAtrasados() {
        List<Emprestimo> emprestimosAtrasados = emprestimoRepository.findEmprestimosAtrasados();
        for (Emprestimo emprestimo : emprestimosAtrasados) {
            emprestimo.marcarComoAtrasado();
            emprestimoRepository.save(emprestimo);
        }
    }
    
    public BigDecimal calcularMulta(Emprestimo emprestimo) {
        if (emprestimo.getStatus() == StatusEmprestimo.ATIVO && emprestimo.isAtrasado()) {
            long diasAtraso = emprestimo.getDiasAtraso();
            return VALOR_MULTA_DIA.multiply(BigDecimal.valueOf(diasAtraso));
        }
        return BigDecimal.ZERO;
    }
    
    @Transactional(readOnly = true)
    public long contarEmprestimosAtivos() {
        return emprestimoRepository.countByStatus(StatusEmprestimo.ATIVO);
    }
    
    @Transactional(readOnly = true)
    public long contarEmprestimosAtrasados() {
        return emprestimoRepository.findEmprestimosAtrasados().size();
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> buscarEstatisticasEmprestimosPorMes(int meses) {
        LocalDateTime dataInicio = LocalDateTime.now().minusMonths(meses);
        return emprestimoRepository.findEstatisticasEmprestimosPorMes(dataInicio);
    }
    
    public Emprestimo renovarEmprestimo(Long emprestimoId, String observacoes) {
        Optional<Emprestimo> emprestimoOpt = emprestimoRepository.findById(emprestimoId);
        if (emprestimoOpt.isEmpty()) {
            throw new RuntimeException("Empréstimo não encontrado com ID: " + emprestimoId);
        }
        
        Emprestimo emprestimo = emprestimoOpt.get();
        
        if (emprestimo.getStatus() != StatusEmprestimo.ATIVO) {
            throw new RuntimeException("Apenas empréstimos ativos podem ser renovados");
        }
        
        // Verificar se não está muito atrasado (mais de 30 dias)
        if (emprestimo.getDiasAtraso() > 30) {
            throw new RuntimeException("Empréstimo muito atrasado para renovação");
        }
        
        // Renovar por mais 15 dias
        LocalDate novaDataPrevista = emprestimo.getDataPrevistaDevolucao().plusDays(DIAS_EMPRESTIMO);
        emprestimo.setDataPrevistaDevolucao(novaDataPrevista);
        
        // Adicionar observação sobre renovação
        String observacaoRenovacao = "Renovado em " + LocalDateTime.now().toLocalDate();
        if (observacoes != null && !observacoes.trim().isEmpty()) {
            observacaoRenovacao += " - " + observacoes;
        }
        emprestimo.setObservacoes(observacaoRenovacao);
        
        return emprestimoRepository.save(emprestimo);
    }
}
