package br.com.zyclos.dto.request;

import br.com.zyclos.domain.Condicao;
import br.com.zyclos.domain.TipoNegociacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/** Dados do assistente de criacao e edicao de anuncio. */
public record AnuncioRequest(
        @NotBlank(message = "Informe o titulo do anuncio.")
        @Size(max = 120)
        String titulo,

        @NotBlank(message = "Descreva a peca.")
        @Size(max = 2000)
        String descricao,

        /** Obrigatorio quando o tipo de negociacao inclui venda. */
        @DecimalMin(value = "0.0", inclusive = false, message = "O preco deve ser maior que zero.")
        @Digits(integer = 8, fraction = 2)
        BigDecimal preco,

        @NotBlank(message = "Escolha uma categoria.")
        String categoria,

        @Size(max = 60)
        String marca,

        @NotBlank(message = "Informe o tamanho.")
        @Size(max = 20)
        String tamanho,

        @NotNull(message = "Informe a condicao da peca.")
        Condicao condicao,

        @Size(max = 40)
        String estilo,

        @Size(max = 120)
        String cidade,

        @Size(max = 2)
        String estado,

        @NotNull(message = "Escolha o tipo de negociacao.")
        TipoNegociacao tipoNegociacao,

        Set<String> tags,

        /** URLs das fotos, na ordem em que devem aparecer. A primeira vira a capa. */
        List<String> imagens) {
}
