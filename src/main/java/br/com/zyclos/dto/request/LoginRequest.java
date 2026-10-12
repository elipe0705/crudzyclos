package br.com.zyclos.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Credenciais enviadas pela tela de login. */
public record LoginRequest(
        @NotBlank(message = "Informe o e-mail.")
        @Email(message = "Digite um e-mail valido.")
        @Size(max = 255)
        String email,

        @NotBlank(message = "Informe a senha.")
        @Size(min = 8, max = 72, message = "A senha deve ter pelo menos 8 caracteres.")
        String senha) {
}
