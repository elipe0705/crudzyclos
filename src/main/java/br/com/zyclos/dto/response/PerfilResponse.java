package br.com.zyclos.dto.response;

import java.time.Instant;
import java.util.List;

/** Perfil publico exibido em /perfil/{username}. */
public record PerfilResponse(
        Long id,
        String nome,
        String username,
        String avatarUrl,
        String bio,
        String localizacao,
        boolean verificado,
        Double avaliacaoMedia,
        long totalAvaliacoes,
        long totalAnuncios,
        long seguidores,
        long seguindo,
        boolean seguidoPorMim,
        boolean proprioPerfil,
        Instant membroDesde,
        List<AnuncioResumoResponse> anuncios) {
}
