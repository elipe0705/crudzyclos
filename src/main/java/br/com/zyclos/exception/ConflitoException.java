package br.com.zyclos.exception;

/** Lancada quando o recurso ja existe ou colide com outro. Resulta em HTTP 409. */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
