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
import java.util.Objects;

/** Foto vinculada a um anuncio. */
@Entity
@Table(name = "anuncio_imagem")
public class AnuncioImagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anuncio_id", nullable = false)
    private Anuncio anuncio;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = false)
    private boolean principal = false;

    @Column(nullable = false)
    private int ordem = 0;

    protected AnuncioImagem() {
    }

    public AnuncioImagem(String url) {
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public Anuncio getAnuncio() {
        return anuncio;
    }

    public void setAnuncio(Anuncio anuncio) {
        this.anuncio = anuncio;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        return outro instanceof AnuncioImagem imagem && id != null && id.equals(imagem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
