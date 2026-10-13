package br.com.zyclos.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Usuario")
public class Usuario {

    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO-INCREMENTO SEQUENCIAL 1 e 1
    private Long id;
    @Column(length = 100, nullable = false)
    private String nome;
    @Column(length = 30, nullable = false)
    private String username;
    @Column(length = 255, nullable = false)
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // a senha é recebida, mas nunca devolvida no JSON
    @Column(length = 255, nullable = false)
    private String senha;
    @Column(length = 500, nullable = true)
    private String avatarUrl;
    @Column(length = 500, nullable = true)
    private String bio;
    @Column(length = 120, nullable = true)
    private String cidade;
    @Column(length = 2, nullable = true)
    private String estado;
    @Column(length = 20, nullable = false)
    private String tipoUsuario;
    @Column(nullable = false)
    private boolean verificado;
    @Column(nullable = false)
    private LocalDateTime dataCadastro;
    private boolean codStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
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

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public boolean isVerificado() {
        return verificado;
    }

    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public boolean isCodStatus() {
        return codStatus;
    }

    public void setCodStatus(boolean codStatus) {
        this.codStatus = codStatus;
    }
}
