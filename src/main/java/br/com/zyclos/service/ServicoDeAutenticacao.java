package br.com.zyclos.service;

import br.com.zyclos.domain.PreferenciasUsuario;
import br.com.zyclos.domain.TokenRecuperacao;
import br.com.zyclos.domain.Usuario;
import br.com.zyclos.dto.request.AlterarSenhaRequest;
import br.com.zyclos.dto.request.CadastroRequest;
import br.com.zyclos.dto.request.LoginRequest;
import br.com.zyclos.dto.request.RecuperarSenhaRequest;
import br.com.zyclos.dto.request.RedefinirSenhaRequest;
import br.com.zyclos.dto.response.TokenResponse;
import br.com.zyclos.exception.ConflitoException;
import br.com.zyclos.exception.RegraDeNegocioException;
import br.com.zyclos.mapper.Conversores;
import br.com.zyclos.repository.TokenRecuperacaoRepository;
import br.com.zyclos.repository.UsuarioRepository;
import br.com.zyclos.security.ServicoDeToken;
import br.com.zyclos.security.UsuarioAutenticado;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Cadastro, login e recuperacao de senha. */
@Service
public class ServicoDeAutenticacao {

    private static final Logger LOG = LoggerFactory.getLogger(ServicoDeAutenticacao.class);
    private static final SecureRandom ALEATORIO = new SecureRandom();
    private static final long HORAS_DE_VALIDADE_DO_CODIGO = 2;

    private final UsuarioRepository usuarios;
    private final TokenRecuperacaoRepository codigos;
    private final PasswordEncoder codificador;
    private final ServicoDeToken tokens;
    private final ServicoDeNotificacao notificacoes;
    private final AuthenticationManager gerenciador;

    public ServicoDeAutenticacao(UsuarioRepository usuarios, TokenRecuperacaoRepository codigos,
                                 PasswordEncoder codificador, ServicoDeToken tokens,
                                 ServicoDeNotificacao notificacoes, AuthenticationManager gerenciador) {
        this.usuarios = usuarios;
        this.codigos = codigos;
        this.codificador = codificador;
        this.tokens = tokens;
        this.notificacoes = notificacoes;
        this.gerenciador = gerenciador;
    }

    @Transactional
    public TokenResponse cadastrar(CadastroRequest pedido) {
        String email = pedido.email().trim().toLowerCase();
        String username = pedido.username().trim().toLowerCase();

        if (usuarios.existsByEmailIgnoreCase(email)) {
            throw new ConflitoException("Ja existe uma conta com este e-mail.");
        }
        if (usuarios.existsByUsernameIgnoreCase(username)) {
            throw new ConflitoException("Este nome de usuario ja esta em uso.");
        }

        Usuario usuario = new Usuario(
                pedido.nome().trim(), username, email, codificador.encode(pedido.senha()));
        usuario.setAvatarUrl(pedido.avatarUrl());
        usuario.setPreferencias(new PreferenciasUsuario(usuario));
        usuarios.save(usuario);

        notificacoes.boasVindas(usuario);
        return emitir(usuario);
    }

    /**
     * Autentica pelo gerenciador do Spring Security.
     *
     * <p>Ele cuida da comparacao do hash e do bloqueio de contas suspensas, lancando
     * BadCredentialsException e DisabledException, que o tratador global converte em 401 e 403.
     */
    @Transactional(readOnly = true)
    public TokenResponse entrar(LoginRequest pedido) {
        Authentication autenticacao = gerenciador.authenticate(
                new UsernamePasswordAuthenticationToken(pedido.email().trim(), pedido.senha()));
        UsuarioAutenticado autenticado = (UsuarioAutenticado) autenticacao.getPrincipal();
        return emitir(autenticado.getUsuario());
    }

    /**
     * Emite um codigo de redefinicao.
     *
     * <p>A resposta e a mesma exista ou nao a conta, para nao revelar quais e-mails estao
     * cadastrados. O codigo vai para o log da aplicacao, no lugar do envio por e-mail.
     */
    @Transactional
    public Optional<String> solicitarRecuperacao(RecuperarSenhaRequest pedido) {
        Optional<Usuario> encontrado = usuarios.findByEmailIgnoreCase(pedido.email().trim());
        if (encontrado.isEmpty()) {
            return Optional.empty();
        }
        Usuario usuario = encontrado.get();
        codigos.invalidarPendentes(usuario, Instant.now());

        byte[] bytes = new byte[24];
        ALEATORIO.nextBytes(bytes);
        String codigo = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        codigos.save(new TokenRecuperacao(usuario, codigo,
                Instant.now().plus(HORAS_DE_VALIDADE_DO_CODIGO, ChronoUnit.HOURS)));

        LOG.info("Codigo de recuperacao gerado para {}: {}", usuario.getEmail(), codigo);
        return Optional.of(codigo);
    }

    @Transactional
    public void redefinirSenha(RedefinirSenhaRequest pedido) {
        TokenRecuperacao codigo = codigos.findByCodigo(pedido.token())
                .orElseThrow(() -> new RegraDeNegocioException("Codigo de recuperacao invalido."));
        if (!codigo.valido()) {
            throw new RegraDeNegocioException("Este codigo expirou ou ja foi usado.");
        }
        Usuario usuario = codigo.getUsuario();
        usuario.setSenhaHash(codificador.encode(pedido.novaSenha()));
        codigo.consumir();
        usuarios.save(usuario);
    }

    @Transactional
    public void alterarSenha(Usuario usuario, AlterarSenhaRequest pedido) {
        if (!codificador.matches(pedido.senhaAtual(), usuario.getSenhaHash())) {
            throw new RegraDeNegocioException("A senha atual nao confere.");
        }
        if (codificador.matches(pedido.novaSenha(), usuario.getSenhaHash())) {
            throw new RegraDeNegocioException("A nova senha deve ser diferente da atual.");
        }
        usuario.setSenhaHash(codificador.encode(pedido.novaSenha()));
        usuarios.save(usuario);
    }

    private TokenResponse emitir(Usuario usuario) {
        ServicoDeToken.Token token = tokens.gerar(usuario);
        return TokenResponse.de(token.valor(), token.expiraEm(), Conversores.conta(usuario));
    }
}
