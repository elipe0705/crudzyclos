package br.com.zyclos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Abertura de conversa com outro usuario, opcionalmente a partir de um anuncio. */
public record NovaConversaRequest(
        @NotBlank(message = "Informe com quem deseja conversar.")
        String destinatario,

        Long anuncioId,

        @Size(max = 2000)
        String mensagem) {
}
