package br.com.zyclos.web;

import br.com.zyclos.dto.request.PropostaTrocaRequest;
import br.com.zyclos.dto.response.PropostaTrocaResponse;
import br.com.zyclos.security.UsuarioAutenticado;
import br.com.zyclos.service.ServicoDeTroca;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Propostas de troca, nas abas enviadas, recebidas e historico. */
@RestController
@RequestMapping("/api/trocas")
@Tag(name = "Trocas", description = "Propostas de troca entre pecas")
public class ControladorDeTroca {

    private final ServicoDeTroca trocas;

    public ControladorDeTroca(ServicoDeTroca trocas) {
        this.trocas = trocas;
    }

    @GetMapping("/enviadas")
    @Operation(summary = "Propostas que a conta enviou e ainda estao abertas")
    public List<PropostaTrocaResponse> enviadas(@AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return trocas.enviadas(autenticado.getUsuario());
    }

    @GetMapping("/recebidas")
    @Operation(summary = "Propostas recebidas e ainda abertas")
    public List<PropostaTrocaResponse> recebidas(@AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return trocas.recebidas(autenticado.getUsuario());
    }

    @GetMapping("/historico")
    @Operation(summary = "Propostas ja encerradas das quais a conta participou")
    public List<PropostaTrocaResponse> historico(@AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return trocas.historico(autenticado.getUsuario());
    }

    @PostMapping
    @Operation(summary = "Envia uma proposta de troca")
    public ResponseEntity<PropostaTrocaResponse> propor(@Valid @RequestBody PropostaTrocaRequest pedido,
                                                        @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        PropostaTrocaResponse criada = trocas.propor(autenticado.getUsuario(), pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PatchMapping("/{id}/aceitar")
    @Operation(summary = "Aceita a proposta recebida e reserva as duas pecas")
    public PropostaTrocaResponse aceitar(@PathVariable Long id,
                                         @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return trocas.aceitar(autenticado.getUsuario(), id);
    }

    @PatchMapping("/{id}/recusar")
    @Operation(summary = "Recusa a proposta recebida")
    public PropostaTrocaResponse recusar(@PathVariable Long id,
                                         @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return trocas.recusar(autenticado.getUsuario(), id);
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancela uma proposta que a propria conta enviou")
    public PropostaTrocaResponse cancelar(@PathVariable Long id,
                                          @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return trocas.cancelar(autenticado.getUsuario(), id);
    }

    @PatchMapping("/{id}/concluir")
    @Operation(summary = "Marca a troca como concluida depois da entrega")
    public PropostaTrocaResponse concluir(@PathVariable Long id,
                                          @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return trocas.concluir(autenticado.getUsuario(), id);
    }
}
