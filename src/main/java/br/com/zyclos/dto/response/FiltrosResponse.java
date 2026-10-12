package br.com.zyclos.dto.response;

import java.util.List;

/** Valores disponiveis para montar os filtros do catalogo. */
public record FiltrosResponse(
        List<String> categorias,
        List<String> marcas,
        List<String> tamanhos,
        List<String> condicoes,
        List<String> tiposDeNegociacao) {
}
