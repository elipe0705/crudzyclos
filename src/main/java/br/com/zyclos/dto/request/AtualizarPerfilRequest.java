package br.com.zyclos.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Campos editaveis do proprio perfil. */
public record AtualizarPerfilRequest(
        @Size(min = 2, max = 100, message = "Digite seu nome completo.")
        String nome,

        @Size(min = 3, max = 30, message = "O nome de usuario deve ter ao menos 3 caracteres.")
        @Pattern(regexp = "^[a-zA-Z0-9._]+$", message = "Use apenas letras, numeros, ponto ou sublinhado.")
        String username,

        @Size(max = 500)
        String bio,

        @Size(max = 120)
        String cidade,

        @Size(max = 2)
        String estado,

        @Size(max = 500)
        String avatarUrl) {
}
