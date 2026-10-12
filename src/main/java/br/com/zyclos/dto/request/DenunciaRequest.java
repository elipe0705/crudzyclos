package br.com.zyclos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Denuncia aberta por um usuario sobre um anuncio ou sobre um perfil. */
public record DenunciaRequest(
        Long anuncioId,

        String usuarioDenunciado,

        @NotBlank(message = "Informe o motivo.")
        @Size(max = 120)
        String motivo,

        @Size(max = 1000)
        String descricao) {
}
