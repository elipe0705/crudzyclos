package br.com.zyclos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/** Preferencias exibidas na tela de configuracoes da conta. */
@Entity
@Table(name = "preferencias_usuario")
public class PreferenciasUsuario {

    @Id
    @Column(name = "usuario_id")
    private Long usuarioId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "receber_notificacoes", nullable = false)
    private boolean receberNotificacoes = true;

    @Column(name = "receber_mensagens", nullable = false)
    private boolean receberMensagens = true;

    @Column(name = "perfil_publico", nullable = false)
    private boolean perfilPublico = true;

    @Column(name = "receber_novidades", nullable = false)
    private boolean receberNovidades = false;

    protected PreferenciasUsuario() {
    }

    public PreferenciasUsuario(Usuario usuario) {
        this.usuario = usuario;
        this.usuarioId = usuario.getId();
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean isReceberNotificacoes() {
        return receberNotificacoes;
    }

    public void setReceberNotificacoes(boolean receberNotificacoes) {
        this.receberNotificacoes = receberNotificacoes;
    }

    public boolean isReceberMensagens() {
        return receberMensagens;
    }

    public void setReceberMensagens(boolean receberMensagens) {
        this.receberMensagens = receberMensagens;
    }

    public boolean isPerfilPublico() {
        return perfilPublico;
    }

    public void setPerfilPublico(boolean perfilPublico) {
        this.perfilPublico = perfilPublico;
    }

    public boolean isReceberNovidades() {
        return receberNovidades;
    }

    public void setReceberNovidades(boolean receberNovidades) {
        this.receberNovidades = receberNovidades;
    }
}
