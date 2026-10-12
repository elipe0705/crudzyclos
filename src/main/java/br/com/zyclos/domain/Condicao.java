package br.com.zyclos.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Estado de conservação informado pelo anunciante. */
public enum Condicao {
    NOVO_COM_ETIQUETA("Novo com etiqueta"),
    EXCELENTE("Excelente"),
    MUITO_BOM("Muito bom"),
    BOM("Bom"),
    USADO("Usado");

    private final String rotulo;

    Condicao(String rotulo) {
        this.rotulo = rotulo;
    }

    @JsonValue
    public String getRotulo() {
        return rotulo;
    }

    @JsonCreator
    public static Condicao de(String valor) {
        return Rotulos.resolver(Condicao.class, values(), valor, Condicao::getRotulo);
    }
}
