package br.com.zyclos.exception;

/** Lancada quando o usuario autenticado nao pode agir sobre o recurso. Resulta em HTTP 403. */
public class OperacaoNaoPermitidaException extends RuntimeException {

    public OperacaoNaoPermitidaException(String mensagem) {
        super(mensagem);
    }
}
