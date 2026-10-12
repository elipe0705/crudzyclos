package br.com.zyclos.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/** Peca publicada por um usuario para venda, troca ou ambos. */
@Entity
@Table(name = "anuncio")
public class Anuncio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(nullable = false, length = 2000)
    private String descricao;

    /** Nulo quando o anuncio aceita apenas troca. */
    @Column(precision = 10, scale = 2)
    private BigDecimal preco;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Usuario vendedor;

    @Column(length = 60)
    private String marca;

    @Column(nullable = false, length = 20)
    private String tamanho;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Condicao condicao;

    @Column(length = 40)
    private String estilo;

    @Column(length = 120)
    private String cidade;

    @Column(length = 2)
    private String estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_negociacao", nullable = false, length = 20)
    private TipoNegociacao tipoNegociacao = TipoNegociacao.VENDA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusAnuncio status = StatusAnuncio.ATIVO;

    @Column(nullable = false)
    private long visualizacoes = 0;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "anuncio_tag", joinColumns = @JoinColumn(name = "anuncio_id"))
    @Column(name = "tag", length = 40, nullable = false)
    private Set<String> tags = new LinkedHashSet<>();

    @OneToMany(mappedBy = "anuncio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("ordem ASC, id ASC")
    private List<AnuncioImagem> imagens = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private Instant atualizadoEm;

    protected Anuncio() {
    }

    public Anuncio(String titulo, String descricao, Categoria categoria, Usuario vendedor) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.vendedor = vendedor;
    }

    /** Localidade formatada como a interface exibe, por exemplo Sao Paulo, SP. */
    public String localizacao() {
        if (cidade == null || cidade.isBlank()) {
            return null;
        }
        return estado == null || estado.isBlank() ? cidade : cidade + ", " + estado;
    }

    /** URL da primeira imagem, usada nos cards de listagem. */
    public String imagemPrincipal() {
        return imagens.stream()
                .filter(AnuncioImagem::isPrincipal)
                .map(AnuncioImagem::getUrl)
                .findFirst()
                .orElseGet(() -> imagens.isEmpty() ? null : imagens.get(0).getUrl());
    }

    public void adicionarImagem(AnuncioImagem imagem) {
        imagem.setAnuncio(this);
        imagem.setOrdem(imagens.size());
        imagem.setPrincipal(imagens.isEmpty());
        imagens.add(imagem);
    }

    public void limparImagens() {
        imagens.clear();
    }

    public boolean pertenceA(Usuario usuario) {
        return usuario != null && vendedor != null && Objects.equals(vendedor.getId(), usuario.getId());
    }

    public boolean disponivel() {
        return status == StatusAnuncio.ATIVO;
    }

    public void registrarVisualizacao() {
        visualizacoes++;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Usuario getVendedor() {
        return vendedor;
    }

    public void setVendedor(Usuario vendedor) {
        this.vendedor = vendedor;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    public Condicao getCondicao() {
        return condicao;
    }

    public void setCondicao(Condicao condicao) {
        this.condicao = condicao;
    }

    public String getEstilo() {
        return estilo;
    }

    public void setEstilo(String estilo) {
        this.estilo = estilo;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public TipoNegociacao getTipoNegociacao() {
        return tipoNegociacao;
    }

    public void setTipoNegociacao(TipoNegociacao tipoNegociacao) {
        this.tipoNegociacao = tipoNegociacao;
    }

    public StatusAnuncio getStatus() {
        return status;
    }

    public void setStatus(StatusAnuncio status) {
        this.status = status;
    }

    public long getVisualizacoes() {
        return visualizacoes;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags == null ? new LinkedHashSet<>() : new LinkedHashSet<>(tags);
    }

    public List<AnuncioImagem> getImagens() {
        return imagens;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        return outro instanceof Anuncio anuncio && id != null && id.equals(anuncio.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
