package br.com.zyclos.service;

import br.com.zyclos.domain.Anuncio;
import br.com.zyclos.domain.Favorito;
import br.com.zyclos.domain.Usuario;
import br.com.zyclos.dto.response.AnuncioResumoResponse;
import br.com.zyclos.mapper.Conversores;
import br.com.zyclos.repository.AnuncioRepository;
import br.com.zyclos.repository.FavoritoRepository;
import br.com.zyclos.exception.RecursoNaoEncontradoException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Pecas salvas pelo usuario na pagina de favoritos. */
@Service
public class ServicoDeFavorito {

    private final FavoritoRepository favoritos;
    private final AnuncioRepository anuncios;
    private final ServicoDeNotificacao notificacoes;

    public ServicoDeFavorito(FavoritoRepository favoritos, AnuncioRepository anuncios,
                             ServicoDeNotificacao notificacoes) {
        this.favoritos = favoritos;
        this.anuncios = anuncios;
        this.notificacoes = notificacoes;
    }

    @Transactional(readOnly = true)
    public List<AnuncioResumoResponse> listar(Usuario usuario) {
        Set<Long> marcados = new HashSet<>(favoritos.idsDosAnunciosFavoritados(usuario));
        return favoritos.findByUsuarioOrderByCriadoEmDesc(usuario).stream()
                .map(Favorito::getAnuncio)
                .map(anuncio -> Conversores.resumo(anuncio, marcados))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Long> ids(Usuario usuario) {
        return favoritos.idsDosAnunciosFavoritados(usuario);
    }

    /** Liga ou desliga o favorito e devolve o estado resultante. */
    @Transactional
    public boolean alternar(Usuario usuario, Long anuncioId) {
        Anuncio anuncio = anuncios.findById(anuncioId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Anuncio", anuncioId));

        return favoritos.findByUsuarioAndAnuncio(usuario, anuncio)
                .map(existente -> {
                    favoritos.delete(existente);
                    return false;
                })
                .orElseGet(() -> {
                    favoritos.save(new Favorito(usuario, anuncio));
                    if (!anuncio.pertenceA(usuario)) {
                        notificacoes.anuncioFavoritado(anuncio.getVendedor(), anuncio.getTitulo());
                    }
                    return true;
                });
    }

    @Transactional
    public void remover(Usuario usuario, Long anuncioId) {
        Anuncio anuncio = anuncios.findById(anuncioId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Anuncio", anuncioId));
        favoritos.findByUsuarioAndAnuncio(usuario, anuncio).ifPresent(favoritos::delete);
    }
}
