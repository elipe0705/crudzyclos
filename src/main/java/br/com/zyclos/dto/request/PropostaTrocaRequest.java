package br.com.zyclos.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Proposta de troca montada no modal Monte sua proposta. */
public record PropostaTrocaRequest(
        @NotNull(message = "Escolha a peca que voce quer receber.")
        Long anuncioDesejadoId,

        @NotNull(message = "Escolha a peca que voce oferece.")
        Long anuncioOferecidoId,

        @Size(max = 500)
        String mensagem) {
}
