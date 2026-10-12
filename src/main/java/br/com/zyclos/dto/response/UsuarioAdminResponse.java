package br.com.zyclos.dto.response;

import br.com.zyclos.domain.Papel;
import br.com.zyclos.domain.StatusUsuario;
import java.time.Instant;

/** Linha da tabela de usuarios no painel administrativo. */
public record UsuarioAdminResponse(
        Long id,
        String nome,
        String username,
        String email,
        Papel papel,
        StatusUsuario status,
        long totalAnuncios,
        Instant criadoEm) {
}
