package br.com.zyclos.dto.response;

import br.com.zyclos.domain.Condicao;
import br.com.zyclos.domain.StatusAnuncio;
import br.com.zyclos.domain.TipoNegociacao;
import java.math.BigDecimal;
import java.time.Instant;

/** Versao resumida de um anuncio, usada nos cards de listagem. */
public record AnuncioResumoResponse(
        Long id,
        String titulo,
        BigDecimal preco,
        String imagem,
        String categoria,
        String marca,
        String tamanho,
        Condicao condicao,
        String localizacao,
        TipoNegociacao tipoNegociacao,
        StatusAnuncio status,
        AutorResponse vendedor,
        boolean favorito,
        Instant criadoEm) {
}
