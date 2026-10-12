package br.com.zyclos.dto.response;

import java.time.Instant;

/** Resposta de login e cadastro, com o token JWT e a conta autenticada. */
public record TokenResponse(
        String token,
        String tipo,
        Instant expiraEm,
        UsuarioResponse usuario) {

    public static TokenResponse de(String token, Instant expiraEm, UsuarioResponse usuario) {
        return new TokenResponse(token, "Bearer", expiraEm, usuario);
    }
}
