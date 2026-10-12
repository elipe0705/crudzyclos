package br.com.zyclos.security;

import br.com.zyclos.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Carrega a conta pelo e-mail para o fluxo de autenticacao do Spring Security. */
@Service
public class ServicoDeDetalhesDoUsuario implements UserDetailsService {

    private final UsuarioRepository usuarios;

    public ServicoDeDetalhesDoUsuario(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        return usuarios.findByEmailIgnoreCase(email)
                .map(UsuarioAutenticado::new)
                .orElseThrow(() -> new UsernameNotFoundException("Nenhuma conta com o e-mail " + email));
    }

    @Transactional(readOnly = true)
    public UserDetails carregarPorId(Long id) {
        return usuarios.findById(id)
                .map(UsuarioAutenticado::new)
                .orElseThrow(() -> new UsernameNotFoundException("Conta inexistente: " + id));
    }
}
