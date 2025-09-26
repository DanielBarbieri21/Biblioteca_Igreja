package com.biblioteca.controller;

import com.biblioteca.entity.Usuario;
import com.biblioteca.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    // Página principal de usuários
    @GetMapping
    public String listarUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String nome,
            Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Usuario> usuarios;
        if (nome != null && !nome.trim().isEmpty()) {
            usuarios = usuarioService.buscarPorNome(nome, pageable);
        } else {
            usuarios = usuarioService.listarTodos(pageable);
        }
        
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usuarios.getTotalPages());
        model.addAttribute("totalItems", usuarios.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("nome", nome);
        
        return "usuarios/listar";
    }
    
    // Formulário para novo usuário
    @GetMapping("/novo")
    public String novoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/form";
    }
    
    // Formulário para editar usuário
    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(id);
        if (usuarioOpt.isPresent()) {
            model.addAttribute("usuario", usuarioOpt.get());
            return "usuarios/form";
        } else {
            return "redirect:/usuarios?error=usuario_nao_encontrado";
        }
    }
    
    // Salvar usuário
    @PostMapping("/salvar")
    public String salvarUsuario(@Valid @ModelAttribute Usuario usuario, 
                               BindingResult result, 
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "usuarios/form";
        }
        
        try {
            usuarioService.salvar(usuario);
            redirectAttributes.addFlashAttribute("success", 
                usuario.getId() == null ? "Usuário cadastrado com sucesso!" : "Usuário atualizado com sucesso!");
            return "redirect:/usuarios";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios/novo";
        }
    }
    
    // Excluir usuário
    @PostMapping("/excluir/{id}")
    public String excluirUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.excluir(id);
            redirectAttributes.addFlashAttribute("success", "Usuário excluído com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }
    
    // Detalhes do usuário
    @GetMapping("/detalhes/{id}")
    public String detalhesUsuario(@PathVariable Long id, Model model) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(id);
        if (usuarioOpt.isPresent()) {
            model.addAttribute("usuario", usuarioOpt.get());
            return "usuarios/detalhes";
        } else {
            return "redirect:/usuarios?error=usuario_nao_encontrado";
        }
    }
    
    // API REST - Listar usuários
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Page<Usuario>> listarUsuariosApi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String nome) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Usuario> usuarios;
        if (nome != null && !nome.trim().isEmpty()) {
            usuarios = usuarioService.buscarPorNome(nome, pageable);
        } else {
            usuarios = usuarioService.listarTodos(pageable);
        }
        
        return ResponseEntity.ok(usuarios);
    }
    
    // API REST - Buscar usuário por ID
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Usuario> buscarUsuarioApi(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(id);
        return usuarioOpt.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }
    
    // API REST - Criar usuário
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> criarUsuarioApi(@Valid @RequestBody Usuario usuario) {
        try {
            Usuario usuarioSalvo = usuarioService.salvar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // API REST - Atualizar usuário
    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> atualizarUsuarioApi(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        try {
            Usuario usuarioAtualizado = usuarioService.atualizar(id, usuario);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // API REST - Excluir usuário
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> excluirUsuarioApi(@PathVariable Long id) {
        try {
            usuarioService.excluir(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
