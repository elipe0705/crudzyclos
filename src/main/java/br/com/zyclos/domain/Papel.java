package br.com.zyclos.domain;

/** Perfil de acesso de um usuário na plataforma. */
public enum Papel {
    USUARIO,
    ADMIN;

    public String authority() {
        return "ROLE_" + name();
    }
}
