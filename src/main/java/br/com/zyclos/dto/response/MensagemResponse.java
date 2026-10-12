package br.com.zyclos.dto.response;

import java.time.Instant;

/** Mensagem dentro de um fio de conversa. */
public record MensagemResponse(
        Long id,
        String conteudo,
        AutorResponse remetente,
        boolean propria,
        boolean lida,
        Instant enviadaEm) {
}
