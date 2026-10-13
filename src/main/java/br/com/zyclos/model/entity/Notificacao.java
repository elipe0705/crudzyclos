package br.com.zyclos.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Notificacao")
public class Notificacao {

    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO-INCREMENTO SEQUENCIAL 1 e 1
    private Long id;
    @Column(length = 300, nullable = false)
    private String texto;
    @Column(length = 20, nullable = false)
    private String tipo;
    @Column(length = 200, nullable = true)
    private String destino;
    @Column(nullable = false)
    private boolean lida;
    @Column(nullable = false)
    private LocalDateTime dataCadastro;
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    private boolean codStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public boolean isLida() {
        return lida;
    }

    public void setLida(boolean lida) {
        this.lida = lida;
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

    public boolean isCodStatus() {
        return codStatus;
    }

    public void setCodStatus(boolean codStatus) {
        this.codStatus = codStatus;
    }
}
