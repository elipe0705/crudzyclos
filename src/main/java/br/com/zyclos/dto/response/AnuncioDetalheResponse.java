package br.com.zyclos.dto.response;

import br.com.zyclos.domain.Condicao;
import br.com.zyclos.domain.StatusAnuncio;
import br.com.zyclos.domain.TipoNegociacao;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

/** Pagina de detalhe de um anuncio. */
public record AnuncioDetalheResponse(
        Long id,
        String titulo,
        String descricao,
        BigDecimal preco,
        List<String> imagens,
        String categoria,
        String marca,
        String tamanho,
        Condicao condicao,
        String estilo,
        String localizacao,
        TipoNegociacao tipoNegociacao,
        StatusAnuncio status,
        Set<String> tags,
        long visualizacoes,
        long favoritos,
        boolean favorito,
        boolean meuAnuncio,
        VendedorResumoResponse vendedor,
        Instant criadoEm,
        Instant atualizadoEm) {
}
