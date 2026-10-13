package br.com.zyclos.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Conversa")
public class Conversa {

    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO-INCREMENTO SEQUENCIAL 1 e 1
    private Long id;
    @Column(nullable = false)
    private LocalDateTime dataCadastro;
    @Column(nullable = true)
    private LocalDateTime dataAtualizacao;
    @Column(name = "participante_a_id", nullable = false)
    private Long participanteAId;
    @Column(name = "participante_b_id", nullable = false)
    private Long participanteBId;
    @Column(name = "anuncio_id", nullable = true)
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

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public Long getParticipanteAId() {
        return participanteAId;
    }

    public void setParticipanteAId(Long participanteAId) {
        this.participanteAId = participanteAId;
    }

    public Long getParticipanteBId() {
        return participanteBId;
    }

    public void setParticipanteBId(Long participanteBId) {
        this.participanteBId = participanteBId;
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
