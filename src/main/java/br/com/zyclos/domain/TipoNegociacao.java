package br.com.zyclos.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Formas de negociação aceitas em um anúncio. */
public enum TipoNegociacao {
    VENDA("Venda"),
    TROCA("Troca"),
    VENDA_OU_TROCA("Venda ou troca");

    private final String rotulo;

    TipoNegociacao(String rotulo) {
        this.rotulo = rotulo;
    }

    @JsonValue
    public String getRotulo() {
        return rotulo;
    }

    /** Indica se o anúncio pode receber propostas de troca. */
    public boolean aceitaTroca() {
        return this == TROCA || this == VENDA_OU_TROCA;
    }

    /** Indica se o anúncio exige um preço definido. */
    public boolean exigePreco() {
        return this == VENDA || this == VENDA_OU_TROCA;
    }

    @JsonCreator
    public static TipoNegociacao de(String valor) {
        return Rotulos.resolver(TipoNegociacao.class, values(), valor, TipoNegociacao::getRotulo);
    }
}
