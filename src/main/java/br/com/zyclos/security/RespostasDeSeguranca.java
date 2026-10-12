package br.com.zyclos.security;

import br.com.zyclos.exception.ErroResposta;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Escreve 401 e 403 no mesmo formato de erro usado pelos controladores.
 *
 * <p>Esses dois casos sao decididos dentro da cadeia de filtros, antes de qualquer controlador,
 * entao nao passam pelo tratador global de excecoes.
 */
@Component
public class RespostasDeSeguranca implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper json;

    public RespostasDeSeguranca(ObjectMapper json) {
        this.json = json;
    }

    @Override
    public void commence(HttpServletRequest requisicao, HttpServletResponse resposta,
                         AuthenticationException erro) throws IOException {
        escrever(requisicao, resposta, HttpStatus.UNAUTHORIZED, "Nao autenticado",
                "Faca login para acessar este recurso.");
    }

    @Override
    public void handle(HttpServletRequest requisicao, HttpServletResponse resposta,
                       AccessDeniedException erro) throws IOException {
        escrever(requisicao, resposta, HttpStatus.FORBIDDEN, "Acesso negado",
                "Voce nao tem permissao para acessar este recurso.");
    }

    private void escrever(HttpServletRequest requisicao, HttpServletResponse resposta,
                          HttpStatus status, String erro, String mensagem) throws IOException {
        resposta.setStatus(status.value());
        resposta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        resposta.setCharacterEncoding("UTF-8");
        json.writeValue(resposta.getOutputStream(),
                ErroResposta.de(status.value(), erro, mensagem, requisicao.getRequestURI()));
    }
}
