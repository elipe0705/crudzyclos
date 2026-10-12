package br.com.zyclos.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Map;

/** Corpo padrao de erro devolvido pela API. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErroResposta(
        Instant momento,
        int status,
        String erro,
        String mensagem,
        String caminho,
        Map<String, String> campos) {

    public static ErroResposta de(int status, String erro, String mensagem, String caminho) {
        return new ErroResposta(Instant.now(), status, erro, mensagem, caminho, null);
    }

    public static ErroResposta comCampos(int status, String erro, String mensagem, String caminho,
                                         Map<String, String> campos) {
        return new ErroResposta(Instant.now(), status, erro, mensagem, caminho, campos);
    }
}
