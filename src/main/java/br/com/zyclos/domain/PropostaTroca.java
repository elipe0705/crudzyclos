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
import org.hibernate.annotations.UpdateTimestamp;

/** Oferta de troca de uma peca por outra entre dois usuarios. */
@Entity
@Table(name = "proposta_troca")
public class PropostaTroca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Quem enviou a proposta. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proponente_id", nullable = false)
    private Usuario proponente;

    /** Dono do anuncio desejado. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destinatario_id", nullable = false)
    private Usuario destinatario;

    /** Peca que o proponente quer receber. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anuncio_desejado_id", nullable = false)
    private Anuncio anuncioDesejado;

    /** Peca que o proponente oferece em troca. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anuncio_oferecido_id", nullable = false)
    private Anuncio anuncioOferecido;

    @Column(length = 500)
    private String mensagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusProposta status = StatusProposta.PENDENTE;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private Instant atualizadoEm;

    protected PropostaTroca() {
    }

    public PropostaTroca(Usuario proponente, Anuncio anuncioDesejado, Anuncio anuncioOferecido, String mensagem) {
        this.proponente = proponente;
        this.destinatario = anuncioDesejado.getVendedor();
        this.anuncioDesejado = anuncioDesejado;
        this.anuncioOferecido = anuncioOferecido;
        this.mensagem = mensagem;
    }

    public boolean envolve(Usuario usuario) {
        if (usuario == null) {
            return false;
        }
        return Objects.equals(proponente.getId(), usuario.getId())
                || Objects.equals(destinatario.getId(), usuario.getId());
    }

    public Long getId() {
        return id;
    }

    public Usuario getProponente() {
        return proponente;
    }

    public Usuario getDestinatario() {
        return destinatario;
    }

    public Anuncio getAnuncioDesejado() {
        return anuncioDesejado;
    }

    public Anuncio getAnuncioOferecido() {
        return anuncioOferecido;
    }

    public String getMensagem() {
        return mensagem;
    }

    public StatusProposta getStatus() {
        return status;
    }

    public void setStatus(StatusProposta status) {
        this.status = status;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        return outro instanceof PropostaTroca proposta && id != null && id.equals(proposta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
