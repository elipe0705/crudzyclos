package br.com.zyclos.dto.response;

import java.time.Instant;

/** Linha da lista de conversas. */
public record ConversaResponse(
        Long id,
        AutorResponse interlocutor,
        String ultimaMensagem,
        Instant atualizadoEm,
        long naoLidas,
        AnuncioResumoResponse anuncio) {
}
