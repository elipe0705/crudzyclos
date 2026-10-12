package br.com.zyclos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.zyclos.TesteDeIntegracao;
import br.com.zyclos.domain.Categoria;
import br.com.zyclos.domain.Condicao;
import br.com.zyclos.domain.StatusAnuncio;
import br.com.zyclos.domain.TipoNegociacao;
import br.com.zyclos.domain.Usuario;
import br.com.zyclos.dto.request.AnuncioRequest;
import br.com.zyclos.dto.response.AnuncioResumoResponse;
import br.com.zyclos.dto.response.PaginaResponse;
import br.com.zyclos.exception.OperacaoNaoPermitidaException;
import br.com.zyclos.exception.RegraDeNegocioException;
import br.com.zyclos.repository.CategoriaRepository;
import br.com.zyclos.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TesteDeIntegracao
@DisplayName("Publicacao e busca de anuncios")
class ServicoDeAnuncioTest {

    @Autowired
    private ServicoDeAnuncio anuncios;

    @Autowired
    private ServicoDeFavorito favoritos;

    @Autowired
    private UsuarioRepository usuarios;

    @Autowired
    private CategoriaRepository categorias;

    private Usuario vendedora;
    private Usuario visitante;

    @BeforeEach
    void prepararCenario() {
        categorias.save(new Categoria("Jaquetas", "jaquetas"));
        categorias.save(new Categoria("Tenis", "tenis"));
        vendedora = usuarios.save(new Usuario("Vendedora", "vendedora", "vendedora@exemplo.com", "hash"));
        visitante = usuarios.save(new Usuario("Visitante", "visitante", "visitante@exemplo.com", "hash"));
    }

    @Test
    @DisplayName("publica a peca com a primeira foto como capa")
    void publicaComCapa() {
        var criado = anuncios.criar(vendedora, pedido("Jaqueta jeans", "Jaquetas",
                TipoNegociacao.VENDA, new BigDecimal("150.00"),
                List.of("/api/imagens/um.jpg", "/api/imagens/dois.jpg")));

        assertThat(criado.imagens()).containsExactly("/api/imagens/um.jpg", "/api/imagens/dois.jpg");
        assertThat(criado.status()).isEqualTo(StatusAnuncio.ATIVO);
        assertThat(criado.meuAnuncio()).isTrue();
    }

    @Test
    @DisplayName("exige preco quando a negociacao inclui venda e zera o preco na troca pura")
    void tratamentoDoPreco() {
        assertThatThrownBy(() -> anuncios.criar(vendedora,
                pedido("Sem preco", "Jaquetas", TipoNegociacao.VENDA, null, List.of())))
                .isInstanceOf(RegraDeNegocioException.class);

        var somenteTroca = anuncios.criar(vendedora,
                pedido("So troca", "Jaquetas", TipoNegociacao.TROCA, new BigDecimal("99.00"), List.of()));

        assertThat(somenteTroca.preco()).isNull();
    }

    @Test
    @DisplayName("filtra o catalogo por categoria, marca e tipo de negociacao")
    void filtraCatalogo() {
        anuncios.criar(vendedora, pedido("Jaqueta jeans", "Jaquetas",
                TipoNegociacao.VENDA, new BigDecimal("150.00"), List.of()));
        anuncios.criar(vendedora, pedido("Tenis retro", "Tenis",
                TipoNegociacao.TROCA, null, List.of()));

        PaginaResponse<AnuncioResumoResponse> porCategoria = buscar(
                new FiltroDeAnuncio(null, "Tenis", null, null, null, null, null, null, null, null));
        PaginaResponse<AnuncioResumoResponse> porNegociacao = buscar(
                new FiltroDeAnuncio(null, null, TipoNegociacao.VENDA, null, null, null, null, null, null, null));
        PaginaResponse<AnuncioResumoResponse> porTexto = buscar(
                new FiltroDeAnuncio("jeans", null, null, null, null, null, null, null, null, null));

        assertThat(porCategoria.total()).isEqualTo(1);
        assertThat(porCategoria.itens().get(0).titulo()).isEqualTo("Tenis retro");
        assertThat(porNegociacao.total()).isEqualTo(1);
        assertThat(porTexto.itens()).singleElement()
                .extracting(AnuncioResumoResponse::titulo).isEqualTo("Jaqueta jeans");
    }

    @Test
    @DisplayName("ordena por menor e por maior preco")
    void ordenaPorPreco() {
        anuncios.criar(vendedora, pedido("Mais cara", "Jaquetas",
                TipoNegociacao.VENDA, new BigDecimal("300.00"), List.of()));
        anuncios.criar(vendedora, pedido("Mais barata", "Jaquetas",
                TipoNegociacao.VENDA, new BigDecimal("80.00"), List.of()));

        var crescente = buscar(new FiltroDeAnuncio(null, null, null, null, null, null, null, null, null, "menor"));
        var decrescente = buscar(new FiltroDeAnuncio(null, null, null, null, null, null, null, null, null, "maior"));

        assertThat(crescente.itens().get(0).titulo()).isEqualTo("Mais barata");
        assertThat(decrescente.itens().get(0).titulo()).isEqualTo("Mais cara");
    }

    @Test
    @DisplayName("o catalogo marca como favorito apenas as pecas salvas por quem consulta")
    void marcaFavoritoDoSolicitante() {
        var criado = anuncios.criar(vendedora, pedido("Jaqueta jeans", "Jaquetas",
                TipoNegociacao.VENDA, new BigDecimal("150.00"), List.of()));
        favoritos.alternar(visitante, criado.id());

        var paraVisitante = anuncios.buscar(filtroVazio(), 0, 10, visitante);
        var paraAnonimo = anuncios.buscar(filtroVazio(), 0, 10, null);

        assertThat(paraVisitante.itens().get(0).favorito()).isTrue();
        assertThat(paraAnonimo.itens().get(0).favorito()).isFalse();
    }

    @Test
    @DisplayName("a remocao tira a peca do catalogo sem apagar o registro")
    void remocaoEhLogica() {
        var criado = anuncios.criar(vendedora, pedido("Jaqueta jeans", "Jaquetas",
                TipoNegociacao.VENDA, new BigDecimal("150.00"), List.of()));

        anuncios.remover(vendedora, criado.id());

        assertThat(buscar(filtroVazio()).total()).isZero();
        assertThat(anuncios.porId(criado.id()).getStatus()).isEqualTo(StatusAnuncio.REMOVIDO);
    }

    @Test
    @DisplayName("impede que outra pessoa edite a peca")
    void impedeEdicaoDeTerceiro() {
        var criado = anuncios.criar(vendedora, pedido("Jaqueta jeans", "Jaquetas",
                TipoNegociacao.VENDA, new BigDecimal("150.00"), List.of()));
        AnuncioRequest alteracao = pedido("Titulo roubado", "Jaquetas",
                TipoNegociacao.VENDA, new BigDecimal("10.00"), List.of());

        assertThatThrownBy(() -> anuncios.atualizar(visitante, criado.id(), alteracao))
                .isInstanceOf(OperacaoNaoPermitidaException.class);
    }

    private PaginaResponse<AnuncioResumoResponse> buscar(FiltroDeAnuncio filtro) {
        return anuncios.buscar(filtro, 0, 10, null);
    }

    private static FiltroDeAnuncio filtroVazio() {
        return new FiltroDeAnuncio(null, null, null, null, null, null, null, null, null, null);
    }

    private static AnuncioRequest pedido(String titulo, String categoria, TipoNegociacao negociacao,
                                         BigDecimal preco, List<String> imagens) {
        return new AnuncioRequest(titulo, "Peca de teste em bom estado.", preco, categoria,
                "Levis", "M", Condicao.BOM, "Casual", "Sao Paulo", "SP", negociacao,
                Set.of("teste"), imagens);
    }
}
