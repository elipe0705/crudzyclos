package br.com.zyclos.dto.response;

/** Cartao de indicador do painel administrativo. */
public record IndicadorResponse(
        String rotulo,
        long valor,
        String variacao) {
}
