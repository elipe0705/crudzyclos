package br.com.zyclos.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Ciclo de vida de uma denúncia na moderação. */
public enum StatusDenuncia {
    PENDENTE("Pendente"),
    EM_ANALISE("Em análise"),
    RESOLVIDA("Resolvida"),
    ARQUIVADA("Arquivada");

    private final String rotulo;

    StatusDenuncia(String rotulo) {
        this.rotulo = rotulo;
    }

    @JsonValue
    public String getRotulo() {
        return rotulo;
    }

    @JsonCreator
    public static StatusDenuncia de(String valor) {
        return Rotulos.resolver(StatusDenuncia.class, values(), valor, StatusDenuncia::getRotulo);
    }
}
