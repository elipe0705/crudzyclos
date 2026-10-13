package br.com.zyclos.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Favorito")
public class Favorito {

    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO-INCREMENTO SEQUENCIAL 1 e 1
    private Long id;
    @Column(nullable = false)
    private LocalDateTime dataCadastro;
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @Column(name = "anuncio_id", nullable = false)
    private Long anuncioId;
    private boolean codStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getAnuncioId() {
        return anuncioId;
    }

    public void setAnuncioId(Long anuncioId) {
        this.anuncioId = anuncioId;
    }

    public boolean isCodStatus() {
        return codStatus;
    }

    public void setCodStatus(boolean codStatus) {
        this.codStatus = codStatus;
    }
}
