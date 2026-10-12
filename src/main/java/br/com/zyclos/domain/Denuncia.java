package br.com.zyclos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import org.hibernate.annotations.CreationTimestamp;

/** Ocorrencia registrada pela comunidade e tratada pela moderacao. */
@Entity
@Table(name = "denuncia")
public class Denuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "denunciante_id", nullable = false)
    private Usuario denunciante;

    /** Anuncio denunciado, quando a ocorrencia for sobre uma peca. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anuncio_id")
    private Anuncio anuncio;

    /** Perfil denunciado, quando a ocorrencia for sobre conduta. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_denunciado_id")
    private Usuario usuarioDenunciado;

    @Column(nullable = false, length = 120)
    private String motivo;

    @Column(length = 1000)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusDenuncia status = StatusDenuncia.PENDENTE;

    @Column(name = "resposta_moderacao", length = 1000)
    private String respostaModeracao;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Column(name = "resolvido_em")
    private Instant resolvidoEm;

    protected Denuncia() {
    }

    public Denuncia(Usuario denunciante, String motivo, String descricao) {
        this.denunciante = denunciante;
        this.motivo = motivo;
        this.descricao = descricao;
    }

    public void resolver(StatusDenuncia novoStatus, String resposta) {
        this.status = novoStatus;
        this.respostaModeracao = resposta;
        this.resolvidoEm = novoStatus == StatusDenuncia.PENDENTE || novoStatus == StatusDenuncia.EM_ANALISE
                ? null
                : Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Usuario getDenunciante() {
        return denunciante;
    }

    public Anuncio getAnuncio() {
        return anuncio;
    }

    public void setAnuncio(Anuncio anuncio) {
        this.anuncio = anuncio;
    }

    public Usuario getUsuarioDenunciado() {
        return usuarioDenunciado;
    }

    public void setUsuarioDenunciado(Usuario usuarioDenunciado) {
        this.usuarioDenunciado = usuarioDenunciado;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getDescricao() {
        return descricao;
    }

    public StatusDenuncia getStatus() {
        return status;
    }

    public String getRespostaModeracao() {
        return respostaModeracao;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getResolvidoEm() {
        return resolvidoEm;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        return outro instanceof Denuncia denuncia && id != null && id.equals(denuncia.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
