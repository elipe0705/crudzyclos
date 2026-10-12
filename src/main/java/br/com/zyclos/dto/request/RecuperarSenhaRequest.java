package br.com.zyclos.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** E-mail informado no fluxo Esqueci minha senha. */
public record RecuperarSenhaRequest(
        @NotBlank(message = "Informe o e-mail.")
        @Email(message = "Digite um e-mail valido.")
        String email) {
}
