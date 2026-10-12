package br.com.zyclos.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Dados do formulario de criacao de conta. */
public record CadastroRequest(
        @NotBlank(message = "Digite seu nome completo.")
        @Size(min = 2, max = 100, message = "Digite seu nome completo.")
        String nome,

        @NotBlank(message = "Escolha um nome de usuario.")
        @Size(min = 3, max = 30, message = "O nome de usuario deve ter ao menos 3 caracteres.")
        @Pattern(regexp = "^[a-zA-Z0-9._]+$", message = "Use apenas letras, numeros, ponto ou sublinhado.")
        String username,

        @NotBlank(message = "Informe o e-mail.")
        @Email(message = "Digite um e-mail valido.")
        @Size(max = 255)
        String email,

        @NotBlank(message = "Informe a senha.")
        @Size(min = 8, max = 72, message = "A senha deve ter pelo menos 8 caracteres.")
        String senha,

        @Size(max = 500)
        String avatarUrl) {
}
