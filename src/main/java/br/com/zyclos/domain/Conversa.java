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
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.Objects;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Fio de conversa entre dois usuarios, opcionalmente ligado a um anuncio.
 *
 * <p>Os participantes sao guardados em ordem crescente de id para que o par seja unico,
 * independentemente de quem iniciou a conversa.
 */
@Entity
@Table(name = "conversa", uniqueConstraints = @UniqueConstraint(
        name = "uk_conversa_participantes_anuncio",
        columnNames = {"participante_a_id", "participante_b_id", "anuncio_id"}))
public class Conversa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participante_a_id", nullable = false)
    private Usuario participanteA;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participante_b_id", nullable = false)
    private Usuario participanteB;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anuncio_id")
    private Anuncio anuncio;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm = Instant.now();

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    protected Conversa() {
    }

    private Conversa(Usuario participanteA, Usuario participanteB, Anuncio anuncio) {
        this.participanteA = participanteA;
        this.participanteB = participanteB;
        this.anuncio = anuncio;
    }

    /** Cria a conversa com os participantes normalizados pelo id, do menor para o maior. */
    public static Conversa entre(Usuario um, Usuario outro, Anuncio anuncio) {
        boolean umPrimeiro = um.getId() <= outro.getId();
        return new Conversa(umPrimeiro ? um : outro, umPrimeiro ? outro : um, anuncio);
    }

    public boolean participa(Usuario usuario) {
        if (usuario == null) {
            return false;
        }
        return Objects.equals(participanteA.getId(), usuario.getId())
                || Objects.equals(participanteB.getId(), usuario.getId());
    }

    /** Retorna o outro lado da conversa em relacao ao usuario informado. */
    public Usuario interlocutorDe(Usuario usuario) {
        return Objects.equals(participanteA.getId(), usuario.getId()) ? participanteB : participanteA;
    }

    public void tocar() {
        this.atualizadoEm = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Usuario getParticipanteA() {
        return participanteA;
    }

    public Usuario getParticipanteB() {
        return participanteB;
    }

    public Anuncio getAnuncio() {
        return anuncio;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        return outro instanceof Conversa conversa && id != null && id.equals(conversa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
