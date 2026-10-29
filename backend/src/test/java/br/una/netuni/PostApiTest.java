package br.una.netuni;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.una.netuni.service.PostService;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

/** RF14 / RF15 - Posts, feed, curtidas, comentarios e upload de midia. */
class PostApiTest extends ApiTesteBase {

    private String tokenAna;
    private String tokenBruno;

    @BeforeEach
    void preparar() throws Exception {
        tokenAna = cadastrarELogar("Ana", "ana@una.br");
        tokenBruno = cadastrarELogar("Bruno", "bruno@una.br");
    }

    private long publicarTexto(String token, String texto) throws Exception {
        String corpo = "{\"tipoMidia\":\"TEXTO\",\"legenda\":\"" + texto + "\"}";
        return lerJson(postar("/posts", token, corpo).andExpect(status().isCreated())).get("id").asLong();
    }

    private JsonNode feed(String token, String parametros) throws Exception {
        return lerJson(mvc.perform(get("/feed" + parametros).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()));
    }

    @Test
    void publicarTextoApareceNoFeed() throws Exception {
        publicarTexto(tokenAna, "Ola, NetUni");
        JsonNode itens = feed(tokenBruno, "");
        assertEquals(1, itens.size());
        assertEquals("Ola, NetUni", itens.get(0).get("legenda").asText());
        assertEquals("Ana", itens.get(0).get("autor").get("nome").asText());
        assertEquals(false, itens.get(0).get("meu").asBoolean());
    }

    @Test
    void textoVazioEhRecusado() throws Exception {
        postar("/posts", tokenAna, "{\"tipoMidia\":\"TEXTO\",\"legenda\":\"   \"}").andExpect(status().isBadRequest());
    }

    @Test
    void imagemSemMidiaEhRecusada() throws Exception {
        postar("/posts", tokenAna, "{\"tipoMidia\":\"IMAGEM\",\"legenda\":\"foto\"}").andExpect(status().isBadRequest());
    }

    @Test
    void enderecoDeMidiaInvalido() throws Exception {
        postar("/posts", tokenAna, "{\"tipoMidia\":\"IMAGEM\",\"midiaUrl\":\"/midias/../segredo\"}")
                .andExpect(status().isBadRequest());
        postar("/posts", tokenAna, "{\"tipoMidia\":\"VIDEO\",\"midiaUrl\":\"http://site.com/v.mp4\"}")
                .andExpect(status().isBadRequest());
        postar("/posts", tokenAna, "{\"tipoMidia\":\"VIDEO\",\"midiaUrl\":\"/demo/paleta.mp4\"}")
                .andExpect(status().isCreated());
    }

    @Test
    void soMembroPublicaNaComunidade() throws Exception {
        long comunidade = lerJson(postar("/comunidades", tokenAna, "{\"nome\":\"Design\"}")).get("id").asLong();
        String corpo = "{\"tipoMidia\":\"TEXTO\",\"legenda\":\"oi\",\"comunidadeId\":" + comunidade + "}";

        postar("/posts", tokenBruno, corpo).andExpect(status().isForbidden());
        postar("/comunidades/" + comunidade + "/membros", tokenBruno, "{}").andExpect(status().isOk());
        postar("/posts", tokenBruno, corpo)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comunidade.nome").value("Design"));

        publicarTexto(tokenAna, "fora da comunidade");
        assertEquals(1, feed(tokenAna, "?comunidadeId=" + comunidade).size());
        assertEquals(2, feed(tokenAna, "").size());
    }

    @Test
    void curtidaAlterna() throws Exception {
        long post = publicarTexto(tokenAna, "curte ai");
        postar("/posts/" + post + "/curtida", tokenBruno, "{}")
                .andExpect(jsonPath("$.curtiu").value(true))
                .andExpect(jsonPath("$.curtidas").value(1));
        postar("/posts/" + post + "/curtida", tokenBruno, "{}")
                .andExpect(jsonPath("$.curtiu").value(false))
                .andExpect(jsonPath("$.curtidas").value(0));
    }

    @Test
    void comentarELista() throws Exception {
        long post = publicarTexto(tokenAna, "comenta ai");
        postar("/posts/" + post + "/comentarios", tokenBruno, "{\"texto\":\"Muito bom\"}")
                .andExpect(status().isCreated());
        postar("/posts/" + post + "/comentarios", tokenBruno, "{\"texto\":\"\"}")
                .andExpect(status().isBadRequest());
        mvc.perform(get("/posts/" + post + "/comentarios").header("Authorization", "Bearer " + tokenAna))
                .andExpect(jsonPath("$[0].texto").value("Muito bom"))
                .andExpect(jsonPath("$[0].autor.nome").value("Bruno"));
        assertEquals(1, feed(tokenAna, "").get(0).get("comentarios").asInt());
    }

    @Test
    void soAutorApaga() throws Exception {
        long post = publicarTexto(tokenAna, "vou apagar");
        postar("/posts/" + post + "/curtida", tokenBruno, "{}");
        mvc.perform(delete("/posts/" + post).header("Authorization", "Bearer " + tokenBruno))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/posts/" + post).header("Authorization", "Bearer " + tokenAna))
                .andExpect(status().isNoContent());
        assertEquals(0, feed(tokenAna, "").size());
    }

    @Test
    void feedRanqueiaPorEngajamento() throws Exception {
        long antigo = publicarTexto(tokenAna, "primeiro");
        publicarTexto(tokenAna, "segundo");
        postar("/posts/" + antigo + "/curtida", tokenBruno, "{}");

        JsonNode itens = feed(tokenBruno, "");
        assertEquals("primeiro", itens.get(0).get("legenda").asText());
        assertTrue(itens.get(0).get("pontuacao").asDouble() > itens.get(1).get("pontuacao").asDouble());
    }

    @Test
    void calculoDaPontuacao() {
        assertEquals(1.0, PostService.calcularPontuacao(0, 0, false, 0));
        assertEquals(11.0, PostService.calcularPontuacao(0, 0, true, 0));
        assertEquals(6.0, PostService.calcularPontuacao(1, 1, false, 0));     // 1 + 2 + 3
        assertEquals(0.5, PostService.calcularPontuacao(0, 0, false, 12));    // metade depois de 12h
    }

    @Test
    void uploadDeImagemEDepoisBaixar() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", "foto.png", "image/png", new byte[] {1, 2, 3, 4});
        JsonNode r = lerJson(mvc.perform(multipart("/midias").file(arquivo).header("Authorization", "Bearer " + tokenAna))
                .andExpect(status().isCreated()));
        String url = r.get("url").asText();
        assertTrue(url.startsWith("/midias/") && url.endsWith(".png"));
        assertEquals("IMAGEM", r.get("tipoMidia").asText());

        // O navegador baixa a midia sem token.
        mvc.perform(get(url)).andExpect(status().isOk());
    }

    @Test
    void uploadDeFormatoNaoAceito() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", "nota.txt", "text/plain", "oi".getBytes());
        mvc.perform(multipart("/midias").file(arquivo).header("Authorization", "Bearer " + tokenAna))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadExigeLogin() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", "foto.png", "image/png", new byte[] {1});
        mvc.perform(multipart("/midias").file(arquivo)).andExpect(status().isUnauthorized());
    }
}
