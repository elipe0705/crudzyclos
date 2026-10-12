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
import org.hibernate.annotations.CreationTimestamp;

/** Mensagem trocada dentro de uma conversa. */
@Entity
@Table(name = "mensagem")
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversa_id", nullable = false)
    private Conversa conversa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "remetente_id", nullable = false)
    private Usuario remetente;

    @Column(nullable = false, length = 2000)
    private String conteudo;

    @Column(nullable = false)
    private boolean lida = false;

    @CreationTimestamp
    @Column(name = "enviada_em", nullable = false, updatable = false)
    private Instant enviadaEm;

    protected Mensagem() {
    }

    public Mensagem(Conversa conversa, Usuario remetente, String conteudo) {
        this.conversa = conversa;
        this.remetente = remetente;
        this.conteudo = conteudo;
    }

    public void marcarComoLida() {
        this.lida = true;
    }

    public Long getId() {
        return id;
    }

    public Conversa getConversa() {
        return conversa;
    }

    public Usuario getRemetente() {
        return remetente;
    }

    public String getConteudo() {
        return conteudo;
    }

    public boolean isLida() {
        return lida;
    }

    public Instant getEnviadaEm() {
        return enviadaEm;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        return outro instanceof Mensagem mensagem && id != null && id.equals(mensagem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
