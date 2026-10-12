package br.com.zyclos.security;

import br.com.zyclos.config.PropriedadesZyclos;
import br.com.zyclos.domain.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Gera e valida os tokens JWT usados pela API. */
@Service
public class ServicoDeToken {

    private static final Logger LOG = LoggerFactory.getLogger(ServicoDeToken.class);

    private final SecretKey chave;
    private final Duration validade;
    private final String emissor;

    public ServicoDeToken(PropriedadesZyclos propriedades) {
        this.chave = construirChave(propriedades.getJwt().getSegredo());
        this.validade = Duration.ofMinutes(propriedades.getJwt().getMinutosDeValidade());
        this.emissor = propriedades.getJwt().getEmissor();
    }

    /** Emite um token com o id do usuario no assunto e o papel como claim. */
    public Token gerar(Usuario usuario) {
        Instant agora = Instant.now();
        Instant expiracao = agora.plus(validade);
        String token = Jwts.builder()
                .issuer(emissor)
                .subject(String.valueOf(usuario.getId()))
                .claim("email", usuario.getEmail())
                .claim("username", usuario.getUsername())
                .claim("papel", usuario.getPapel().name())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(expiracao))
                .signWith(chave)
                .compact();
        return new Token(token, expiracao);
    }

    /** Devolve o id do usuario quando o token e valido, ou vazio quando nao e. */
    public Optional<Long> idDoUsuario(String token) {
        try {
            Claims corpo = Jwts.parser()
                    .verifyWith(chave)
                    .requireIssuer(emissor)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(Long.valueOf(corpo.getSubject()));
        } catch (JwtException | IllegalArgumentException erro) {
            LOG.debug("Token rejeitado: {}", erro.getMessage());
            return Optional.empty();
        }
    }

    private static SecretKey construirChave(String segredo) {
        if (segredo == null || segredo.isBlank()) {
            LOG.warn("Nenhum segredo JWT configurado. Gerando uma chave temporaria: "
                    + "os tokens deixarao de valer a cada reinicio. Defina ZYCLOS_JWT_SEGREDO em producao.");
            return Jwts.SIG.HS256.key().build();
        }
        byte[] bytes = tentarBase64(segredo);
        if (bytes.length < 32) {
            throw new IllegalStateException(
                    "O segredo JWT precisa ter ao menos 32 bytes (256 bits). Gere um novo valor.");
        }
        return Keys.hmacShaKeyFor(bytes);
    }

    /** Aceita o segredo em Base64 ou como texto simples, o que facilita a configuracao local. */
    private static byte[] tentarBase64(String segredo) {
        try {
            return Decoders.BASE64.decode(segredo);
        } catch (DecodingException | IllegalArgumentException naoEhBase64) {
            return segredo.getBytes(StandardCharsets.UTF_8);
        }
    }

    /** Token emitido junto do instante em que deixa de valer. */
    public record Token(String valor, Instant expiraEm) {
    }
}
