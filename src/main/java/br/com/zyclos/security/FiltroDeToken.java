package br.com.zyclos.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Le o cabecalho Authorization e autentica a requisicao quando o token e valido. */
@Component
public class FiltroDeToken extends OncePerRequestFilter {

    private static final String PREFIXO = "Bearer ";

    private final ServicoDeToken tokens;
    private final ServicoDeDetalhesDoUsuario detalhes;

    public FiltroDeToken(ServicoDeToken tokens, ServicoDeDetalhesDoUsuario detalhes) {
        this.tokens = tokens;
        this.detalhes = detalhes;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest requisicao,
                                    @NonNull HttpServletResponse resposta,
                                    @NonNull FilterChain cadeia) throws ServletException, IOException {
        String cabecalho = requisicao.getHeader(HttpHeaders.AUTHORIZATION);
        if (cabecalho != null && cabecalho.startsWith(PREFIXO)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = cabecalho.substring(PREFIXO.length()).trim();
            tokens.idDoUsuario(token).ifPresent(id -> autenticar(id, requisicao));
        }
        cadeia.doFilter(requisicao, resposta);
    }

    private void autenticar(Long id, HttpServletRequest requisicao) {
        try {
            UserDetails usuario = detalhes.carregarPorId(id);
            if (!usuario.isEnabled()) {
                return;
            }
            var autenticacao = new UsernamePasswordAuthenticationToken(
                    usuario, null, usuario.getAuthorities());
            autenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(requisicao));
            SecurityContextHolder.getContext().setAuthentication(autenticacao);
        } catch (UsernameNotFoundException contaRemovida) {
            SecurityContextHolder.clearContext();
        }
    }
}
