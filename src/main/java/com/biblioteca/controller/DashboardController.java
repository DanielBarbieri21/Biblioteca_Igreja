package com.biblioteca.controller;

import com.biblioteca.service.EmprestimoService;
import com.biblioteca.service.LivroService;
import com.biblioteca.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class DashboardController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private LivroService livroService;
    
    @Autowired
    private EmprestimoService emprestimoService;
    
    @GetMapping
    public String dashboard(Model model) {
        try {
            // Estatísticas gerais
            long totalUsuarios = usuarioService.contarUsuariosAtivos();
            long totalLivros = livroService.contarLivrosAtivos();
            long livrosDisponiveis = livroService.contarLivrosDisponiveis();
            long emprestimosAtivos = emprestimoService.contarEmprestimosAtivos();
            long emprestimosAtrasados = emprestimoService.contarEmprestimosAtrasados();
            
            model.addAttribute("totalUsuarios", totalUsuarios);
            model.addAttribute("totalLivros", totalLivros);
            model.addAttribute("livrosDisponiveis", livrosDisponiveis);
            model.addAttribute("emprestimosAtivos", emprestimosAtivos);
            model.addAttribute("emprestimosAtrasados", emprestimosAtrasados);
            
            // Empréstimos vencendo nos próximos 7 dias
            try {
                List<Object[]> emprestimosVencendo = emprestimoService.buscarEmprestimosVencendoNosProximosDias(7)
                        .stream()
                        .map(e -> new Object[]{e.getId(), e.getUsuario().getNome(), e.getLivro().getTitulo(), e.getDataPrevistaDevolucao()})
                        .toList();
                model.addAttribute("emprestimosVencendo", emprestimosVencendo);
            } catch (Exception e) {
                model.addAttribute("emprestimosVencendo", java.util.Collections.emptyList());
            }
            
            // Livros mais emprestados
            try {
                List<Object[]> livrosMaisEmprestados = livroService.buscarLivrosMaisEmprestados(
                        org.springframework.data.domain.PageRequest.of(0, 5))
                        .stream()
                        .map(l -> new Object[]{l.getTitulo(), l.getAutor(), l.getEmprestimos() != null ? l.getEmprestimos().size() : 0})
                        .toList();
                model.addAttribute("livrosMaisEmprestados", livrosMaisEmprestados);
            } catch (Exception e) {
                model.addAttribute("livrosMaisEmprestados", java.util.Collections.emptyList());
            }
            
            // Estatísticas por gênero
            try {
                List<Object[]> livrosPorGenero = livroService.contarLivrosPorGenero();
                model.addAttribute("livrosPorGenero", livrosPorGenero);
            } catch (Exception e) {
                model.addAttribute("livrosPorGenero", java.util.Collections.emptyList());
            }
            
            // Empréstimos dos últimos 6 meses
            try {
                List<Object[]> emprestimosPorMes = emprestimoService.buscarEstatisticasEmprestimosPorMes(6);
                model.addAttribute("emprestimosPorMes", emprestimosPorMes);
            } catch (Exception e) {
                model.addAttribute("emprestimosPorMes", java.util.Collections.emptyList());
            }
            
        } catch (Exception e) {
            // Em caso de erro, definir valores padrão
            model.addAttribute("totalUsuarios", 0);
            model.addAttribute("totalLivros", 0);
            model.addAttribute("livrosDisponiveis", 0);
            model.addAttribute("emprestimosAtivos", 0);
            model.addAttribute("emprestimosAtrasados", 0);
            model.addAttribute("emprestimosVencendo", java.util.Collections.emptyList());
            model.addAttribute("livrosMaisEmprestados", java.util.Collections.emptyList());
            model.addAttribute("livrosPorGenero", java.util.Collections.emptyList());
            model.addAttribute("emprestimosPorMes", java.util.Collections.emptyList());
        }
        
        return "dashboard";
    }
    
    @GetMapping("/sobre")
    public String sobre(Model model) {
        try {
            // Estatísticas gerais para a página sobre
            long totalUsuarios = usuarioService.contarUsuariosAtivos();
            long totalLivros = livroService.contarLivrosAtivos();
            long livrosDisponiveis = livroService.contarLivrosDisponiveis();
            long emprestimosAtivos = emprestimoService.contarEmprestimosAtivos();
            long emprestimosAtrasados = emprestimoService.contarEmprestimosAtrasados();
            
            model.addAttribute("totalUsuarios", totalUsuarios);
            model.addAttribute("totalLivros", totalLivros);
            model.addAttribute("livrosDisponiveis", livrosDisponiveis);
            model.addAttribute("emprestimosAtivos", emprestimosAtivos);
            model.addAttribute("emprestimosAtrasados", emprestimosAtrasados);
            
            // Estatísticas adicionais
            model.addAttribute("emprestimosHoje", 0);
            model.addAttribute("totalEmprestimos", 0);
            model.addAttribute("totalMultas", java.math.BigDecimal.ZERO);
            
        } catch (Exception e) {
            // Em caso de erro, definir valores padrão
            model.addAttribute("totalUsuarios", 0);
            model.addAttribute("totalLivros", 0);
            model.addAttribute("livrosDisponiveis", 0);
            model.addAttribute("emprestimosAtivos", 0);
            model.addAttribute("emprestimosAtrasados", 0);
            model.addAttribute("emprestimosHoje", 0);
            model.addAttribute("totalEmprestimos", 0);
            model.addAttribute("totalMultas", java.math.BigDecimal.ZERO);
        }
        
        return "sobre";
    }
}
