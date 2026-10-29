package br.una.netuni;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

/** RF13 - Comunidades. */
class ComunidadeApiTest extends ApiTesteBase {

    private String tokenBruno;
    private String tokenCarla;

    @BeforeEach
    void preparar() throws Exception {
        tokenBruno = cadastrarELogar("Bruno", "bruno@una.br");
        tokenCarla = cadastrarELogar("Carla", "carla@una.br");
    }

    private long criarComunidade(String token, String nome) throws Exception {
        String corpo = "{\"nome\":\"" + nome + "\",\"descricao\":\"teste\",\"icone\":\"X\",\"cor\":\"#2f6bff\"}";
        return lerJson(postar("/comunidades", token, corpo).andExpect(status().isCreated())).get("id").asLong();
    }

    private ResultActions sair(String token, long id) throws Exception {
        return mvc.perform(delete("/comunidades/" + id + "/membros").header("Authorization", "Bearer " + token));
    }

    @Test
    void criarEListar() throws Exception {
        postar("/comunidades", tokenBruno, "{\"nome\":\"Dev UNA\"}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.souMembro").value(true))
                .andExpect(jsonPath("$.membros").value(1))
                .andExpect(jsonPath("$.cor").value("#2f6bff"));

        mvc.perform(get("/comunidades").header("Authorization", "Bearer " + tokenCarla))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Dev UNA"))
                .andExpect(jsonPath("$[0].souMembro").value(false));
    }

    @Test
    void nomeDuplicadoIgnoraMaiusculas() throws Exception {
        criarComunidade(tokenBruno, "Dev UNA");
        postar("/comunidades", tokenCarla, "{\"nome\":\"dev una\"}").andExpect(status().isConflict());
    }

    @Test
    void corInvalida() throws Exception {
        postar("/comunidades", tokenBruno, "{\"nome\":\"Dev UNA\",\"cor\":\"azul\"}").andExpect(status().isBadRequest());
    }

    @Test
    void entrarESair() throws Exception {
        long id = criarComunidade(tokenBruno, "Dev UNA");

        postar("/comunidades/" + id + "/membros", tokenCarla, "{}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.membros").value(2))
                .andExpect(jsonPath("$.souMembro").value(true));
        postar("/comunidades/" + id + "/membros", tokenCarla, "{}").andExpect(status().isConflict());

        sair(tokenCarla, id)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.membros").value(1))
                .andExpect(jsonPath("$.souMembro").value(false));
        sair(tokenCarla, id).andExpect(status().isConflict());
    }

    @Test
    void criadorNaoPodeSair() throws Exception {
        long id = criarComunidade(tokenBruno, "Dev UNA");
        sair(tokenBruno, id).andExpect(status().isBadRequest());
    }

    @Test
    void comunidadeInexistente() throws Exception {
        mvc.perform(get("/comunidades/999").header("Authorization", "Bearer " + tokenBruno))
                .andExpect(status().isNotFound());
    }
}
