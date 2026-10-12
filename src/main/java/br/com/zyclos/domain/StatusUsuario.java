package br.com.zyclos.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Situação da conta de um usuário. */
public enum StatusUsuario {
    ATIVO("Ativo"),
    SUSPENSO("Suspenso"),
    INATIVO("Inativo");

    private final String rotulo;

    StatusUsuario(String rotulo) {
        this.rotulo = rotulo;
    }

    @JsonValue
    public String getRotulo() {
        return rotulo;
    }

    @JsonCreator
    public static StatusUsuario de(String valor) {
        return Rotulos.resolver(StatusUsuario.class, values(), valor, StatusUsuario::getRotulo);
    }
}
