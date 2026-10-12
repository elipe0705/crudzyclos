package br.com.zyclos.dto.request;

import br.com.zyclos.domain.StatusDenuncia;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Parecer da moderacao sobre uma denuncia. */
public record ResolverDenunciaRequest(
        @NotNull(message = "Informe o novo status.")
        StatusDenuncia status,

        @Size(max = 1000)
        String resposta) {
}
