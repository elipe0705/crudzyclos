package br.com.zyclos.web;

import br.com.zyclos.dto.response.NotificacaoResponse;
import br.com.zyclos.security.UsuarioAutenticado;
import br.com.zyclos.service.ServicoDeNotificacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Central de notificacoes. */
@RestController
@RequestMapping("/api/notificacoes")
@Tag(name = "Notificacoes", description = "Avisos da comunidade")
public class ControladorDeNotificacao {

    private final ServicoDeNotificacao notificacoes;

    public ControladorDeNotificacao(ServicoDeNotificacao notificacoes) {
        this.notificacoes = notificacoes;
    }

    @GetMapping
    @Operation(summary = "Lista as notificacoes da conta, da mais recente para a mais antiga")
    public List<NotificacaoResponse> listar(@AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return notificacoes.listar(autenticado.getUsuario());
    }

    @GetMapping("/nao-lidas")
    @Operation(summary = "Quantidade de notificacoes ainda nao lidas")
    public Map<String, Long> naoLidas(@AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return Map.of("total", notificacoes.naoLidas(autenticado.getUsuario()));
    }

    @PatchMapping("/{id}/lida")
    @Operation(summary = "Marca uma notificacao como lida")
    public NotificacaoResponse marcar(@PathVariable Long id,
                                      @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return notificacoes.marcarComoLida(autenticado.getUsuario(), id);
    }

    @PatchMapping("/lidas")
    @Operation(summary = "Marca todas as notificacoes como lidas")
    public Map<String, Integer> marcarTodas(@AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return Map.of("atualizadas", notificacoes.marcarTodasComoLidas(autenticado.getUsuario()));
    }
}
