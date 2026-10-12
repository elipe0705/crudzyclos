package br.com.zyclos.dto.response;

import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;

/** Envelope de paginacao devolvido pelas listagens. */
public record PaginaResponse<T>(
        List<T> itens,
        int pagina,
        int tamanho,
        long total,
        int totalPaginas,
        boolean primeira,
        boolean ultima) {

    public static <E, T> PaginaResponse<T> de(Page<E> pagina, Function<E, T> conversor) {
        return new PaginaResponse<>(
                pagina.getContent().stream().map(conversor).toList(),
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages(),
                pagina.isFirst(),
                pagina.isLast());
    }

    public static <T> PaginaResponse<T> de(List<T> itens) {
        return new PaginaResponse<>(itens, 0, itens.size(), itens.size(), 1, true, true);
    }
}
