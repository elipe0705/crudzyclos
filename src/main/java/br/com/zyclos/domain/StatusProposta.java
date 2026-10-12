package br.com.zyclos.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Ciclo de vida de uma proposta de troca. */
public enum StatusProposta {
    PENDENTE("Aguardando resposta"),
    ACEITA("Aceita"),
    RECUSADA("Recusada"),
    CANCELADA("Cancelada"),
    CONCLUIDA("Concluída");

    private final String rotulo;

    StatusProposta(String rotulo) {
        this.rotulo = rotulo;
    }

    @JsonValue
    public String getRotulo() {
        return rotulo;
    }

    /** Propostas encerradas não podem mais mudar de estado. */
    public boolean encerrada() {
        return this == RECUSADA || this == CANCELADA || this == CONCLUIDA;
    }

    @JsonCreator
    public static StatusProposta de(String valor) {
        return Rotulos.resolver(StatusProposta.class, values(), valor, StatusProposta::getRotulo);
    }
}
