package br.com.zyclos.exception;

/** Lancada quando a operacao fere uma regra do dominio. Resulta em HTTP 422. */
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
