package br.com.zyclos.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "proposta_troca")
public class PropostaTroca {

    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO-INCREMENTO SEQUENCIAL 1 e 1
    private Long id;
    @Column(length = 500, nullable = true)
    private String mensagem;
    @Column(length = 20, nullable = false)
    private String status;
    @Column(nullable = false)
    private LocalDateTime dataCadastro;
    @Column(nullable = true)
    private LocalDateTime dataAtualizacao;
    @Column(name = "proponente_id", nullable = false)
    private Long proponenteId;
    @Column(name = "destinatario_id", nullable = false)
    private Long destinatarioId;
    @Column(name = "anuncio_desejado_id", nullable = false)
    private Long anuncioDesejadoId;
    @Column(name = "anuncio_oferecido_id", nullable = false)
    private Long anuncioOferecidoId;
    private boolean codStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Long getProponenteId() {
        return proponenteId;
    }

    public void setProponenteId(Long proponenteId) {
        this.proponenteId = proponenteId;
    }

    public Long getDestinatarioId() {
        return destinatarioId;
    }

    public void setDestinatarioId(Long destinatarioId) {
        this.destinatarioId = destinatarioId;
    }

    public Long getAnuncioDesejadoId() {
        return anuncioDesejadoId;
    }

    public void setAnuncioDesejadoId(Long anuncioDesejadoId) {
        this.anuncioDesejadoId = anuncioDesejadoId;
    }

    public Long getAnuncioOferecidoId() {
        return anuncioOferecidoId;
    }

    public void setAnuncioOferecidoId(Long anuncioOferecidoId) {
        this.anuncioOferecidoId = anuncioOferecidoId;
    }

    public boolean isCodStatus() {
        return codStatus;
    }

    public void setCodStatus(boolean codStatus) {
        this.codStatus = codStatus;
    }
}
