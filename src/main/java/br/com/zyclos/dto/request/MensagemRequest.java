package br.com.zyclos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Texto enviado dentro de uma conversa existente. */
public record MensagemRequest(
        @NotBlank(message = "Escreva uma mensagem.")
        @Size(max = 2000)
        String conteudo) {
}
