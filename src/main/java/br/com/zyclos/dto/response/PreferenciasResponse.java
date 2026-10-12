package br.com.zyclos.dto.response;

/** Estado atual das preferencias da conta. */
public record PreferenciasResponse(
        boolean receberNotificacoes,
        boolean receberMensagens,
        boolean perfilPublico,
        boolean receberNovidades) {
}
