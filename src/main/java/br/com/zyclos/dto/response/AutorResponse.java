package br.com.zyclos.dto.response;

/** Identificacao enxuta de um usuario, usada dentro de outros recursos. */
public record AutorResponse(
        Long id,
        String nome,
        String username,
        String avatarUrl) {
}
