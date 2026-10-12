package br.com.zyclos.repository;

import br.com.zyclos.domain.Anuncio;
import br.com.zyclos.domain.Condicao;
import br.com.zyclos.domain.StatusAnuncio;
import br.com.zyclos.domain.TipoNegociacao;
import br.com.zyclos.domain.Usuario;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

/** Filtros combinaveis do catalogo de anuncios. */
public final class EspecificacoesDeAnuncio {

    private EspecificacoesDeAnuncio() {
    }

    public static Specification<Anuncio> comStatus(StatusAnuncio status) {
        return (raiz, consulta, builder) ->
                status == null ? null : builder.equal(raiz.get("status"), status);
    }

    /** Busca por texto no titulo ou na descricao. */
    public static Specification<Anuncio> contendo(String termo) {
        if (termo == null || termo.isBlank()) {
            return (raiz, consulta, builder) -> null;
        }
        String padrao = "%" + termo.trim().toLowerCase(Locale.ROOT) + "%";
        return (raiz, consulta, builder) -> {
            Predicate noTitulo = builder.like(builder.lower(raiz.get("titulo")), padrao);
            Predicate naDescricao = builder.like(builder.lower(raiz.get("descricao")), padrao);
            Predicate naMarca = builder.like(builder.lower(raiz.get("marca")), padrao);
            return builder.or(noTitulo, naDescricao, naMarca);
        };
    }

    public static Specification<Anuncio> naCategoria(String nome) {
        if (nome == null || nome.isBlank() || "Todos".equalsIgnoreCase(nome)) {
            return (raiz, consulta, builder) -> null;
        }
        return (raiz, consulta, builder) ->
                builder.equal(builder.lower(raiz.get("categoria").get("nome")), nome.toLowerCase(Locale.ROOT));
    }

    public static Specification<Anuncio> daMarca(String marca) {
        if (marca == null || marca.isBlank() || marca.toLowerCase(Locale.ROOT).startsWith("todas")) {
            return (raiz, consulta, builder) -> null;
        }
        return (raiz, consulta, builder) ->
                builder.equal(builder.lower(raiz.get("marca")), marca.toLowerCase(Locale.ROOT));
    }

    public static Specification<Anuncio> doTamanho(String tamanho) {
        if (tamanho == null || tamanho.isBlank() || "Todos".equalsIgnoreCase(tamanho)) {
            return (raiz, consulta, builder) -> null;
        }
        return (raiz, consulta, builder) ->
                builder.equal(builder.lower(raiz.get("tamanho")), tamanho.toLowerCase(Locale.ROOT));
    }

    public static Specification<Anuncio> naCondicao(Condicao condicao) {
        return (raiz, consulta, builder) ->
                condicao == null ? null : builder.equal(raiz.get("condicao"), condicao);
    }

    public static Specification<Anuncio> comNegociacao(TipoNegociacao tipo) {
        return (raiz, consulta, builder) ->
                tipo == null ? null : builder.equal(raiz.get("tipoNegociacao"), tipo);
    }

    public static Specification<Anuncio> doVendedor(Usuario vendedor) {
        return (raiz, consulta, builder) ->
                vendedor == null ? null : builder.equal(raiz.get("vendedor"), vendedor);
    }

    public static Specification<Anuncio> precoEntre(BigDecimal minimo, BigDecimal maximo) {
        return (raiz, consulta, builder) -> {
            if (minimo == null && maximo == null) {
                return null;
            }
            if (minimo == null) {
                return builder.lessThanOrEqualTo(raiz.get("preco"), maximo);
            }
            if (maximo == null) {
                return builder.greaterThanOrEqualTo(raiz.get("preco"), minimo);
            }
            return builder.between(raiz.get("preco"), minimo, maximo);
        };
    }

    /** Restringe a uma cidade, comparando sem diferenciar maiusculas. */
    public static Specification<Anuncio> naCidade(String cidade) {
        if (cidade == null || cidade.isBlank()) {
            return (raiz, consulta, builder) -> null;
        }
        return (raiz, consulta, builder) ->
                builder.equal(builder.lower(raiz.get("cidade")), cidade.toLowerCase(Locale.ROOT));
    }
}
