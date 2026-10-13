package br.com.zyclos.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Anuncio")
public class Anuncio {

    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO-INCREMENTO SEQUENCIAL 1 e 1
    private Long id;
    @Column(length = 120, nullable = false)
    private String titulo;
    @Column(length = 2000, nullable = false)
    private String descricao;
    @Column(columnDefinition = "DECIMAL(10,2)", nullable = true) // nulo para anúncios de troca ou doação
    private Double preco;
    @Column(length = 60, nullable = true)
    private String marca;
    @Column(length = 20, nullable = false)
    private String tamanho;
    @Column(length = 30, nullable = false)
    private String condicao;
    @Column(length = 40, nullable = true)
    private String estilo;
    @Column(length = 120, nullable = true)
    private String cidade;
    @Column(length = 2, nullable = true)
    private String estado;
    @Column(length = 20, nullable = false)
    private String tipoNegociacao;
    @Column(length = 20, nullable = false)
    private String status;
    @Column(nullable = false)
    private long visualizacoes;
    @Column(nullable = false)
    private LocalDateTime dataCadastro;
    @Column(nullable = true)
    private LocalDateTime dataAtualizacao;
    @Column(name = "categoria_id", nullable = false)
    private Long categoriaId;
    @Column(name = "vendedor_id", nullable = false)
    private Long vendedorId;
    private boolean codStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
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

    public String getCondicao() {
        return condicao;
    }

    public void setCondicao(String condicao) {
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

    public String getTipoNegociacao() {
        return tipoNegociacao;
    }

    public void setTipoNegociacao(String tipoNegociacao) {
        this.tipoNegociacao = tipoNegociacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getVisualizacoes() {
        return visualizacoes;
    }

    public void setVisualizacoes(long visualizacoes) {
        this.visualizacoes = visualizacoes;
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

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Long getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(Long vendedorId) {
        this.vendedorId = vendedorId;
    }

    public boolean isCodStatus() {
        return codStatus;
    }

    public void setCodStatus(boolean codStatus) {
        this.codStatus = codStatus;
    }
}
