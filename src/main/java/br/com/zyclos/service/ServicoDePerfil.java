package br.com.zyclos.service;

import br.com.zyclos.domain.Avaliacao;
import br.com.zyclos.domain.Seguidor;
import br.com.zyclos.domain.StatusAnuncio;
import br.com.zyclos.domain.Usuario;
import br.com.zyclos.dto.request.AtualizarPerfilRequest;
import br.com.zyclos.dto.request.AvaliacaoRequest;
import br.com.zyclos.dto.request.PreferenciasRequest;
import br.com.zyclos.dto.response.AvaliacaoResponse;
import br.com.zyclos.dto.response.PerfilResponse;
import br.com.zyclos.dto.response.PreferenciasResponse;
import br.com.zyclos.dto.response.UsuarioResponse;
import br.com.zyclos.domain.PreferenciasUsuario;
import br.com.zyclos.exception.ConflitoException;
import br.com.zyclos.exception.RecursoNaoEncontradoException;
import br.com.zyclos.exception.RegraDeNegocioException;
import br.com.zyclos.mapper.Conversores;
import br.com.zyclos.repository.AnuncioRepository;
import br.com.zyclos.repository.AvaliacaoRepository;
import br.com.zyclos.repository.SeguidorRepository;
import br.com.zyclos.repository.UsuarioRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Perfil publico, seguidores, avaliacoes e preferencias da conta. */
@Service
public class ServicoDePerfil {

    private final UsuarioRepository usuarios;
    private final AnuncioRepository anuncios;
    private final SeguidorRepository seguidores;
    private final AvaliacaoRepository avaliacoes;
    private final ServicoDeAnuncio servicoDeAnuncio;
    private final ServicoDeNotificacao notificacoes;

    public ServicoDePerfil(UsuarioRepository usuarios, AnuncioRepository anuncios,
                           SeguidorRepository seguidores, AvaliacaoRepository avaliacoes,
                           ServicoDeAnuncio servicoDeAnuncio, ServicoDeNotificacao notificacoes) {
        this.usuarios = usuarios;
        this.anuncios = anuncios;
        this.seguidores = seguidores;
        this.avaliacoes = avaliacoes;
        this.servicoDeAnuncio = servicoDeAnuncio;
        this.notificacoes = notificacoes;
    }

    @Transactional(readOnly = true)
    public Usuario porUsername(String username) {
        return usuarios.findByUsernameIgnoreCase(username.trim())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Perfil inexistente: " + username));
    }

    @Transactional(readOnly = true)
    public PerfilResponse perfil(String username, Usuario solicitante) {
        Usuario dono = porUsername(username);
        boolean proprio = solicitante != null && Objects.equals(dono.getId(), solicitante.getId());
        return new PerfilResponse(
                dono.getId(),
                dono.getNome(),
                dono.getUsername(),
                dono.getAvatarUrl(),
                dono.getBio(),
                dono.localizacao(),
                dono.isVerificado(),
                avaliacoes.mediaDoPerfil(dono),
                avaliacoes.countByAvaliado(dono),
                anuncios.countByVendedorAndStatus(dono, StatusAnuncio.ATIVO),
                seguidores.countBySeguido(dono),
                seguidores.countBySeguidor(dono),
                solicitante != null && !proprio && seguidores.existsBySeguidorAndSeguido(solicitante, dono),
                proprio,
                dono.getCriadoEm(),
                servicoDeAnuncio.doVendedor(dono, solicitante));
    }

    @Transactional
    public UsuarioResponse atualizar(Usuario usuario, AtualizarPerfilRequest pedido) {
        if (pedido.username() != null && !pedido.username().isBlank()) {
            String novo = pedido.username().trim().toLowerCase();
            usuarios.findByUsernameIgnoreCase(novo)
                    .filter(outro -> !outro.getId().equals(usuario.getId()))
                    .ifPresent(outro -> {
                        throw new ConflitoException("Este nome de usuario ja esta em uso.");
                    });
            usuario.setUsername(novo);
        }
        if (pedido.nome() != null && !pedido.nome().isBlank()) {
            usuario.setNome(pedido.nome().trim());
        }
        if (pedido.bio() != null) {
            usuario.setBio(pedido.bio().isBlank() ? null : pedido.bio().trim());
        }
        if (pedido.cidade() != null) {
            usuario.setCidade(pedido.cidade().isBlank() ? null : pedido.cidade().trim());
        }
        if (pedido.estado() != null) {
            usuario.setEstado(pedido.estado().isBlank() ? null : pedido.estado().trim().toUpperCase());
        }
        if (pedido.avatarUrl() != null) {
            usuario.setAvatarUrl(pedido.avatarUrl().isBlank() ? null : pedido.avatarUrl().trim());
        }
        return Conversores.conta(usuarios.save(usuario));
    }

    /** Liga ou desliga o acompanhamento e devolve o estado resultante. */
    @Transactional
    public boolean alternarSeguir(Usuario solicitante, String username) {
        Usuario alvo = porUsername(username);
        if (Objects.equals(alvo.getId(), solicitante.getId())) {
            throw new RegraDeNegocioException("Voce nao pode seguir o proprio perfil.");
        }
        return seguidores.findBySeguidorAndSeguido(solicitante, alvo)
                .map(existente -> {
                    seguidores.delete(existente);
                    return false;
                })
                .orElseGet(() -> {
                    seguidores.save(new Seguidor(solicitante, alvo));
                    notificacoes.novoSeguidor(alvo, solicitante);
                    return true;
                });
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoResponse> avaliacoesDe(String username) {
        return avaliacoes.findByAvaliadoOrderByCriadoEmDesc(porUsername(username)).stream()
                .map(Conversores::avaliacao)
                .toList();
    }

    @Transactional
    public AvaliacaoResponse avaliar(Usuario autor, String username, AvaliacaoRequest pedido) {
        Usuario avaliado = porUsername(username);
        if (Objects.equals(avaliado.getId(), autor.getId())) {
            throw new RegraDeNegocioException("Voce nao pode avaliar o proprio perfil.");
        }
        if (avaliacoes.existsByAutorAndAvaliado(autor, avaliado)) {
            throw new ConflitoException("Voce ja avaliou este perfil.");
        }
        Avaliacao avaliacao = new Avaliacao(autor, avaliado, pedido.nota(), pedido.comentario());
        avaliacoes.save(avaliacao);
        notificacoes.novaAvaliacao(avaliado, autor);
        return Conversores.avaliacao(avaliacao);
    }

    @Transactional(readOnly = true)
    public PreferenciasResponse preferencias(Usuario usuario) {
        return Conversores.preferencias(usuario.getPreferencias());
    }

    @Transactional
    public PreferenciasResponse salvarPreferencias(Usuario usuario, PreferenciasRequest pedido) {
        PreferenciasUsuario preferencias = usuario.getPreferencias();
        if (preferencias == null) {
            preferencias = new PreferenciasUsuario(usuario);
            usuario.setPreferencias(preferencias);
        }
        preferencias.setReceberNotificacoes(pedido.receberNotificacoes());
        preferencias.setReceberMensagens(pedido.receberMensagens());
        preferencias.setPerfilPublico(pedido.perfilPublico());
        preferencias.setReceberNovidades(pedido.receberNovidades());
        usuarios.save(usuario);
        return Conversores.preferencias(preferencias);
    }
}
