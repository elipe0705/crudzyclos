package br.com.zyclos.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/** Converte as excecoes da aplicacao no corpo de erro padrao da API. */
@RestControllerAdvice
public class ManipuladorDeErros {

    private static final Logger LOG = LoggerFactory.getLogger(ManipuladorDeErros.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> naoEncontrado(RecursoNaoEncontradoException erro, HttpServletRequest requisicao) {
        return responder(HttpStatus.NOT_FOUND, "Recurso nao encontrado", erro.getMessage(), requisicao);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroResposta> regraDeNegocio(RegraDeNegocioException erro, HttpServletRequest requisicao) {
        return responder(HttpStatus.UNPROCESSABLE_ENTITY, "Regra de negocio", erro.getMessage(), requisicao);
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResposta> conflito(ConflitoException erro, HttpServletRequest requisicao) {
        return responder(HttpStatus.CONFLICT, "Conflito", erro.getMessage(), requisicao);
    }

    @ExceptionHandler({OperacaoNaoPermitidaException.class, AccessDeniedException.class})
    public ResponseEntity<ErroResposta> semPermissao(Exception erro, HttpServletRequest requisicao) {
        String mensagem = erro instanceof AccessDeniedException
                ? "Voce nao tem permissao para acessar este recurso."
                : erro.getMessage();
        return responder(HttpStatus.FORBIDDEN, "Acesso negado", mensagem, requisicao);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErroResposta> credenciaisInvalidas(BadCredentialsException erro, HttpServletRequest requisicao) {
        return responder(HttpStatus.UNAUTHORIZED, "Credenciais invalidas",
                "E-mail ou senha incorretos.", requisicao);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErroResposta> contaDesativada(DisabledException erro, HttpServletRequest requisicao) {
        return responder(HttpStatus.FORBIDDEN, "Conta indisponivel",
                "Esta conta esta suspensa ou inativa.", requisicao);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErroResposta> naoAutenticado(AuthenticationException erro, HttpServletRequest requisicao) {
        return responder(HttpStatus.UNAUTHORIZED, "Nao autenticado",
                "Faca login para continuar.", requisicao);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> validacao(MethodArgumentNotValidException erro, HttpServletRequest requisicao) {
        Map<String, String> campos = new LinkedHashMap<>();
        erro.getBindingResult().getFieldErrors()
                .forEach(campo -> campos.putIfAbsent(campo.getField(), campo.getDefaultMessage()));
        erro.getBindingResult().getGlobalErrors()
                .forEach(global -> campos.putIfAbsent(global.getObjectName(), global.getDefaultMessage()));
        String primeira = campos.values().stream().findFirst().orElse("Revise os dados informados.");
        return ResponseEntity.badRequest().body(ErroResposta.comCampos(
                HttpStatus.BAD_REQUEST.value(), "Dados invalidos", primeira, requisicao.getRequestURI(), campos));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, IllegalArgumentException.class})
    public ResponseEntity<ErroResposta> corpoInvalido(Exception erro, HttpServletRequest requisicao) {
        String mensagem = erro instanceof IllegalArgumentException
                ? erro.getMessage()
                : "Nao foi possivel interpretar o corpo da requisicao.";
        return responder(HttpStatus.BAD_REQUEST, "Dados invalidos", mensagem, requisicao);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResposta> integridade(DataIntegrityViolationException erro, HttpServletRequest requisicao) {
        LOG.warn("Violacao de integridade em {}", requisicao.getRequestURI(), erro);
        return responder(HttpStatus.CONFLICT, "Conflito",
                "A operacao conflita com um registro existente.", requisicao);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErroResposta> arquivoGrande(MaxUploadSizeExceededException erro, HttpServletRequest requisicao) {
        return responder(HttpStatus.PAYLOAD_TOO_LARGE, "Arquivo muito grande",
                "A imagem excede o tamanho maximo permitido.", requisicao);
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ErroResposta> rotaInexistente(Exception erro, HttpServletRequest requisicao) {
        return responder(HttpStatus.NOT_FOUND, "Rota nao encontrada",
                "Nenhum recurso responde a este endereco.", requisicao);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResposta> metodoNaoSuportado(HttpRequestMethodNotSupportedException erro,
                                                           HttpServletRequest requisicao) {
        return responder(HttpStatus.METHOD_NOT_ALLOWED, "Metodo nao permitido",
                "Este recurso nao aceita requisicoes " + erro.getMethod() + ".", requisicao);
    }

    /** Parametro de consulta com valor que nao converte, por exemplo um status inexistente. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResposta> parametroInvalido(MethodArgumentTypeMismatchException erro,
                                                          HttpServletRequest requisicao) {
        Throwable causa = erro.getMostSpecificCause();
        String detalhe = causa instanceof IllegalArgumentException && causa.getMessage() != null
                ? causa.getMessage()
                : "Valor invalido para o parametro " + erro.getName() + ".";
        return responder(HttpStatus.BAD_REQUEST, "Parametro invalido", detalhe, requisicao);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> inesperado(Exception erro, HttpServletRequest requisicao) {
        LOG.error("Erro inesperado em {}", requisicao.getRequestURI(), erro);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
                "Algo deu errado ao processar a requisicao.", requisicao);
    }

    private ResponseEntity<ErroResposta> responder(HttpStatus status, String erro, String mensagem,
                                                   HttpServletRequest requisicao) {
        return ResponseEntity.status(status)
                .body(ErroResposta.de(status.value(), erro, mensagem, requisicao.getRequestURI()));
    }
}
