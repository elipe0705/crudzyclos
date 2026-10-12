package br.com.zyclos.dto.response;

import java.time.Instant;

/** Avaliacao exibida na aba Avaliacoes do perfil. */
public record AvaliacaoResponse(
        Long id,
        AutorResponse autor,
        int nota,
        String comentario,
        Instant criadoEm) {
}
