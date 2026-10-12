package br.com.zyclos.dto.response;

import java.time.Instant;

/** Linha do bloco Atividade recente do painel. */
public record AtividadeResponse(
        String descricao,
        String tipo,
        Instant quando) {
}
