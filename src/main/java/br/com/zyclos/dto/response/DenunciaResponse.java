package br.com.zyclos.dto.response;

import br.com.zyclos.domain.StatusDenuncia;
import java.time.Instant;

/** Ocorrencia listada no painel de moderacao. */
public record DenunciaResponse(
        Long id,
        AutorResponse denunciante,
        String anuncio,
        Long anuncioId,
        AutorResponse usuarioDenunciado,
        String motivo,
        String descricao,
        StatusDenuncia status,
        String respostaModeracao,
        Instant criadoEm,
        Instant resolvidoEm) {
}
