package br.com.zyclos.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Seguidor")
public class Seguidor {

    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO-INCREMENTO SEQUENCIAL 1 e 1
    private Long id;
    @Column(nullable = false)
    private LocalDateTime dataCadastro;
    @Column(name = "seguidor_id", nullable = false)
    private Long seguidorId;
    @Column(name = "seguido_id", nullable = false)
    private Long seguidoId;
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

    public Long getSeguidorId() {
        return seguidorId;
    }

    public void setSeguidorId(Long seguidorId) {
        this.seguidorId = seguidorId;
    }

    public Long getSeguidoId() {
        return seguidoId;
    }

    public void setSeguidoId(Long seguidoId) {
        this.seguidoId = seguidoId;
    }

    public boolean isCodStatus() {
        return codStatus;
    }

    public void setCodStatus(boolean codStatus) {
        this.codStatus = codStatus;
    }
}
