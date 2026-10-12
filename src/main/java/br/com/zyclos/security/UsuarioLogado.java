package br.com.zyclos.security;

import br.com.zyclos.domain.Usuario;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** Atalho para recuperar a conta autenticada no contexto da requisicao. */
public final class UsuarioLogado {

    private UsuarioLogado() {
    }

    /** Conta autenticada, ou vazio quando a requisicao e anonima. */
    public static Optional<Usuario> atual() {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            return Optional.empty();
        }
        return autenticacao.getPrincipal() instanceof UsuarioAutenticado autenticado
                ? Optional.of(autenticado.getUsuario())
                : Optional.empty();
    }
}
