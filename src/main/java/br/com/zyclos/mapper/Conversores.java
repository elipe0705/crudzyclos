package br.com.zyclos.mapper;

import br.com.zyclos.domain.Anuncio;
import br.com.zyclos.domain.AnuncioImagem;
import br.com.zyclos.domain.Avaliacao;
import br.com.zyclos.domain.Categoria;
import br.com.zyclos.domain.Denuncia;
import br.com.zyclos.domain.Mensagem;
import br.com.zyclos.domain.Notificacao;
import br.com.zyclos.domain.PreferenciasUsuario;
import br.com.zyclos.domain.PropostaTroca;
import br.com.zyclos.domain.Usuario;
import br.com.zyclos.dto.response.AnuncioDetalheResponse;
import br.com.zyclos.dto.response.AnuncioResumoResponse;
import br.com.zyclos.dto.response.AutorResponse;
import br.com.zyclos.dto.response.AvaliacaoResponse;
import br.com.zyclos.dto.response.CategoriaResponse;
import br.com.zyclos.dto.response.DenunciaResponse;
import br.com.zyclos.dto.response.MensagemResponse;
import br.com.zyclos.dto.response.NotificacaoResponse;
import br.com.zyclos.dto.response.PreferenciasResponse;
import br.com.zyclos.dto.response.PropostaTrocaResponse;
import br.com.zyclos.dto.response.UsuarioResponse;
import br.com.zyclos.dto.response.VendedorResumoResponse;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Traduz entidades do dominio para os objetos devolvidos pela API. */
public final class Conversores {

    private Conversores() {
    }

    public static UsuarioResponse conta(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getAvatarUrl(),
                usuario.getBio(),
                usuario.localizacao(),
                usuario.getPapel(),
                usuario.getStatus(),
                usuario.isVerificado(),
                usuario.getCriadoEm());
    }

    public static AutorResponse autor(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return new AutorResponse(usuario.getId(), usuario.getNome(), usuario.getUsername(), usuario.getAvatarUrl());
    }

    public static VendedorResumoResponse vendedor(Usuario usuario, Double media, long totalAvaliacoes) {
        return new VendedorResumoResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getUsername(),
                usuario.getAvatarUrl(),
                usuario.localizacao(),
                usuario.isVerificado(),
                media,
                totalAvaliacoes);
    }

    public static AnuncioResumoResponse resumo(Anuncio anuncio, Set<Long> favoritos) {
        return new AnuncioResumoResponse(
                anuncio.getId(),
                anuncio.getTitulo(),
                anuncio.getPreco(),
                anuncio.imagemPrincipal(),
                anuncio.getCategoria().getNome(),
                anuncio.getMarca(),
                anuncio.getTamanho(),
                anuncio.getCondicao(),
                anuncio.localizacao(),
                anuncio.getTipoNegociacao(),
                anuncio.getStatus(),
                autor(anuncio.getVendedor()),
                favoritos != null && favoritos.contains(anuncio.getId()),
                anuncio.getCriadoEm());
    }

    public static AnuncioDetalheResponse detalhe(Anuncio anuncio, boolean favorito, long totalFavoritos,
                                                 Usuario solicitante, Double mediaDoVendedor,
                                                 long avaliacoesDoVendedor) {
        // As duas colecoes sao carregadas sob demanda. Copiar aqui, ainda dentro da transacao,
        // evita que a serializacao da resposta tente inicializa-las com a sessao ja fechada.
        List<String> imagens = anuncio.getImagens().stream().map(AnuncioImagem::getUrl).toList();
        Set<String> tags = new LinkedHashSet<>(anuncio.getTags());
        return new AnuncioDetalheResponse(
                anuncio.getId(),
                anuncio.getTitulo(),
                anuncio.getDescricao(),
                anuncio.getPreco(),
                imagens,
                anuncio.getCategoria().getNome(),
                anuncio.getMarca(),
                anuncio.getTamanho(),
                anuncio.getCondicao(),
                anuncio.getEstilo(),
                anuncio.localizacao(),
                anuncio.getTipoNegociacao(),
                anuncio.getStatus(),
                tags,
                anuncio.getVisualizacoes(),
                totalFavoritos,
                favorito,
                anuncio.pertenceA(solicitante),
                vendedor(anuncio.getVendedor(), mediaDoVendedor, avaliacoesDoVendedor),
                anuncio.getCriadoEm(),
                anuncio.getAtualizadoEm());
    }

    public static CategoriaResponse categoria(Categoria categoria, long totalAnuncios) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getSlug(),
                categoria.isAtiva(),
                totalAnuncios,
                categoria.getCriadoEm());
    }

    public static MensagemResponse mensagem(Mensagem mensagem, Usuario leitor) {
        boolean propria = leitor != null
                && Objects.equals(mensagem.getRemetente().getId(), leitor.getId());
        return new MensagemResponse(
                mensagem.getId(),
                mensagem.getConteudo(),
                autor(mensagem.getRemetente()),
                propria,
                mensagem.isLida(),
                mensagem.getEnviadaEm());
    }

    public static NotificacaoResponse notificacao(Notificacao notificacao) {
        return new NotificacaoResponse(
                notificacao.getId(),
                notificacao.getTexto(),
                notificacao.getTipo(),
                notificacao.getDestino(),
                notificacao.isLida(),
                notificacao.getCriadoEm());
    }

    public static AvaliacaoResponse avaliacao(Avaliacao avaliacao) {
        return new AvaliacaoResponse(
                avaliacao.getId(),
                autor(avaliacao.getAutor()),
                avaliacao.getNota(),
                avaliacao.getComentario(),
                avaliacao.getCriadoEm());
    }

    public static PropostaTrocaResponse proposta(PropostaTroca proposta, Usuario solicitante, Set<Long> favoritos) {
        boolean souProponente = solicitante != null
                && Objects.equals(proposta.getProponente().getId(), solicitante.getId());
        return new PropostaTrocaResponse(
                proposta.getId(),
                autor(proposta.getProponente()),
                autor(proposta.getDestinatario()),
                resumo(proposta.getAnuncioDesejado(), favoritos),
                resumo(proposta.getAnuncioOferecido(), favoritos),
                proposta.getMensagem(),
                proposta.getStatus(),
                souProponente,
                proposta.getCriadoEm(),
                proposta.getAtualizadoEm());
    }

    public static DenunciaResponse denuncia(Denuncia denuncia) {
        return new DenunciaResponse(
                denuncia.getId(),
                autor(denuncia.getDenunciante()),
                denuncia.getAnuncio() == null ? null : denuncia.getAnuncio().getTitulo(),
                denuncia.getAnuncio() == null ? null : denuncia.getAnuncio().getId(),
                autor(denuncia.getUsuarioDenunciado()),
                denuncia.getMotivo(),
                denuncia.getDescricao(),
                denuncia.getStatus(),
                denuncia.getRespostaModeracao(),
                denuncia.getCriadoEm(),
                denuncia.getResolvidoEm());
    }

    public static PreferenciasResponse preferencias(PreferenciasUsuario preferencias) {
        if (preferencias == null) {
            return new PreferenciasResponse(true, true, true, false);
        }
        return new PreferenciasResponse(
                preferencias.isReceberNotificacoes(),
                preferencias.isReceberMensagens(),
                preferencias.isPerfilPublico(),
                preferencias.isReceberNovidades());
    }
}
