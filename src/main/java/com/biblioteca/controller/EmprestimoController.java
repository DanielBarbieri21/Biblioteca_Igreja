package com.biblioteca.controller;

import com.biblioteca.entity.Emprestimo;
import com.biblioteca.service.EmprestimoService;
import com.biblioteca.service.LivroService;
import com.biblioteca.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/emprestimos")
public class EmprestimoController {
    
    @Autowired
    private EmprestimoService emprestimoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private LivroService livroService;
    
    // Página principal de empréstimos
    @GetMapping
    public String listarEmprestimos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataEmprestimo") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status,
            Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Emprestimo> emprestimos;
        if (status != null && !status.trim().isEmpty()) {
            emprestimos = emprestimoService.buscarEmprestimosAtivos(pageable);
        } else {
            emprestimos = emprestimoService.listarTodos(pageable);
        }
        
        model.addAttribute("emprestimos", emprestimos);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", emprestimos.getTotalPages());
        model.addAttribute("totalItems", emprestimos.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("status", status);
        
        return "emprestimos/listar";
    }
    
    // Formulário para novo empréstimo
    @GetMapping("/novo")
    public String novoEmprestimo(Model model) {
        model.addAttribute("emprestimo", new Emprestimo());
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("livros", livroService.buscarDisponiveis());
        return "emprestimos/form";
    }
    
    // Realizar empréstimo
    @PostMapping("/realizar")
    public String realizarEmprestimo(
            @RequestParam Long usuarioId,
            @RequestParam Long livroId,
            @RequestParam(required = false) String observacoes,
            RedirectAttributes redirectAttributes) {
        
        try {
            Emprestimo emprestimo = emprestimoService.realizarEmprestimo(usuarioId, livroId, observacoes);
            redirectAttributes.addFlashAttribute("success", 
                "Empréstimo realizado com sucesso! ID: " + emprestimo.getId());
            return "redirect:/emprestimos";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/emprestimos/novo";
        }
    }
    
    // Devolver livro
    @PostMapping("/devolver/{id}")
    public String devolverLivro(
            @PathVariable Long id,
            @RequestParam(required = false) String observacoes,
            RedirectAttributes redirectAttributes) {
        
        try {
            Emprestimo emprestimo = emprestimoService.devolverLivro(id, observacoes);
            String mensagem = "Livro devolvido com sucesso!";
            if (emprestimo.getMulta().compareTo(java.math.BigDecimal.ZERO) > 0) {
                mensagem += " Multa aplicada: R$ " + emprestimo.getMulta();
            }
            redirectAttributes.addFlashAttribute("success", mensagem);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/emprestimos";
    }
    
    // Renovar empréstimo
    @PostMapping("/renovar/{id}")
    public String renovarEmprestimo(
            @PathVariable Long id,
            @RequestParam(required = false) String observacoes,
            RedirectAttributes redirectAttributes) {
        
        try {
            emprestimoService.renovarEmprestimo(id, observacoes);
            redirectAttributes.addFlashAttribute("success", "Empréstimo renovado com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/emprestimos";
    }
    
    // Detalhes do empréstimo
    @GetMapping("/detalhes/{id}")
    public String detalhesEmprestimo(@PathVariable Long id, Model model) {
        Optional<Emprestimo> emprestimoOpt = emprestimoService.buscarPorId(id);
        if (emprestimoOpt.isPresent()) {
            model.addAttribute("emprestimo", emprestimoOpt.get());
            return "emprestimos/detalhes";
        } else {
            return "redirect:/emprestimos?error=emprestimo_nao_encontrado";
        }
    }
    
    // Empréstimos ativos
    @GetMapping("/ativos")
    public String listarEmprestimosAtivos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("dataPrevistaDevolucao").ascending());
        Page<Emprestimo> emprestimos = emprestimoService.buscarEmprestimosAtivos(pageable);
        
        model.addAttribute("emprestimos", emprestimos);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", emprestimos.getTotalPages());
        model.addAttribute("totalItems", emprestimos.getTotalElements());
        
        return "emprestimos/ativos";
    }
    
    // Empréstimos atrasados
    @GetMapping("/atrasados")
    public String listarEmprestimosAtrasados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("dataPrevistaDevolucao").ascending());
        Page<Emprestimo> emprestimos = emprestimoService.buscarEmprestimosAtrasados(pageable);
        
        model.addAttribute("emprestimos", emprestimos);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", emprestimos.getTotalPages());
        model.addAttribute("totalItems", emprestimos.getTotalElements());
        
        return "emprestimos/atrasados";
    }
    
    // Empréstimos vencendo em breve
    @GetMapping("/vencendo")
    public String listarEmprestimosVencendo(
            @RequestParam(defaultValue = "7") int dias,
            Model model) {
        
        List<Emprestimo> emprestimos = emprestimoService.buscarEmprestimosVencendoNosProximosDias(dias);
        model.addAttribute("emprestimos", emprestimos);
        model.addAttribute("dias", dias);
        
        return "emprestimos/vencendo";
    }
    
    // API REST - Listar empréstimos
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Page<Emprestimo>> listarEmprestimosApi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataEmprestimo") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Emprestimo> emprestimos = emprestimoService.listarTodos(pageable);
        return ResponseEntity.ok(emprestimos);
    }
    
    // API REST - Buscar empréstimo por ID
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Emprestimo> buscarEmprestimoApi(@PathVariable Long id) {
        Optional<Emprestimo> emprestimoOpt = emprestimoService.buscarPorId(id);
        return emprestimoOpt.map(ResponseEntity::ok)
                           .orElse(ResponseEntity.notFound().build());
    }
    
    // API REST - Realizar empréstimo
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> realizarEmprestimoApi(
            @RequestParam Long usuarioId,
            @RequestParam Long livroId,
            @RequestParam(required = false) String observacoes) {
        
        try {
            Emprestimo emprestimo = emprestimoService.realizarEmprestimo(usuarioId, livroId, observacoes);
            return ResponseEntity.status(201).body(emprestimo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // API REST - Devolver livro
    @PostMapping("/api/{id}/devolver")
    @ResponseBody
    public ResponseEntity<?> devolverLivroApi(
            @PathVariable Long id,
            @RequestParam(required = false) String observacoes) {
        
        try {
            Emprestimo emprestimo = emprestimoService.devolverLivro(id, observacoes);
            return ResponseEntity.ok(emprestimo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // API REST - Renovar empréstimo
    @PostMapping("/api/{id}/renovar")
    @ResponseBody
    public ResponseEntity<?> renovarEmprestimoApi(
            @PathVariable Long id,
            @RequestParam(required = false) String observacoes) {
        
        try {
            Emprestimo emprestimo = emprestimoService.renovarEmprestimo(id, observacoes);
            return ResponseEntity.ok(emprestimo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
