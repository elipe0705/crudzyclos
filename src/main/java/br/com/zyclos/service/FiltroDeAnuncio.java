package br.com.zyclos.service;

import br.com.zyclos.domain.Condicao;
import br.com.zyclos.domain.TipoNegociacao;
import java.math.BigDecimal;

/**
 * Parametros de busca do catalogo.
 *
 * <p>Os nomes acompanham os filtros ja existentes na tela de anuncios: texto livre,
 * categoria, tipo de negociacao, marca, tamanho, condicao e ordenacao.
 */
public record FiltroDeAnuncio(
        String q,
        String categoria,
        TipoNegociacao negociacao,
        String marca,
        String tamanho,
        Condicao condicao,
        String cidade,
        BigDecimal precoMinimo,
        BigDecimal precoMaximo,
        String ordenar) {

    /** Valor aceito pelo parametro ordenar quando nenhum e informado. */
    public static final String ORDEM_PADRAO = "recentes";

    public String ordenarOuPadrao() {
        return ordenar == null || ordenar.isBlank() ? ORDEM_PADRAO : ordenar;
    }
}
