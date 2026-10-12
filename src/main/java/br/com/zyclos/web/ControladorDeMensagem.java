package br.com.zyclos.web;

import br.com.zyclos.dto.request.MensagemRequest;
import br.com.zyclos.dto.request.NovaConversaRequest;
import br.com.zyclos.dto.response.ConversaResponse;
import br.com.zyclos.dto.response.MensagemResponse;
import br.com.zyclos.security.UsuarioAutenticado;
import br.com.zyclos.service.ServicoDeMensagem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
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

/** Conversas e mensagens entre usuarios. */
@RestController
@RequestMapping("/api/conversas")
@Tag(name = "Mensagens", description = "Conversas entre compradores e vendedores")
public class ControladorDeMensagem {

    private final ServicoDeMensagem servico;

    public ControladorDeMensagem(ServicoDeMensagem servico) {
        this.servico = servico;
    }

    @GetMapping
    @Operation(summary = "Lista as conversas da conta, da mais recente para a mais antiga")
    public List<ConversaResponse> listar(@AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return servico.listar(autenticado.getUsuario());
    }

    @PostMapping
    @Operation(summary = "Abre uma conversa, ou devolve a existente, e envia a primeira mensagem")
    public ResponseEntity<ConversaResponse> abrir(@Valid @RequestBody NovaConversaRequest pedido,
                                                  @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        ConversaResponse conversa = servico.abrir(autenticado.getUsuario(), pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(conversa);
    }

    @GetMapping("/nao-lidas")
    @Operation(summary = "Total de mensagens nao lidas em todas as conversas")
    public Map<String, Long> naoLidas(@AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return Map.of("total", servico.naoLidas(autenticado.getUsuario()));
    }

    @GetMapping("/{id}/mensagens")
    @Operation(summary = "Mensagens de uma conversa, em ordem cronologica")
    public List<MensagemResponse> mensagens(@PathVariable Long id,
                                            @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return servico.mensagensDe(autenticado.getUsuario(), id);
    }

    @PostMapping("/{id}/mensagens")
    @Operation(summary = "Envia uma mensagem na conversa")
    public ResponseEntity<MensagemResponse> enviar(@PathVariable Long id,
                                                   @Valid @RequestBody MensagemRequest pedido,
                                                   @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        MensagemResponse enviada = servico.enviar(autenticado.getUsuario(), id, pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(enviada);
    }

    @PatchMapping("/{id}/lidas")
    @Operation(summary = "Marca como lidas as mensagens recebidas na conversa")
    public Map<String, Integer> marcarLidas(@PathVariable Long id,
                                            @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return Map.of("atualizadas", servico.marcarComoLidas(autenticado.getUsuario(), id));
    }
}
