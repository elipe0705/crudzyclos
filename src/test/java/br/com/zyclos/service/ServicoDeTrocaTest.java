package br.com.zyclos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.zyclos.TesteDeIntegracao;
import br.com.zyclos.domain.Anuncio;
import br.com.zyclos.domain.Categoria;
import br.com.zyclos.domain.Condicao;
import br.com.zyclos.domain.StatusAnuncio;
import br.com.zyclos.domain.StatusProposta;
import br.com.zyclos.domain.TipoNegociacao;
import br.com.zyclos.domain.Usuario;
import br.com.zyclos.dto.request.PropostaTrocaRequest;
import br.com.zyclos.dto.response.PropostaTrocaResponse;
import br.com.zyclos.exception.ConflitoException;
import br.com.zyclos.exception.OperacaoNaoPermitidaException;
import br.com.zyclos.exception.RegraDeNegocioException;
import br.com.zyclos.repository.AnuncioRepository;
import br.com.zyclos.repository.CategoriaRepository;
import br.com.zyclos.repository.UsuarioRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@TesteDeIntegracao
@DisplayName("Regras das propostas de troca")
class ServicoDeTrocaTest {

    @Autowired
    private ServicoDeTroca trocas;

    @Autowired
    private UsuarioRepository usuarios;

    @Autowired
    private CategoriaRepository categorias;

    @Autowired
    private AnuncioRepository anuncios;

    private Usuario dona;
    private Usuario interessada;
    private Anuncio pecaDaDona;
    private Anuncio pecaDaInteressada;

    @BeforeEach
    void prepararCenario() {
        Categoria jaquetas = categorias.save(new Categoria("Jaquetas", "jaquetas"));
        dona = usuarios.save(new Usuario("Dona da Peca", "dona.peca", "dona@exemplo.com", "hash"));
        interessada = usuarios.save(
                new Usuario("Pessoa Interessada", "interessada", "interessada@exemplo.com", "hash"));

        pecaDaDona = criarAnuncio("Jaqueta oliva", dona, jaquetas, TipoNegociacao.TROCA, null);
        pecaDaInteressada = criarAnuncio("Jaqueta jeans", interessada, jaquetas,
                TipoNegociacao.VENDA_OU_TROCA, new BigDecimal("160.00"));
    }

    @Test
    @DisplayName("registra a proposta como pendente e aponta o dono da peca como destinatario")
    void registraPropostaPendente() {
        PropostaTrocaResponse proposta = propor();

        assertThat(proposta.status()).isEqualTo(StatusProposta.PENDENTE);
        assertThat(proposta.destinatario().username()).isEqualTo("dona.peca");
        assertThat(proposta.souProponente()).isTrue();
        assertThat(trocas.recebidas(dona)).hasSize(1);
    }

    @Test
    @DisplayName("aceitar a proposta marca as duas pecas como negociadas")
    void aceitarReservaAsPecas() {
        PropostaTrocaResponse proposta = propor();

        PropostaTrocaResponse aceita = trocas.aceitar(dona, proposta.id());

        assertThat(aceita.status()).isEqualTo(StatusProposta.ACEITA);
        assertThat(anuncios.findById(pecaDaDona.getId()).orElseThrow().getStatus())
                .isEqualTo(StatusAnuncio.NEGOCIADO);
        assertThat(anuncios.findById(pecaDaInteressada.getId()).orElseThrow().getStatus())
                .isEqualTo(StatusAnuncio.NEGOCIADO);
    }

    @Test
    @DisplayName("somente quem recebeu a proposta pode aceita-la")
    void proponenteNaoAceitaPropriaProposta() {
        PropostaTrocaResponse proposta = propor();

        assertThatThrownBy(() -> trocas.aceitar(interessada, proposta.id()))
                .isInstanceOf(OperacaoNaoPermitidaException.class);
    }

    @Test
    @DisplayName("nao aceita uma segunda proposta pendente para a mesma peca")
    void bloqueiaPropostaDuplicada() {
        propor();

        assertThatThrownBy(this::propor).isInstanceOf(ConflitoException.class);
    }

    @Test
    @DisplayName("recusa proposta sobre peca que esta apenas a venda")
    void recusaPecaSemTroca() {
        pecaDaDona.setTipoNegociacao(TipoNegociacao.VENDA);
        pecaDaDona.setPreco(new BigDecimal("200.00"));
        anuncios.save(pecaDaDona);

        assertThatThrownBy(this::propor)
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("nao esta aberta para troca");
    }

    @Test
    @DisplayName("recusa oferecer uma peca que pertence a outra pessoa")
    void recusaOferecerPecaDeTerceiro() {
        Anuncio outraPecaDaDona = criarAnuncio("Moletom cinza", dona,
                pecaDaDona.getCategoria(), TipoNegociacao.VENDA_OU_TROCA, new BigDecimal("90.00"));
        PropostaTrocaRequest pedido = new PropostaTrocaRequest(
                pecaDaDona.getId(), outraPecaDaDona.getId(), "Troca comigo?");

        assertThatThrownBy(() -> trocas.propor(interessada, pedido))
                .isInstanceOf(OperacaoNaoPermitidaException.class)
                .hasMessageContaining("pecas que anunciou");
    }

    @Test
    @DisplayName("recusa uma proposta que oferece e pede a mesma peca")
    void recusaPecasIguais() {
        PropostaTrocaRequest pedido = new PropostaTrocaRequest(
                pecaDaDona.getId(), pecaDaDona.getId(), "Troca comigo?");

        assertThatThrownBy(() -> trocas.propor(interessada, pedido))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("duas pecas diferentes");
    }

    @Test
    @DisplayName("a proposta recusada sai das abas abertas e entra no historico")
    void recusarMovimentaParaHistorico() {
        PropostaTrocaResponse proposta = propor();

        trocas.recusar(dona, proposta.id());

        assertThat(trocas.recebidas(dona)).isEmpty();
        assertThat(trocas.enviadas(interessada)).isEmpty();
        assertThat(trocas.historico(interessada)).hasSize(1);
    }

    private PropostaTrocaResponse propor() {
        return trocas.propor(interessada, new PropostaTrocaRequest(
                pecaDaDona.getId(), pecaDaInteressada.getId(), "Topa trocar?"));
    }

    private Anuncio criarAnuncio(String titulo, Usuario vendedor, Categoria categoria,
                                 TipoNegociacao negociacao, BigDecimal preco) {
        Anuncio anuncio = new Anuncio(titulo, "Peca de teste.", categoria, vendedor);
        anuncio.setTamanho("M");
        anuncio.setCondicao(Condicao.BOM);
        anuncio.setTipoNegociacao(negociacao);
        anuncio.setPreco(preco);
        return anuncios.save(anuncio);
    }
}
