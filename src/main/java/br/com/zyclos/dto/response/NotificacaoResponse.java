package br.com.zyclos.dto.response;

import br.com.zyclos.domain.TipoNotificacao;
import java.time.Instant;

/** Item da central de notificacoes. */
public record NotificacaoResponse(
        Long id,
        String texto,
        TipoNotificacao tipo,
        String destino,
        boolean lida,
        Instant criadoEm) {
}
