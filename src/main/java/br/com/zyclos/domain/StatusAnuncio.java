package br.com.zyclos.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Ciclo de vida de um anúncio. */
public enum StatusAnuncio {
    ATIVO("Ativo"),
    PAUSADO("Pausado"),
    NEGOCIADO("Negociado"),
    REMOVIDO("Removido");

    private final String rotulo;

    StatusAnuncio(String rotulo) {
        this.rotulo = rotulo;
    }

    @JsonValue
    public String getRotulo() {
        return rotulo;
    }

    @JsonCreator
    public static StatusAnuncio de(String valor) {
        return Rotulos.resolver(StatusAnuncio.class, values(), valor, StatusAnuncio::getRotulo);
    }
}
