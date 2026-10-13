package br.com.zyclos.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Avaliacao")
public class Avaliacao {

    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO-INCREMENTO SEQUENCIAL 1 e 1
    private Long id;
    @Column(nullable = false) // de 1 a 5
    private int nota;
    @Column(length = 1000, nullable = true)
    private String comentario;
    @Column(nullable = false)
    private LocalDateTime dataCadastro;
    @Column(name = "autor_id", nullable = false)
    private Long autorId;
    @Column(name = "avaliado_id", nullable = false)
    private Long avaliadoId;
    @Column(name = "anuncio_id", nullable = true)
    private Long anuncioId;
    private boolean codStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Long getAutorId() {
        return autorId;
    }

    public void setAutorId(Long autorId) {
        this.autorId = autorId;
    }

    public Long getAvaliadoId() {
        return avaliadoId;
    }

    public void setAvaliadoId(Long avaliadoId) {
        this.avaliadoId = avaliadoId;
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
