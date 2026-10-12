package br.com.zyclos.dto.response;

import br.com.zyclos.domain.StatusProposta;
import java.time.Instant;

/** Proposta de troca exibida nas abas da pagina de trocas. */
public record PropostaTrocaResponse(
        Long id,
        AutorResponse proponente,
        AutorResponse destinatario,
        AnuncioResumoResponse anuncioDesejado,
        AnuncioResumoResponse anuncioOferecido,
        String mensagem,
        StatusProposta status,
        boolean souProponente,
        Instant criadoEm,
        Instant atualizadoEm) {
}
