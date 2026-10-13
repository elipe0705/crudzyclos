package br.com.zyclos.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Denuncia")
public class Denuncia {

    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO-INCREMENTO SEQUENCIAL 1 e 1
    private Long id;
    @Column(length = 120, nullable = false)
    private String motivo;
    @Column(length = 1000, nullable = true)
    private String descricao;
    @Column(length = 20, nullable = false)
    private String status;
    @Column(length = 1000, nullable = true)
    private String respostaModeracao;
    @Column(nullable = false)
    private LocalDateTime dataCadastro;
    @Column(nullable = true)
    private LocalDateTime dataResolucao;
    @Column(name = "denunciante_id", nullable = false)
    private Long denuncianteId;
    @Column(name = "anuncio_id", nullable = true)
    private Long anuncioId;
    @Column(name = "usuario_denunciado_id", nullable = true)
    private Long usuarioDenunciadoId;
    private boolean codStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRespostaModeracao() {
        return respostaModeracao;
    }

    public void setRespostaModeracao(String respostaModeracao) {
        this.respostaModeracao = respostaModeracao;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDateTime getDataResolucao() {
        return dataResolucao;
    }

    public void setDataResolucao(LocalDateTime dataResolucao) {
        this.dataResolucao = dataResolucao;
    }

    public Long getDenuncianteId() {
        return denuncianteId;
    }

    public void setDenuncianteId(Long denuncianteId) {
        this.denuncianteId = denuncianteId;
    }

    public Long getAnuncioId() {
        return anuncioId;
    }

    public void setAnuncioId(Long anuncioId) {
        this.anuncioId = anuncioId;
    }

    public Long getUsuarioDenunciadoId() {
        return usuarioDenunciadoId;
    }

    public void setUsuarioDenunciadoId(Long usuarioDenunciadoId) {
        this.usuarioDenunciadoId = usuarioDenunciadoId;
    }

    public boolean isCodStatus() {
        return codStatus;
    }

    public void setCodStatus(boolean codStatus) {
        this.codStatus = codStatus;
    }
}
