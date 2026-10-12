package br.com.zyclos.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.zyclos.TesteDeIntegracao;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@TesteDeIntegracao
@DisplayName("Fluxo de cadastro e login")
class AutenticacaoTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper json;

    @Test
    @DisplayName("cadastra uma conta e devolve o token junto dos dados do usuario")
    void cadastraEDevolveToken() throws Exception {
        mvc.perform(post("/api/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoDeCadastro("ana@exemplo.com", "ana.estilo")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.usuario.username").value("ana.estilo"))
                .andExpect(jsonPath("$.usuario.papel").value("USUARIO"));
    }

    @Test
    @DisplayName("recusa um segundo cadastro com o mesmo e-mail")
    void recusaEmailRepetido() throws Exception {
        mvc.perform(post("/api/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoDeCadastro("bruno@exemplo.com", "bruno.um")))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoDeCadastro("bruno@exemplo.com", "bruno.dois")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("Ja existe uma conta com este e-mail."));
    }

    @Test
    @DisplayName("rejeita senha com menos de oito caracteres")
    void rejeitaSenhaCurta() throws Exception {
        String corpo = json.writeValueAsString(Map.of(
                "nome", "Carla Souza", "username", "carla", "email", "carla@exemplo.com", "senha", "123"));

        mvc.perform(post("/api/auth/cadastro").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.senha").exists());
    }

    @Test
    @DisplayName("autentica com a senha correta e recusa a incorreta")
    void autenticaComSenhaCorreta() throws Exception {
        mvc.perform(post("/api/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoDeCadastro("davi@exemplo.com", "davi.z")))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(
                                Map.of("email", "davi@exemplo.com", "senha", "senhaSegura1"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(
                                Map.of("email", "davi@exemplo.com", "senha", "senhaErrada1"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("devolve a conta do token em /auth/eu e bloqueia o acesso sem token")
    void protegeRotaDeConta() throws Exception {
        String resposta = mvc.perform(post("/api/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoDeCadastro("elisa@exemplo.com", "elisa.z")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode corpo = json.readTree(resposta);

        mvc.perform(get("/api/auth/eu").header("Authorization", "Bearer " + corpo.get("token").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("elisa@exemplo.com"));

        mvc.perform(get("/api/auth/eu"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro").value("Nao autenticado"));
    }

    private String corpoDeCadastro(String email, String username) throws Exception {
        return json.writeValueAsString(Map.of(
                "nome", "Pessoa de Teste",
                "username", username,
                "email", email,
                "senha", "senhaSegura1"));
    }
}
