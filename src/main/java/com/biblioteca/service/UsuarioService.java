package com.biblioteca.service;

import com.biblioteca.entity.Usuario;
import com.biblioteca.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    public Usuario salvar(Usuario usuario) {
        // Verificar se email já existe
        if (usuario.getId() == null) {
            if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
                throw new RuntimeException("Email já cadastrado: " + usuario.getEmail());
            }
        } else {
            if (usuarioRepository.existsByEmailAndIdNot(usuario.getEmail(), usuario.getId())) {
                throw new RuntimeException("Email já cadastrado: " + usuario.getEmail());
            }
        }
        
        return usuarioRepository.save(usuario);
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findByAtivoTrue();
    }
    
    @Transactional(readOnly = true)
    public Page<Usuario> listarTodos(Pageable pageable) {
        return usuarioRepository.findByAtivoTrue(pageable);
    }
    
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    @Transactional(readOnly = true)
    public Page<Usuario> buscarPorNome(String nome, Pageable pageable) {
        return usuarioRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome, pageable);
    }
    
    public void excluir(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Verificar se tem empréstimos ativos
            if (usuario.getQuantidadeEmprestimosAtivos() > 0) {
                throw new RuntimeException("Não é possível excluir usuário com empréstimos ativos");
            }
            // Soft delete
            usuario.setAtivo(false);
            usuarioRepository.save(usuario);
        } else {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> buscarUsuariosComEmprestimosAtivos() {
        return usuarioRepository.findUsuariosComEmprestimosAtivos();
    }
    
    @Transactional(readOnly = true)
    public List<Usuario> buscarUsuariosComEmprestimosAtrasados() {
        return usuarioRepository.findUsuariosComEmprestimosAtrasados();
    }
    
    @Transactional(readOnly = true)
    public long contarUsuariosAtivos() {
        return usuarioRepository.countByAtivoTrue();
    }
    
    public Usuario atualizar(Long id, Usuario usuarioAtualizado) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setNome(usuarioAtualizado.getNome());
            usuario.setEmail(usuarioAtualizado.getEmail());
            usuario.setTelefone(usuarioAtualizado.getTelefone());
            usuario.setEndereco(usuarioAtualizado.getEndereco());
            return usuarioRepository.save(usuario);
        } else {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }
    }
}
