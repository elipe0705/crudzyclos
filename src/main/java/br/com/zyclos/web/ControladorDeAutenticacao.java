package br.com.zyclos.web;

import br.com.zyclos.dto.request.AlterarSenhaRequest;
import br.com.zyclos.dto.request.CadastroRequest;
import br.com.zyclos.dto.request.LoginRequest;
import br.com.zyclos.dto.request.RecuperarSenhaRequest;
import br.com.zyclos.dto.request.RedefinirSenhaRequest;
import br.com.zyclos.dto.response.MensagemSimplesResponse;
import br.com.zyclos.dto.response.TokenResponse;
import br.com.zyclos.dto.response.UsuarioResponse;
import br.com.zyclos.mapper.Conversores;
import br.com.zyclos.security.UsuarioAutenticado;
import br.com.zyclos.service.ServicoDeAutenticacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Cadastro, login e recuperacao de senha. */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticacao", description = "Cadastro, login e recuperacao de senha")
public class ControladorDeAutenticacao {

    private final ServicoDeAutenticacao autenticacao;
    private final Environment ambiente;

    public ControladorDeAutenticacao(ServicoDeAutenticacao autenticacao, Environment ambiente) {
        this.autenticacao = autenticacao;
        this.ambiente = ambiente;
    }

    @PostMapping("/cadastro")
    @SecurityRequirements
    @Operation(summary = "Cria uma conta e ja devolve o token de acesso")
    public ResponseEntity<TokenResponse> cadastrar(@Valid @RequestBody CadastroRequest pedido) {
        return ResponseEntity.status(HttpStatus.CREATED).body(autenticacao.cadastrar(pedido));
    }

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(summary = "Autentica com e-mail e senha")
    public TokenResponse entrar(@Valid @RequestBody LoginRequest pedido) {
        return autenticacao.entrar(pedido);
    }

    @GetMapping("/eu")
    @Operation(summary = "Devolve a conta do token informado")
    public UsuarioResponse eu(@AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return Conversores.conta(autenticado.getUsuario());
    }

    /**
     * Gera o codigo de redefinicao.
     *
     * <p>A resposta nao revela se o e-mail existe. Fora de producao o codigo tambem vem no corpo,
     * para que o fluxo possa ser testado sem servidor de e-mail.
     */
    @PostMapping("/recuperar-senha")
    @SecurityRequirements
    @Operation(summary = "Solicita o codigo de redefinicao de senha")
    public Map<String, Object> recuperar(@Valid @RequestBody RecuperarSenhaRequest pedido) {
        var codigo = autenticacao.solicitarRecuperacao(pedido);
        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("mensagem", "Se o e-mail estiver cadastrado, as instrucoes foram enviadas.");
        if (codigo.isPresent() && !ambiente.matchesProfiles("prod")) {
            resposta.put("codigo", codigo.get());
        }
        return resposta;
    }

    @PostMapping("/redefinir-senha")
    @SecurityRequirements
    @Operation(summary = "Troca a senha usando o codigo recebido")
    public MensagemSimplesResponse redefinir(@Valid @RequestBody RedefinirSenhaRequest pedido) {
        autenticacao.redefinirSenha(pedido);
        return new MensagemSimplesResponse("Senha redefinida com sucesso.");
    }

    @PostMapping("/alterar-senha")
    @Operation(summary = "Troca a senha da conta autenticada")
    public MensagemSimplesResponse alterar(@AuthenticationPrincipal UsuarioAutenticado autenticado,
                                           @Valid @RequestBody AlterarSenhaRequest pedido) {
        autenticacao.alterarSenha(autenticado.getUsuario(), pedido);
        return new MensagemSimplesResponse("Senha alterada com sucesso.");
    }
}
