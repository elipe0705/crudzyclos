package br.com.zyclos.domain;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Function;

/**
 * Converte textos vindos da API em enums do domínio.
 *
 * <p>Aceita tanto o rótulo exibido na interface ("Venda ou troca") quanto a constante
 * técnica ("VENDA_OU_TROCA"), para que o front-end existente continue enviando os mesmos
 * textos que já usava com os dados mockados.
 */
final class Rotulos {

    private Rotulos() {
    }

    static <E extends Enum<E>> E resolver(Class<E> tipo, E[] valores, String valor, Function<E, String> rotulo) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String alvo = normalizar(valor);
        return Arrays.stream(valores)
                .filter(e -> normalizar(e.name()).equals(alvo) || normalizar(rotulo.apply(e)).equals(alvo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Valor inválido para " + tipo.getSimpleName() + ": " + valor));
    }

    private static String normalizar(String texto) {
        String semAcento = java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return semAcento.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }
}
