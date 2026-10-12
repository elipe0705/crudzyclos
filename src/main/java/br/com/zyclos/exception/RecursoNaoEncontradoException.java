package br.com.zyclos.exception;

/** Lancada quando o recurso pedido nao existe. Resulta em HTTP 404. */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public static RecursoNaoEncontradoException de(String recurso, Object identificador) {
        return new RecursoNaoEncontradoException(recurso + " nao encontrado: " + identificador);
    }
}
