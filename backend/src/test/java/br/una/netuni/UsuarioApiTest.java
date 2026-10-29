package br.una.netuni;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

/** RF01 Cadastro, RF02 Login, RF03 Perfil. */
class UsuarioApiTest extends ApiTesteBase {

    // ---------- Cadastro ----------
    @Test
    void cadastroValido() throws Exception {
        postar("/usuarios", null,
                "{\"nome\":\"Ana\",\"email\":\"Ana@UNA.br\",\"senha\":\"123456\",\"curso\":\"ADS\",\"semestre\":2}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ana@una.br"))
                .andExpect(jsonPath("$.senha").doesNotExist())
                .andExpect(jsonPath("$.senhaHash").doesNotExist());
    }

    @Test
    void cadastroComEmailInvalido() throws Exception {
        postar("/usuarios", null, "{\"nome\":\"Ana\",\"email\":\"ana-sem-arroba\",\"senha\":\"123456\"}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastroComCamposVazios() throws Exception {
        postar("/usuarios", null, "{\"nome\":\"\",\"email\":\"\",\"senha\":\"\"}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastroComSenhaCurta() throws Exception {
        postar("/usuarios", null, "{\"nome\":\"Ana\",\"email\":\"ana@una.br\",\"senha\":\"123\"}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void cadastroComEmailDuplicado() throws Exception {
        cadastrar("Ana", "ana@una.br");
        postar("/usuarios", null, "{\"nome\":\"Outra Ana\",\"email\":\"ANA@una.br\",\"senha\":\"654321\"}")
                .andExpect(status().isConflict());
    }

    // ---------- Login ----------
    @Test
    void loginCorreto() throws Exception {
        cadastrar("Bruno", "bruno@una.br");
        postar("/login", null, "{\"email\":\"bruno@una.br\",\"senha\":\"123456\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.usuario.nome").value("Bruno"));
    }

    @Test
    void loginComSenhaIncorreta() throws Exception {
        cadastrar("Bruno", "bruno@una.br");
        postar("/login", null, "{\"email\":\"bruno@una.br\",\"senha\":\"errada\"}")
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginComUsuarioInexistente() throws Exception {
        postar("/login", null, "{\"email\":\"ninguem@una.br\",\"senha\":\"123456\"}")
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rotaProtegidaSemTokenDevolve401() throws Exception {
        mvc.perform(get("/servicos")).andExpect(status().isUnauthorized());
    }

    // ---------- Perfil ----------
    @Test
    void atualizarPerfilComHabilidades() throws Exception {
        String token = cadastrarELogar("Carla", "carla@una.br");
        long id = lerJson(mvc.perform(get("/usuarios/me").header("Authorization", "Bearer " + token)))
                .get("id").asLong();

        alterar("/usuarios/" + id, token,
                "{\"nome\":\"Carla Dias\",\"descricao\":\"Planilhas\",\"habilidades\":[\"Excel\",\" excel \",\"\",\"Power BI\"]}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habilidades.length()").value(2));
    }

    @Test
    void naoPodeAlterarPerfilDeOutroAluno() throws Exception {
        String tokenCarla = cadastrarELogar("Carla", "carla@una.br");
        String tokenEva = cadastrarELogar("Eva", "eva@una.br");
        long idCarla = lerJson(mvc.perform(get("/usuarios/me").header("Authorization", "Bearer " + tokenCarla)))
                .get("id").asLong();

        alterar("/usuarios/" + idCarla, tokenEva, "{\"nome\":\"Invasor\"}")
                .andExpect(status().isForbidden());
    }

    @Test
    void perfilDeOutroAlunoNaoMostraEmail() throws Exception {
        String tokenCarla = cadastrarELogar("Carla", "carla@una.br");
        String tokenEva = cadastrarELogar("Eva", "eva@una.br");
        long idCarla = lerJson(mvc.perform(get("/usuarios/me").header("Authorization", "Bearer " + tokenCarla)))
                .get("id").asLong();

        mvc.perform(get("/usuarios/" + idCarla).header("Authorization", "Bearer " + tokenEva))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").doesNotExist());
    }
}
