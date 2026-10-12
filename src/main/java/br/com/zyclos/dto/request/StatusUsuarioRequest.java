package br.com.zyclos.dto.request;

import br.com.zyclos.domain.StatusUsuario;
import jakarta.validation.constraints.NotNull;

/** Mudanca de situacao de uma conta pelo painel administrativo. */
public record StatusUsuarioRequest(
        @NotNull(message = "Informe o novo status.")
        StatusUsuario status) {
}
