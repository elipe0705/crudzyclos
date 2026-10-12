package br.com.zyclos.dto.response;

/** Ponto mensal dos graficos do painel administrativo. */
public record PontoSerieResponse(
        String nome,
        long usuarios,
        long anuncios) {
}
