package br.com.zyclos.service;

import br.com.zyclos.domain.Anuncio;
import br.com.zyclos.domain.AnuncioImagem;
import br.com.zyclos.domain.Categoria;
import br.com.zyclos.domain.Condicao;
import br.com.zyclos.domain.StatusAnuncio;
import br.com.zyclos.domain.TipoNegociacao;
import br.com.zyclos.domain.Usuario;
import br.com.zyclos.dto.request.AnuncioRequest;
import br.com.zyclos.dto.response.AnuncioDetalheResponse;
import br.com.zyclos.dto.response.AnuncioResumoResponse;
import br.com.zyclos.dto.response.FiltrosResponse;
import br.com.zyclos.dto.response.PaginaResponse;
import br.com.zyclos.exception.OperacaoNaoPermitidaException;
import br.com.zyclos.exception.RecursoNaoEncontradoException;
import br.com.zyclos.exception.RegraDeNegocioException;
import br.com.zyclos.mapper.Conversores;
import br.com.zyclos.repository.AnuncioRepository;
import br.com.zyclos.repository.AvaliacaoRepository;
import br.com.zyclos.repository.CategoriaRepository;
import br.com.zyclos.repository.EspecificacoesDeAnuncio;
import br.com.zyclos.repository.FavoritoRepository;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Publicacao, consulta e manutencao dos anuncios. */
@Service
public class ServicoDeAnuncio {

    private static final int LIMITE_DE_IMAGENS = 8;

    private final AnuncioRepository anuncios;
    private final CategoriaRepository categorias;
    private final FavoritoRepository favoritos;
    private final AvaliacaoRepository avaliacoes;
    private final ServicoDeCategoria servicoDeCategoria;

    public ServicoDeAnuncio(AnuncioRepository anuncios, CategoriaRepository categorias,
                            FavoritoRepository favoritos, AvaliacaoRepository avaliacoes,
                            ServicoDeCategoria servicoDeCategoria) {
        this.anuncios = anuncios;
        this.categorias = categorias;
        this.favoritos = favoritos;
        this.avaliacoes = avaliacoes;
        this.servicoDeCategoria = servicoDeCategoria;
    }

    @Transactional(readOnly = true)
    public PaginaResponse<AnuncioResumoResponse> buscar(FiltroDeAnuncio filtro, int pagina, int tamanho,
                                                        Usuario solicitante) {
        Specification<Anuncio> criterios = Specification
                .allOf(EspecificacoesDeAnuncio.comStatus(StatusAnuncio.ATIVO),
                        EspecificacoesDeAnuncio.contendo(filtro.q()),
                        EspecificacoesDeAnuncio.naCategoria(filtro.categoria()),
                        EspecificacoesDeAnuncio.comNegociacao(filtro.negociacao()),
                        EspecificacoesDeAnuncio.daMarca(filtro.marca()),
                        EspecificacoesDeAnuncio.doTamanho(filtro.tamanho()),
                        EspecificacoesDeAnuncio.naCondicao(filtro.condicao()),
                        EspecificacoesDeAnuncio.naCidade(filtro.cidade()),
                        EspecificacoesDeAnuncio.precoEntre(filtro.precoMinimo(), filtro.precoMaximo()));

        Pageable paginacao = PageRequest.of(
                Math.max(pagina, 0), limitar(tamanho), ordenacao(filtro.ordenarOuPadrao()));
        Page<Anuncio> resultado = anuncios.findAll(criterios, paginacao);
        Set<Long> marcados = idsFavoritos(solicitante);
        return PaginaResponse.de(resultado, anuncio -> Conversores.resumo(anuncio, marcados));
    }

    @Transactional
    public AnuncioDetalheResponse detalhar(Long id, Usuario solicitante) {
        Anuncio anuncio = anuncios.buscarComRelacionamentos(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Anuncio", id));
        if (anuncio.getStatus() == StatusAnuncio.REMOVIDO && !anuncio.pertenceA(solicitante)) {
            throw RecursoNaoEncontradoException.de("Anuncio", id);
        }
        if (!anuncio.pertenceA(solicitante)) {
            anuncio.registrarVisualizacao();
            anuncios.save(anuncio);
        }
        boolean favorito = solicitante != null && favoritos.existsByUsuarioAndAnuncio(solicitante, anuncio);
        Usuario vendedor = anuncio.getVendedor();
        return Conversores.detalhe(anuncio, favorito, favoritos.countByAnuncio(anuncio), solicitante,
                avaliacoes.mediaDoPerfil(vendedor), avaliacoes.countByAvaliado(vendedor));
    }

    @Transactional(readOnly = true)
    public List<AnuncioResumoResponse> doVendedor(Usuario vendedor, Usuario solicitante) {
        Set<Long> marcados = idsFavoritos(solicitante);
        return anuncios.findByVendedorAndStatusOrderByCriadoEmDesc(vendedor, StatusAnuncio.ATIVO).stream()
                .map(anuncio -> Conversores.resumo(anuncio, marcados))
                .toList();
    }

    @Transactional(readOnly = true)
    public PaginaResponse<AnuncioResumoResponse> meusAnuncios(Usuario vendedor, StatusAnuncio status,
                                                              int pagina, int tamanho) {
        Specification<Anuncio> criterios = Specification
                .allOf(EspecificacoesDeAnuncio.doVendedor(vendedor),
                        EspecificacoesDeAnuncio.comStatus(status));
        Pageable paginacao = PageRequest.of(
                Math.max(pagina, 0), limitar(tamanho), Sort.by(Sort.Direction.DESC, "criadoEm"));
        Set<Long> marcados = idsFavoritos(vendedor);
        return PaginaResponse.de(anuncios.findAll(criterios, paginacao),
                anuncio -> Conversores.resumo(anuncio, marcados));
    }

    @Transactional
    public AnuncioDetalheResponse criar(Usuario vendedor, AnuncioRequest pedido) {
        Categoria categoria = servicoDeCategoria.porNome(pedido.categoria());
        validarPreco(pedido);

        Anuncio anuncio = new Anuncio(
                pedido.titulo().trim(), pedido.descricao().trim(), categoria, vendedor);
        aplicar(anuncio, pedido, vendedor);
        anuncios.save(anuncio);
        return Conversores.detalhe(anuncio, false, 0, vendedor,
                avaliacoes.mediaDoPerfil(vendedor), avaliacoes.countByAvaliado(vendedor));
    }

    @Transactional
    public AnuncioDetalheResponse atualizar(Usuario solicitante, Long id, AnuncioRequest pedido) {
        Anuncio anuncio = carregarParaEdicao(solicitante, id);
        validarPreco(pedido);

        anuncio.setTitulo(pedido.titulo().trim());
        anuncio.setDescricao(pedido.descricao().trim());
        anuncio.setCategoria(servicoDeCategoria.porNome(pedido.categoria()));
        aplicar(anuncio, pedido, anuncio.getVendedor());
        anuncios.save(anuncio);

        Usuario vendedor = anuncio.getVendedor();
        return Conversores.detalhe(anuncio,
                favoritos.existsByUsuarioAndAnuncio(solicitante, anuncio),
                favoritos.countByAnuncio(anuncio), solicitante,
                avaliacoes.mediaDoPerfil(vendedor), avaliacoes.countByAvaliado(vendedor));
    }

    /** Remocao logica: o anuncio sai do catalogo mas o historico de trocas continua valido. */
    @Transactional
    public void remover(Usuario solicitante, Long id) {
        Anuncio anuncio = carregarParaEdicao(solicitante, id);
        anuncio.setStatus(StatusAnuncio.REMOVIDO);
        anuncios.save(anuncio);
    }

    @Transactional
    public AnuncioResumoResponse alterarStatus(Usuario solicitante, Long id, StatusAnuncio status) {
        Anuncio anuncio = carregarParaEdicao(solicitante, id);
        anuncio.setStatus(status);
        return Conversores.resumo(anuncios.save(anuncio), idsFavoritos(solicitante));
    }

    @Transactional(readOnly = true)
    public FiltrosResponse filtrosDisponiveis() {
        return new FiltrosResponse(
                categorias.findAllByAtivaTrueOrderByNomeAsc().stream().map(Categoria::getNome).toList(),
                anuncios.marcasDisponiveis(),
                anuncios.tamanhosDisponiveis(),
                Arrays.stream(Condicao.values()).map(Condicao::getRotulo).toList(),
                Arrays.stream(TipoNegociacao.values()).map(TipoNegociacao::getRotulo).toList());
    }

    /** Carrega um anuncio garantindo que o solicitante pode altera-lo. */
    @Transactional(readOnly = true)
    public Anuncio carregarParaEdicao(Usuario solicitante, Long id) {
        Anuncio anuncio = anuncios.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Anuncio", id));
        if (!anuncio.pertenceA(solicitante) && !solicitante.isAdmin()) {
            throw new OperacaoNaoPermitidaException("Este anuncio pertence a outro usuario.");
        }
        return anuncio;
    }

    @Transactional(readOnly = true)
    public Anuncio porId(Long id) {
        return anuncios.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Anuncio", id));
    }

    private void aplicar(Anuncio anuncio, AnuncioRequest pedido, Usuario vendedor) {
        anuncio.setMarca(textoOuNulo(pedido.marca()));
        anuncio.setTamanho(pedido.tamanho().trim());
        anuncio.setCondicao(pedido.condicao());
        anuncio.setEstilo(textoOuNulo(pedido.estilo()));
        anuncio.setTipoNegociacao(pedido.tipoNegociacao());
        anuncio.setPreco(pedido.tipoNegociacao() == TipoNegociacao.TROCA ? null : pedido.preco());
        anuncio.setCidade(pedido.cidade() == null || pedido.cidade().isBlank()
                ? vendedor.getCidade() : pedido.cidade().trim());
        anuncio.setEstado(pedido.estado() == null || pedido.estado().isBlank()
                ? vendedor.getEstado() : pedido.estado().trim().toUpperCase());
        anuncio.setTags(pedido.tags());

        if (pedido.imagens() != null) {
            if (pedido.imagens().size() > LIMITE_DE_IMAGENS) {
                throw new RegraDeNegocioException("Envie no maximo " + LIMITE_DE_IMAGENS + " fotos por anuncio.");
            }
            anuncio.limparImagens();
            pedido.imagens().stream()
                    .filter(url -> url != null && !url.isBlank())
                    .forEach(url -> anuncio.adicionarImagem(new AnuncioImagem(url.trim())));
        }
    }

    private void validarPreco(AnuncioRequest pedido) {
        if (pedido.tipoNegociacao().exigePreco() && pedido.preco() == null) {
            throw new RegraDeNegocioException("Informe o preco para anuncios de venda.");
        }
    }

    private Set<Long> idsFavoritos(Usuario usuario) {
        return usuario == null ? Set.of() : new HashSet<>(favoritos.idsDosAnunciosFavoritados(usuario));
    }

    private static String textoOuNulo(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private static int limitar(int tamanho) {
        if (tamanho <= 0) {
            return 12;
        }
        return Math.min(tamanho, 60);
    }

    /** Traduz o parametro ordenar usado pela tela de catalogo em uma ordenacao JPA. */
    private static Sort ordenacao(String ordenar) {
        return switch (ordenar.toLowerCase()) {
            case "menor", "preco-asc" -> Sort.by(Sort.Direction.ASC, "preco");
            case "maior", "preco-desc" -> Sort.by(Sort.Direction.DESC, "preco");
            case "relevantes" -> Sort.by(Sort.Direction.DESC, "visualizacoes").and(
                    Sort.by(Sort.Direction.DESC, "criadoEm"));
            default -> Sort.by(Sort.Direction.DESC, "criadoEm");
        };
    }
}
