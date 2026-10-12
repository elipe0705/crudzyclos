package br.com.zyclos.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Avaliacao deixada no perfil de outro usuario. */
public record AvaliacaoRequest(
        @NotNull(message = "Informe a nota.")
        @Min(value = 1, message = "A nota vai de 1 a 5.")
        @Max(value = 5, message = "A nota vai de 1 a 5.")
        Integer nota,

        @Size(max = 1000)
        String comentario) {
}
