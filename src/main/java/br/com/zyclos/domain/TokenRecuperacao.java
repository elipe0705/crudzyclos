package br.com.zyclos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;

/**
 * Codigo de uso unico para redefinir a senha.
 *
 * <p>Esta versao nao envia e-mail: o codigo e devolvido pela API em ambiente de desenvolvimento
 * e registrado no log, para que o fluxo possa ser demonstrado sem servico externo.
 */
@Entity
@Table(name = "token_recuperacao")
public class TokenRecuperacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, unique = true, length = 100)
    private String codigo;

    @Column(name = "expira_em", nullable = false)
    private Instant expiraEm;

    @Column(name = "usado_em")
    private Instant usadoEm;

    protected TokenRecuperacao() {
    }

    public TokenRecuperacao(Usuario usuario, String codigo, Instant expiraEm) {
        this.usuario = usuario;
        this.codigo = codigo;
        this.expiraEm = expiraEm;
    }

    public boolean valido() {
        return usadoEm == null && expiraEm.isAfter(Instant.now());
    }

    public void consumir() {
        this.usadoEm = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getCodigo() {
        return codigo;
    }

    public Instant getExpiraEm() {
        return expiraEm;
    }

    public Instant getUsadoEm() {
        return usadoEm;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        return outro instanceof TokenRecuperacao token && id != null && id.equals(token.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
