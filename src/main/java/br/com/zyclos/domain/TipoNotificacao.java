package br.com.zyclos.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Categoria de uma notificação, usada pelo front para escolher o ícone. */
public enum TipoNotificacao {
    TROCA("troca"),
    PERFIL("perfil"),
    FAVORITO("favorito"),
    MENSAGEM("mensagem"),
    SUCESSO("sucesso"),
    DENUNCIA("denuncia"),
    SISTEMA("sistema");

    private final String rotulo;

    TipoNotificacao(String rotulo) {
        this.rotulo = rotulo;
    }

    @JsonValue
    public String getRotulo() {
        return rotulo;
    }

    @JsonCreator
    public static TipoNotificacao de(String valor) {
        return Rotulos.resolver(TipoNotificacao.class, values(), valor, TipoNotificacao::getRotulo);
    }
}
