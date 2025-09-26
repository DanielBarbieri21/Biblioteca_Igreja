package com.biblioteca.controller;

import com.biblioteca.entity.Livro;
import com.biblioteca.service.LivroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/livros")
public class LivroController {
    
    @Autowired
    private LivroService livroService;
    
    // Página principal de livros
    @GetMapping
    public String listarLivros(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "titulo") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) String genero,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Boolean disponivel,
            Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Livro> livros = livroService.buscarPorCriterios(titulo, autor, genero, ano, disponivel, pageable);
        
        model.addAttribute("livros", livros);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", livros.getTotalPages());
        model.addAttribute("totalItems", livros.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("titulo", titulo);
        model.addAttribute("autor", autor);
        model.addAttribute("genero", genero);
        model.addAttribute("ano", ano);
        model.addAttribute("disponivel", disponivel);
        
        // Lista de gêneros para o filtro
        List<Object[]> generos = livroService.contarLivrosPorGenero();
        model.addAttribute("generos", generos);
        
        return "livros/listar";
    }
    
    // Formulário para novo livro
    @GetMapping("/novo")
    public String novoLivro(Model model) {
        model.addAttribute("livro", new Livro());
        return "livros/form";
    }
    
    // Formulário para editar livro
    @GetMapping("/editar/{id}")
    public String editarLivro(@PathVariable Long id, Model model) {
        Optional<Livro> livroOpt = livroService.buscarPorId(id);
        if (livroOpt.isPresent()) {
            model.addAttribute("livro", livroOpt.get());
            return "livros/form";
        } else {
            return "redirect:/livros?error=livro_nao_encontrado";
        }
    }
    
    // Salvar livro
    @PostMapping("/salvar")
    public String salvarLivro(@Valid @ModelAttribute Livro livro, 
                             BindingResult result, 
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "livros/form";
        }
        
        try {
            livroService.salvar(livro);
            redirectAttributes.addFlashAttribute("success", 
                livro.getId() == null ? "Livro cadastrado com sucesso!" : "Livro atualizado com sucesso!");
            return "redirect:/livros";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return livro.getId() == null ? "redirect:/livros/novo" : "redirect:/livros/editar/" + livro.getId();
        }
    }
    
    // Excluir livro
    @PostMapping("/excluir/{id}")
    public String excluirLivro(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            livroService.excluir(id);
            redirectAttributes.addFlashAttribute("success", "Livro excluído com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/livros";
    }
    
    // Detalhes do livro
    @GetMapping("/detalhes/{id}")
    public String detalhesLivro(@PathVariable Long id, Model model) {
        Optional<Livro> livroOpt = livroService.buscarPorId(id);
        if (livroOpt.isPresent()) {
            model.addAttribute("livro", livroOpt.get());
            return "livros/detalhes";
        } else {
            return "redirect:/livros?error=livro_nao_encontrado";
        }
    }
    
    // Buscar livros disponíveis
    @GetMapping("/disponiveis")
    public String listarLivrosDisponiveis(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("titulo").ascending());
        Page<Livro> livros = livroService.buscarDisponiveis(pageable);
        
        model.addAttribute("livros", livros);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", livros.getTotalPages());
        model.addAttribute("totalItems", livros.getTotalElements());
        
        return "livros/disponiveis";
    }
    
    // API REST - Listar livros
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Page<Livro>> listarLivrosApi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "titulo") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) String genero,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Boolean disponivel) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Livro> livros = livroService.buscarPorCriterios(titulo, autor, genero, ano, disponivel, pageable);
        return ResponseEntity.ok(livros);
    }
    
    // API REST - Buscar livro por ID
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Livro> buscarLivroApi(@PathVariable Long id) {
        Optional<Livro> livroOpt = livroService.buscarPorId(id);
        return livroOpt.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    // API REST - Criar livro
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> criarLivroApi(@Valid @RequestBody Livro livro) {
        try {
            Livro livroSalvo = livroService.salvar(livro);
            return ResponseEntity.status(201).body(livroSalvo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // API REST - Atualizar livro
    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> atualizarLivroApi(@PathVariable Long id, @Valid @RequestBody Livro livro) {
        try {
            Livro livroAtualizado = livroService.atualizar(id, livro);
            return ResponseEntity.ok(livroAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // API REST - Excluir livro
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> excluirLivroApi(@PathVariable Long id) {
        try {
            livroService.excluir(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // API REST - Buscar livros disponíveis
    @GetMapping("/api/disponiveis")
    @ResponseBody
    public ResponseEntity<Page<Livro>> buscarLivrosDisponiveisApi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("titulo").ascending());
        Page<Livro> livros = livroService.buscarDisponiveis(pageable);
        return ResponseEntity.ok(livros);
    }
}
