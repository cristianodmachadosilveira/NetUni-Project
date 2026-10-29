package br.una.netuni;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** RF13 - Comunidades como etiqueta dos anuncios. */
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

    private long anunciar(String token, String titulo, Long comunidadeId) throws Exception {
        String corpo = "{\"tipo\":\"OFERTA\",\"titulo\":\"" + titulo + "\",\"descricao\":\"teste\",\"preco\":50,"
                + "\"prazoDias\":3" + (comunidadeId == null ? "" : ",\"comunidadeId\":" + comunidadeId) + "}";
        return lerJson(postar("/servicos", token, corpo).andExpect(status().isCreated())).get("id").asLong();
    }

    private JsonNode buscar(String token, String parametros) throws Exception {
        return lerJson(mvc.perform(get("/servicos" + parametros).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()));
    }

    @Test
    void criarEListar() throws Exception {
        postar("/comunidades", tokenBruno, "{\"nome\":\"Dev UNA\"}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.anuncios").value(0))
                .andExpect(jsonPath("$.cor").value("#2f6bff"));

        mvc.perform(get("/comunidades").header("Authorization", "Bearer " + tokenCarla))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Dev UNA"))
                .andExpect(jsonPath("$[0].criador.nome").value("Bruno"));
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
    void comunidadeInexistente() throws Exception {
        mvc.perform(get("/comunidades/999").header("Authorization", "Bearer " + tokenBruno))
                .andExpect(status().isNotFound());
        mvc.perform(get("/servicos?comunidadeId=999").header("Authorization", "Bearer " + tokenBruno))
                .andExpect(status().isNotFound());
    }

    @Test
    void anuncioComEtiquetaApareceNoFiltroENaListaGeral() throws Exception {
        long dev = criarComunidade(tokenBruno, "Dev UNA");
        anunciar(tokenBruno, "Aula de Java", dev);
        anunciar(tokenCarla, "Planilha", null);

        JsonNode filtrado = buscar(tokenCarla, "?comunidadeId=" + dev);
        assertEquals(1, filtrado.size());
        assertEquals("Aula de Java", filtrado.get(0).get("titulo").asText());
        assertEquals("Dev UNA", filtrado.get(0).get("comunidade").get("nome").asText());

        // A lista geral continua mostrando todos, com e sem etiqueta.
        assertEquals(2, buscar(tokenCarla, "").size());

        mvc.perform(get("/comunidades/" + dev).header("Authorization", "Bearer " + tokenCarla))
                .andExpect(jsonPath("$.anuncios").value(1));
    }

    @Test
    void anuncioComComunidadeInexistente() throws Exception {
        String corpo = "{\"tipo\":\"OFERTA\",\"titulo\":\"X\",\"preco\":10,\"prazoDias\":1,\"comunidadeId\":999}";
        postar("/servicos", tokenBruno, corpo).andExpect(status().isNotFound());
    }

    @Test
    void editarTrocaOuTiraEtiqueta() throws Exception {
        long dev = criarComunidade(tokenBruno, "Dev UNA");
        long servico = anunciar(tokenBruno, "Aula de Java", dev);

        alterar("/servicos/" + servico, tokenBruno,
                "{\"tipo\":\"OFERTA\",\"titulo\":\"Aula de Java\",\"preco\":50,\"prazoDias\":3}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comunidade").doesNotExist());
        assertEquals(0, buscar(tokenBruno, "?comunidadeId=" + dev).size());
        assertEquals(1, buscar(tokenBruno, "").size());
    }

    @Test
    void anuncioEncerradoNaoContaNaComunidade() throws Exception {
        long dev = criarComunidade(tokenBruno, "Dev UNA");
        long servico = anunciar(tokenBruno, "Aula de Java", dev);
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/servicos/" + servico)
                .header("Authorization", "Bearer " + tokenBruno)).andExpect(status().isOk());
        mvc.perform(get("/comunidades/" + dev).header("Authorization", "Bearer " + tokenBruno))
                .andExpect(jsonPath("$.anuncios").value(0));
    }
}
