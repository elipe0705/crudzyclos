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

/** Relacao de acompanhamento entre dois perfis da comunidade. */
@Entity
@Table(name = "seguidor", uniqueConstraints = @UniqueConstraint(
        name = "uk_seguidor_par", columnNames = {"seguidor_id", "seguido_id"}))
public class Seguidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Quem clicou em Seguir. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seguidor_id", nullable = false)
    private Usuario seguidor;

    /** Perfil acompanhado. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seguido_id", nullable = false)
    private Usuario seguido;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    protected Seguidor() {
    }

    public Seguidor(Usuario seguidor, Usuario seguido) {
        this.seguidor = seguidor;
        this.seguido = seguido;
    }

    public Long getId() {
        return id;
    }

    public Usuario getSeguidor() {
        return seguidor;
    }

    public Usuario getSeguido() {
        return seguido;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        return outro instanceof Seguidor seguidorOutro && id != null && id.equals(seguidorOutro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
