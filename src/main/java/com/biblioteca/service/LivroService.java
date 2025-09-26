package com.biblioteca.service;

import com.biblioteca.entity.Livro;
import com.biblioteca.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LivroService {
    
    @Autowired
    private LivroRepository livroRepository;
    
    public Livro salvar(Livro livro) {
        // Verificar se ISBN já existe (se fornecido)
        if (livro.getIsbn() != null && !livro.getIsbn().trim().isEmpty()) {
            if (livro.getId() == null) {
                if (livroRepository.findByIsbn(livro.getIsbn()).isPresent()) {
                    throw new RuntimeException("ISBN já cadastrado: " + livro.getIsbn());
                }
            } else {
                if (livroRepository.existsByIsbnAndIdNot(livro.getIsbn(), livro.getId())) {
                    throw new RuntimeException("ISBN já cadastrado: " + livro.getIsbn());
                }
            }
        }
        
        // Garantir que quantidade disponível não seja maior que total
        if (livro.getQuantidadeDisponivel() > livro.getQuantidadeTotal()) {
            livro.setQuantidadeDisponivel(livro.getQuantidadeTotal());
        }
        
        return livroRepository.save(livro);
    }
    
    @Transactional(readOnly = true)
    public List<Livro> listarTodos() {
        return livroRepository.findByAtivoTrue();
    }
    
    @Transactional(readOnly = true)
    public Page<Livro> listarTodos(Pageable pageable) {
        return livroRepository.findByAtivoTrue(pageable);
    }
    
    @Transactional(readOnly = true)
    public Optional<Livro> buscarPorId(Long id) {
        return livroRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Livro> buscarPorIsbn(String isbn) {
        return livroRepository.findByIsbn(isbn);
    }
    
    @Transactional(readOnly = true)
    public List<Livro> buscarDisponiveis() {
        return livroRepository.findByQuantidadeDisponivelGreaterThanAndAtivoTrue(0);
    }
    
    @Transactional(readOnly = true)
    public Page<Livro> buscarDisponiveis(Pageable pageable) {
        return livroRepository.findByQuantidadeDisponivelGreaterThanAndAtivoTrue(0, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<Livro> buscarPorCriterios(String titulo, String autor, String genero, 
                                        Integer ano, Boolean disponivel, Pageable pageable) {
        // Se não há critérios de busca, retorna todos os livros ativos
        if ((titulo == null || titulo.trim().isEmpty()) && 
            (autor == null || autor.trim().isEmpty()) && 
            (genero == null || genero.trim().isEmpty()) && 
            ano == null && 
            disponivel == null) {
            return livroRepository.findByAtivoTrue(pageable);
        }
        
        // Se há critérios, usa a busca avançada
        return livroRepository.findByCriterios(titulo, autor, genero, ano, disponivel, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Livro> buscarPorTitulo(String titulo) {
        return livroRepository.findByTituloContainingIgnoreCase(titulo);
    }
    
    @Transactional(readOnly = true)
    public List<Livro> buscarPorAutor(String autor) {
        return livroRepository.findByAutorContainingIgnoreCase(autor);
    }
    
    @Transactional(readOnly = true)
    public List<Livro> buscarPorGenero(String genero) {
        return livroRepository.findByGenero(genero);
    }
    
    public void excluir(Long id) {
        Optional<Livro> livroOpt = livroRepository.findById(id);
        if (livroOpt.isPresent()) {
            Livro livro = livroOpt.get();
            // Verificar se tem empréstimos ativos
            if (livro.getQuantidadeDisponivel() < livro.getQuantidadeTotal()) {
                throw new RuntimeException("Não é possível excluir livro com empréstimos ativos");
            }
            // Soft delete
            livro.setAtivo(false);
            livroRepository.save(livro);
        } else {
            throw new RuntimeException("Livro não encontrado com ID: " + id);
        }
    }
    
    public Livro atualizar(Long id, Livro livroAtualizado) {
        Optional<Livro> livroOpt = livroRepository.findById(id);
        if (livroOpt.isPresent()) {
            Livro livro = livroOpt.get();
            livro.setTitulo(livroAtualizado.getTitulo());
            livro.setAutor(livroAtualizado.getAutor());
            livro.setIsbn(livroAtualizado.getIsbn());
            livro.setGenero(livroAtualizado.getGenero());
            livro.setAnoPublicacao(livroAtualizado.getAnoPublicacao());
            livro.setEditora(livroAtualizado.getEditora());
            
            // Atualizar quantidades
            int diferenca = livroAtualizado.getQuantidadeTotal() - livro.getQuantidadeTotal();
            livro.setQuantidadeTotal(livroAtualizado.getQuantidadeTotal());
            livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() + diferenca);
            
            // Garantir que quantidade disponível não seja negativa
            if (livro.getQuantidadeDisponivel() < 0) {
                livro.setQuantidadeDisponivel(0);
            }
            
            return livroRepository.save(livro);
        } else {
            throw new RuntimeException("Livro não encontrado com ID: " + id);
        }
    }
    
    @Transactional(readOnly = true)
    public long contarLivrosDisponiveis() {
        return livroRepository.countLivrosDisponiveis();
    }
    
    @Transactional(readOnly = true)
    public long contarLivrosAtivos() {
        return livroRepository.countLivrosAtivos();
    }
    
    @Transactional(readOnly = true)
    public List<Livro> buscarLivrosMaisEmprestados(Pageable pageable) {
        return livroRepository.findLivrosMaisEmprestados(pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> contarLivrosPorGenero() {
        return livroRepository.countLivrosPorGenero();
    }
    
    public void decrementarDisponivel(Long livroId) {
        Optional<Livro> livroOpt = livroRepository.findById(livroId);
        if (livroOpt.isPresent()) {
            Livro livro = livroOpt.get();
            livro.decrementarDisponivel();
            livroRepository.save(livro);
        }
    }
    
    public void incrementarDisponivel(Long livroId) {
        Optional<Livro> livroOpt = livroRepository.findById(livroId);
        if (livroOpt.isPresent()) {
            Livro livro = livroOpt.get();
            livro.incrementarDisponivel();
            livroRepository.save(livro);
        }
    }
}
