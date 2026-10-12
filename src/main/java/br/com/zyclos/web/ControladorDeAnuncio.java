package br.com.zyclos.web;

import br.com.zyclos.domain.Condicao;
import br.com.zyclos.domain.StatusAnuncio;
import br.com.zyclos.domain.TipoNegociacao;
import br.com.zyclos.domain.Usuario;
import br.com.zyclos.dto.request.AnuncioRequest;
import br.com.zyclos.dto.request.StatusAnuncioRequest;
import br.com.zyclos.dto.response.AnuncioDetalheResponse;
import br.com.zyclos.dto.response.AnuncioResumoResponse;
import br.com.zyclos.dto.response.FiltrosResponse;
import br.com.zyclos.dto.response.PaginaResponse;
import br.com.zyclos.security.UsuarioAutenticado;
import br.com.zyclos.service.FiltroDeAnuncio;
import br.com.zyclos.service.ServicoDeAnuncio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Catalogo publico e manutencao dos proprios anuncios. */
@RestController
@RequestMapping("/api")
@Tag(name = "Anuncios", description = "Catalogo, publicacao e manutencao de pecas")
public class ControladorDeAnuncio {

    private final ServicoDeAnuncio anuncios;

    public ControladorDeAnuncio(ServicoDeAnuncio anuncios) {
        this.anuncios = anuncios;
    }

    @GetMapping("/anuncios")
    @SecurityRequirements
    @Operation(summary = "Lista os anuncios ativos com busca, filtros e ordenacao")
    public PaginaResponse<AnuncioResumoResponse> listar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) TipoNegociacao negociacao,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String tamanho,
            @RequestParam(required = false) Condicao condicao,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) BigDecimal precoMinimo,
            @RequestParam(required = false) BigDecimal precoMaximo,
            @RequestParam(required = false, defaultValue = FiltroDeAnuncio.ORDEM_PADRAO) String ordenar,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanhoDaPagina,
            @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        FiltroDeAnuncio filtro = new FiltroDeAnuncio(
                q, categoria, negociacao, marca, tamanho, condicao, cidade, precoMinimo, precoMaximo, ordenar);
        return anuncios.buscar(filtro, pagina, tamanhoDaPagina, conta(autenticado));
    }

    @GetMapping("/anuncios/{id}")
    @SecurityRequirements
    @Operation(summary = "Detalha um anuncio e registra a visualizacao")
    public AnuncioDetalheResponse detalhar(@PathVariable Long id,
                                           @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return anuncios.detalhar(id, conta(autenticado));
    }

    @GetMapping("/filtros")
    @SecurityRequirements
    @Operation(summary = "Valores disponiveis para montar os filtros do catalogo")
    public FiltrosResponse filtros() {
        return anuncios.filtrosDisponiveis();
    }

    @GetMapping("/meus-anuncios")
    @Operation(summary = "Lista os anuncios da conta autenticada")
    public PaginaResponse<AnuncioResumoResponse> meus(
            @RequestParam(required = false) StatusAnuncio status,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanhoDaPagina,
            @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return anuncios.meusAnuncios(autenticado.getUsuario(), status, pagina, tamanhoDaPagina);
    }

    @PostMapping("/anuncios")
    @Operation(summary = "Publica uma nova peca")
    public ResponseEntity<AnuncioDetalheResponse> criar(@Valid @RequestBody AnuncioRequest pedido,
                                                        @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        AnuncioDetalheResponse criado = anuncios.criar(autenticado.getUsuario(), pedido);
        return ResponseEntity.created(URI.create("/api/anuncios/" + criado.id())).body(criado);
    }

    @PutMapping("/anuncios/{id}")
    @Operation(summary = "Atualiza um anuncio da propria conta")
    public AnuncioDetalheResponse atualizar(@PathVariable Long id,
                                            @Valid @RequestBody AnuncioRequest pedido,
                                            @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return anuncios.atualizar(autenticado.getUsuario(), id, pedido);
    }

    @PatchMapping("/anuncios/{id}/status")
    @Operation(summary = "Pausa, reativa ou marca o anuncio como negociado")
    public AnuncioResumoResponse alterarStatus(@PathVariable Long id,
                                               @Valid @RequestBody StatusAnuncioRequest pedido,
                                               @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return anuncios.alterarStatus(autenticado.getUsuario(), id, pedido.status());
    }

    @DeleteMapping("/anuncios/{id}")
    @Operation(summary = "Remove o anuncio do catalogo")
    public ResponseEntity<Void> remover(@PathVariable Long id,
                                        @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        anuncios.remover(autenticado.getUsuario(), id);
        return ResponseEntity.noContent().build();
    }

    private static Usuario conta(UsuarioAutenticado autenticado) {
        return autenticado == null ? null : autenticado.getUsuario();
    }
}
