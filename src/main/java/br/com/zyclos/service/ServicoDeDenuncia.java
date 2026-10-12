package br.com.zyclos.service;

import br.com.zyclos.domain.Denuncia;
import br.com.zyclos.domain.StatusDenuncia;
import br.com.zyclos.domain.Usuario;
import br.com.zyclos.dto.request.DenunciaRequest;
import br.com.zyclos.dto.request.ResolverDenunciaRequest;
import br.com.zyclos.dto.response.DenunciaResponse;
import br.com.zyclos.dto.response.PaginaResponse;
import br.com.zyclos.exception.RecursoNaoEncontradoException;
import br.com.zyclos.exception.RegraDeNegocioException;
import br.com.zyclos.mapper.Conversores;
import br.com.zyclos.repository.DenunciaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Registro e moderacao de denuncias. */
@Service
public class ServicoDeDenuncia {

    private final DenunciaRepository denuncias;
    private final ServicoDeAnuncio anuncios;
    private final ServicoDePerfil perfis;
    private final ServicoDeNotificacao notificacoes;

    public ServicoDeDenuncia(DenunciaRepository denuncias, ServicoDeAnuncio anuncios,
                             ServicoDePerfil perfis, ServicoDeNotificacao notificacoes) {
        this.denuncias = denuncias;
        this.anuncios = anuncios;
        this.perfis = perfis;
        this.notificacoes = notificacoes;
    }

    @Transactional
    public DenunciaResponse registrar(Usuario denunciante, DenunciaRequest pedido) {
        boolean semAlvo = pedido.anuncioId() == null
                && (pedido.usuarioDenunciado() == null || pedido.usuarioDenunciado().isBlank());
        if (semAlvo) {
            throw new RegraDeNegocioException("Informe o anuncio ou o perfil denunciado.");
        }

        Denuncia denuncia = new Denuncia(denunciante, pedido.motivo().trim(), pedido.descricao());
        if (pedido.anuncioId() != null) {
            denuncia.setAnuncio(anuncios.porId(pedido.anuncioId()));
        }
        if (pedido.usuarioDenunciado() != null && !pedido.usuarioDenunciado().isBlank()) {
            Usuario alvo = perfis.porUsername(pedido.usuarioDenunciado());
            if (alvo.getId().equals(denunciante.getId())) {
                throw new RegraDeNegocioException("Voce nao pode denunciar o proprio perfil.");
            }
            denuncia.setUsuarioDenunciado(alvo);
        }
        return Conversores.denuncia(denuncias.save(denuncia));
    }

    @Transactional(readOnly = true)
    public PaginaResponse<DenunciaResponse> listar(StatusDenuncia status, int pagina, int tamanho) {
        Pageable paginacao = PageRequest.of(Math.max(pagina, 0), tamanho <= 0 ? 20 : Math.min(tamanho, 100));
        var resultado = status == null
                ? denuncias.findAllByOrderByCriadoEmDesc(paginacao)
                : denuncias.findByStatusOrderByCriadoEmDesc(status, paginacao);
        return PaginaResponse.de(resultado, Conversores::denuncia);
    }

    @Transactional
    public DenunciaResponse resolver(Long id, ResolverDenunciaRequest pedido) {
        Denuncia denuncia = denuncias.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Denuncia", id));
        denuncia.resolver(pedido.status(), pedido.resposta());
        denuncias.save(denuncia);
        if (pedido.status() == StatusDenuncia.RESOLVIDA) {
            notificacoes.denunciaResolvida(denuncia.getDenunciante(), denuncia.getMotivo());
        }
        return Conversores.denuncia(denuncia);
    }
}
