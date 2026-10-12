package br.com.zyclos.dto.response;

import br.com.zyclos.domain.Papel;
import br.com.zyclos.domain.StatusUsuario;
import java.time.Instant;

/** Dados da conta devolvidos apos login e em /auth/eu. */
public record UsuarioResponse(
        Long id,
        String nome,
        String username,
        String email,
        String avatarUrl,
        String bio,
        String localizacao,
        Papel papel,
        StatusUsuario status,
        boolean verificado,
        Instant criadoEm) {
}
