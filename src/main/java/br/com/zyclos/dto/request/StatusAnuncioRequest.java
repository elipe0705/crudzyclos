package br.com.zyclos.dto.request;

import br.com.zyclos.domain.StatusAnuncio;
import jakarta.validation.constraints.NotNull;

/** Mudanca de situacao de um anuncio. */
public record StatusAnuncioRequest(
        @NotNull(message = "Informe o novo status.")
        StatusAnuncio status) {
}
