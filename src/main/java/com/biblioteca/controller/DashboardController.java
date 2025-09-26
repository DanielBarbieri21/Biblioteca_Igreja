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
        List<Object[]> emprestimosVencendo = emprestimoService.buscarEmprestimosVencendoNosProximosDias(7)
                .stream()
                .map(e -> new Object[]{e.getId(), e.getUsuario().getNome(), e.getLivro().getTitulo(), e.getDataPrevistaDevolucao()})
                .toList();
        model.addAttribute("emprestimosVencendo", emprestimosVencendo);
        
        // Livros mais emprestados
        List<Object[]> livrosMaisEmprestados = livroService.buscarLivrosMaisEmprestados(
                org.springframework.data.domain.PageRequest.of(0, 5))
                .stream()
                .map(l -> new Object[]{l.getTitulo(), l.getAutor(), l.getEmprestimos().size()})
                .toList();
        model.addAttribute("livrosMaisEmprestados", livrosMaisEmprestados);
        
        // Estatísticas por gênero
        List<Object[]> livrosPorGenero = livroService.contarLivrosPorGenero();
        model.addAttribute("livrosPorGenero", livrosPorGenero);
        
        // Empréstimos dos últimos 6 meses
        List<Object[]> emprestimosPorMes = emprestimoService.buscarEstatisticasEmprestimosPorMes(6);
        model.addAttribute("emprestimosPorMes", emprestimosPorMes);
        
        return "dashboard";
    }
    
    @GetMapping("/sobre")
    public String sobre() {
        return "sobre";
    }
}
