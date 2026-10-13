package br.com.zyclos.model.services;

import br.com.zyclos.model.entity.Usuario;
import br.com.zyclos.model.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired // Injeção de dependência automática
    private UsuarioRepository usuarioRepository;

    // Método responsável em listar todos os usuários cadastrados no banco de dados

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    // Método responsável em criar o usuário no banco de dados

    public Usuario save(Usuario usuario) {
        if (usuario.getTipoUsuario() == null) {
            usuario.setTipoUsuario("USUARIO");
        }
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setCodStatus(true);
        return usuarioRepository.save(usuario);
    }

    // Método responsável em listar o usuário por ID

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o id " + id));
    }

    // Método responsável em atualizar o usuário

    public Usuario update(Long id, Usuario usuario) {
        Usuario usuarioExistente = findById(id);
        usuarioExistente.setNome(usuario.getNome());
        usuarioExistente.setUsername(usuario.getUsername());
        usuarioExistente.setEmail(usuario.getEmail());
        if (usuario.getSenha() != null) {
            usuarioExistente.setSenha(usuario.getSenha());
        }
        usuarioExistente.setAvatarUrl(usuario.getAvatarUrl());
        usuarioExistente.setBio(usuario.getBio());
        usuarioExistente.setCidade(usuario.getCidade());
        usuarioExistente.setEstado(usuario.getEstado());
        usuarioExistente.setTipoUsuario(usuario.getTipoUsuario());
        usuarioExistente.setVerificado(usuario.isVerificado());
        return usuarioRepository.save(usuarioExistente);
    }

    // Método responsável em excluir o usuário ( exclusão física )

    public void delete(Long id) {
        Usuario usuarioExistente = findById(id);
        usuarioRepository.delete(usuarioExistente);
    }
}
