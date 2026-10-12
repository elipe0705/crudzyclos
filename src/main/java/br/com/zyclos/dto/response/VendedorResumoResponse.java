package br.com.zyclos.dto.response;

/** Bloco do vendedor exibido na pagina do anuncio. */
public record VendedorResumoResponse(
        Long id,
        String nome,
        String username,
        String avatarUrl,
        String localizacao,
        boolean verificado,
        Double avaliacaoMedia,
        long totalAvaliacoes) {
}
