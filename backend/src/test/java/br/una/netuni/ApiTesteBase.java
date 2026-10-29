package br.una.netuni;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base dos testes de API: sobe a aplicacao com banco H2 em memoria
 * e desfaz tudo ao fim de cada teste (@Transactional).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public abstract class ApiTesteBase {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper json;

    protected ResultActions postar(String url, String token, String corpo) throws Exception {
        if (token == null) {
            return mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(corpo));
        }
        return mvc.perform(post(url).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(corpo));
    }

    protected ResultActions alterar(String url, String token, String corpo) throws Exception {
        return mvc.perform(put(url).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(corpo));
    }

    protected JsonNode lerJson(ResultActions resultado) throws Exception {
        return json.readTree(resultado.andReturn().getResponse().getContentAsString());
    }

    protected void cadastrar(String nome, String email) throws Exception {
        String corpo = "{\"nome\":\"" + nome + "\",\"email\":\"" + email + "\",\"senha\":\"123456\","
                + "\"curso\":\"Ciencia da Computacao\",\"semestre\":2}";
        postar("/usuarios", null, corpo).andExpect(status().isCreated());
    }

    /** Cadastra e faz login, devolvendo o token. */
    protected String cadastrarELogar(String nome, String email) throws Exception {
        cadastrar(nome, email);
        ResultActions login = postar("/login", null, "{\"email\":\"" + email + "\",\"senha\":\"123456\"}")
                .andExpect(status().isOk());
        return lerJson(login).get("token").asText();
    }

    protected long criarServico(String token, String tipo, String titulo) throws Exception {
        String corpo = "{\"tipo\":\"" + tipo + "\",\"titulo\":\"" + titulo + "\",\"descricao\":\"teste\","
                + "\"preco\":50,\"prazoDias\":3}";
        return lerJson(postar("/servicos", token, corpo).andExpect(status().isCreated())).get("id").asLong();
    }

    protected long solicitar(String token, long servicoId) throws Exception {
        return lerJson(postar("/contratacoes", token, "{\"servicoId\":" + servicoId + "}")
                .andExpect(status().isCreated())).get("id").asLong();
    }

    protected ResultActions mudarStatus(String token, long contratacaoId, String novoStatus) throws Exception {
        return alterar("/contratacoes/" + contratacaoId + "/status", token, "{\"status\":\"" + novoStatus + "\"}");
    }

    protected ResultActions negociar(String token, long contratacaoId) throws Exception {
        return alterar("/contratacoes/" + contratacaoId, token,
                "{\"valorNegociado\":45,\"formaPagamento\":\"Pix\"}");
    }
}
