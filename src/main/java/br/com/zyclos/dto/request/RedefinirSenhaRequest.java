package br.com.zyclos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Troca de senha a partir do token enviado por e-mail. */
public record RedefinirSenhaRequest(
        @NotBlank(message = "Token obrigatorio.")
        String token,

        @NotBlank(message = "Informe a nova senha.")
        @Size(min = 8, max = 72, message = "A senha deve ter pelo menos 8 caracteres.")
        String novaSenha) {
}
