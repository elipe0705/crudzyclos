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

/** Marcacao de um anuncio como favorito por um usuario. */
@Entity
@Table(name = "favorito", uniqueConstraints = @UniqueConstraint(
        name = "uk_favorito_usuario_anuncio", columnNames = {"usuario_id", "anuncio_id"}))
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anuncio_id", nullable = false)
    private Anuncio anuncio;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    protected Favorito() {
    }

    public Favorito(Usuario usuario, Anuncio anuncio) {
        this.usuario = usuario;
        this.anuncio = anuncio;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Anuncio getAnuncio() {
        return anuncio;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        return outro instanceof Favorito favorito && id != null && id.equals(favorito.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
