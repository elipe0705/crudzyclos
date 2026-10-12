package br.com.zyclos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import org.hibernate.annotations.CreationTimestamp;

/** Aviso exibido na central de notificacoes do usuario. */
@Entity
@Table(name = "notificacao")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 300)
    private String texto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoNotificacao tipo = TipoNotificacao.SISTEMA;

    /** Caminho no front para onde a notificacao leva, por exemplo /trocas. */
    @Column(length = 200)
    private String destino;

    @Column(nullable = false)
    private boolean lida = false;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    protected Notificacao() {
    }

    public Notificacao(Usuario usuario, String texto, TipoNotificacao tipo, String destino) {
        this.usuario = usuario;
        this.texto = texto;
        this.tipo = tipo;
        this.destino = destino;
    }

    public void marcarComoLida() {
        this.lida = true;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getTexto() {
        return texto;
    }

    public TipoNotificacao getTipo() {
        return tipo;
    }

    public String getDestino() {
        return destino;
    }

    public boolean isLida() {
        return lida;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        return outro instanceof Notificacao notificacao && id != null && id.equals(notificacao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
