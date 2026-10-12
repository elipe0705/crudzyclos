package br.com.zyclos.service;

import br.com.zyclos.domain.Categoria;
import br.com.zyclos.domain.StatusAnuncio;
import br.com.zyclos.dto.request.CategoriaRequest;
import br.com.zyclos.dto.response.CategoriaResponse;
import br.com.zyclos.exception.ConflitoException;
import br.com.zyclos.exception.RecursoNaoEncontradoException;
import br.com.zyclos.exception.RegraDeNegocioException;
import br.com.zyclos.mapper.Conversores;
import br.com.zyclos.repository.AnuncioRepository;
import br.com.zyclos.repository.CategoriaRepository;
import br.com.zyclos.repository.EspecificacoesDeAnuncio;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Consulta e manutencao das categorias do catalogo. */
@Service
public class ServicoDeCategoria {

    private final CategoriaRepository categorias;
    private final AnuncioRepository anuncios;

    public ServicoDeCategoria(CategoriaRepository categorias, AnuncioRepository anuncios) {
        this.categorias = categorias;
        this.anuncios = anuncios;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarAtivas() {
        return categorias.findAllByAtivaTrueOrderByNomeAsc().stream()
                .map(categoria -> Conversores.categoria(categoria, contar(categoria)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarTodas() {
        return categorias.findAllByOrderByNomeAsc().stream()
                .map(categoria -> Conversores.categoria(categoria, contar(categoria)))
                .toList();
    }

    @Transactional(readOnly = true)
    public Categoria porNome(String nome) {
        return categorias.findByNomeIgnoreCase(nome)
                .or(() -> categorias.findBySlugIgnoreCase(gerarSlug(nome)))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria inexistente: " + nome));
    }

    @Transactional
    public CategoriaResponse criar(CategoriaRequest pedido) {
        String nome = pedido.nome().trim();
        if (categorias.existsByNomeIgnoreCase(nome)) {
            throw new ConflitoException("Ja existe uma categoria com este nome.");
        }
        Categoria categoria = new Categoria(nome, gerarSlug(nome));
        if (pedido.ativa() != null) {
            categoria.setAtiva(pedido.ativa());
        }
        return Conversores.categoria(categorias.save(categoria), 0);
    }

    @Transactional
    public CategoriaResponse atualizar(Long id, CategoriaRequest pedido) {
        Categoria categoria = categorias.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Categoria", id));
        String nome = pedido.nome().trim();
        categorias.findByNomeIgnoreCase(nome)
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> {
                    throw new ConflitoException("Ja existe uma categoria com este nome.");
                });
        categoria.setNome(nome);
        categoria.setSlug(gerarSlug(nome));
        if (pedido.ativa() != null) {
            categoria.setAtiva(pedido.ativa());
        }
        return Conversores.categoria(categorias.save(categoria), contar(categoria));
    }

    @Transactional
    public void remover(Long id) {
        Categoria categoria = categorias.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Categoria", id));
        long emUso = contar(categoria);
        if (emUso > 0) {
            throw new RegraDeNegocioException(
                    "Esta categoria tem " + emUso + " anuncio(s) ativo(s) e nao pode ser removida. Desative-a.");
        }
        categorias.delete(categoria);
    }

    private long contar(Categoria categoria) {
        return anuncios.count(EspecificacoesDeAnuncio.naCategoria(categoria.getNome())
                .and(EspecificacoesDeAnuncio.comStatus(StatusAnuncio.ATIVO)));
    }

    /** Converte o nome em um identificador de URL, por exemplo Tenis vira tenis. */
    public static String gerarSlug(String nome) {
        String semAcento = Normalizer.normalize(nome, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return semAcento.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
