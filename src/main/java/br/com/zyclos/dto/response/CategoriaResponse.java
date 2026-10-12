package br.com.zyclos.dto.response;

import java.time.Instant;

/** Categoria com a contagem de anuncios ativos. */
public record CategoriaResponse(
        Long id,
        String nome,
        String slug,
        boolean ativa,
        long totalAnuncios,
        Instant criadoEm) {
}
