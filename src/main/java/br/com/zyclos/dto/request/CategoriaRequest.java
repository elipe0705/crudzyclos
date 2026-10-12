package br.com.zyclos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Criacao ou edicao de categoria pelo painel administrativo. */
public record CategoriaRequest(
        @NotBlank(message = "Informe o nome da categoria.")
        @Size(max = 60)
        String nome,

        Boolean ativa) {
}
