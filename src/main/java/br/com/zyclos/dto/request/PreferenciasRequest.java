package br.com.zyclos.dto.request;

import jakarta.validation.constraints.NotNull;

/** Chaves da tela de configuracoes da conta. */
public record PreferenciasRequest(
        @NotNull Boolean receberNotificacoes,
        @NotNull Boolean receberMensagens,
        @NotNull Boolean perfilPublico,
        @NotNull Boolean receberNovidades) {
}
