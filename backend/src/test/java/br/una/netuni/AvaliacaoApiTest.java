package br.una.netuni;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.una.netuni.service.AvaliacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** RF11 Avaliacao e RF12 Portfolio. */
class AvaliacaoApiTest extends ApiTesteBase {

    private String tokenPrestador;
    private String tokenContratante;
    private long contratacaoId;
    private long prestadorId;

    @BeforeEach
    void preparar() throws Exception {
        tokenPrestador = cadastrarELogar("Bruno", "bruno@una.br");
        tokenContratante = cadastrarELogar("Carla", "carla@una.br");
        prestadorId = lerJson(mvc.perform(get("/usuarios/me").header("Authorization", "Bearer " + tokenPrestador)))
                .get("id").asLong();
        long ofertaId = criarServico(tokenPrestador, "OFERTA", "Aulas de Calculo");
        contratacaoId = solicitar(tokenContratante, ofertaId);
        negociar(tokenContratante, contratacaoId);
        mudarStatus(tokenPrestador, contratacaoId, "ACEITA");
        mudarStatus(tokenPrestador, contratacaoId, "EM_ANDAMENTO");
        mudarStatus(tokenContratante, contratacaoId, "CONCLUIDA").andExpect(status().isOk());
    }

    private String avaliacao(int nota) {
        return "{\"contratacaoId\":" + contratacaoId + ",\"nota\":" + nota + ",\"comentario\":\"Otimo\"}";
    }

    @Test
    void notaValida() throws Exception {
        postar("/avaliacoes", tokenContratante, avaliacao(5))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nota").value(5));

        mvc.perform(get("/usuarios/" + prestadorId + "/avaliacoes").header("Authorization", "Bearer " + tokenPrestador))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.media").value(5.0));
    }

    @Test
    void notaAbaixoDoPermitido() throws Exception {
        postar("/avaliacoes", tokenContratante, avaliacao(0)).andExpect(status().isBadRequest());
    }

    @Test
    void notaAcimaDoPermitido() throws Exception {
        postar("/avaliacoes", tokenContratante, avaliacao(6)).andExpect(status().isBadRequest());
    }

    @Test
    void avaliacaoDuplicada() throws Exception {
        postar("/avaliacoes", tokenContratante, avaliacao(4)).andExpect(status().isCreated());
        postar("/avaliacoes", tokenContratante, avaliacao(5)).andExpect(status().isConflict());
    }

    @Test
    void prestadorNaoAvaliaASiMesmo() throws Exception {
        postar("/avaliacoes", tokenPrestador, avaliacao(5)).andExpect(status().isForbidden());
    }

    @Test
    void servicoConcluidoEntraNoPortfolio() throws Exception {
        mvc.perform(get("/usuarios/" + prestadorId + "/portfolio").header("Authorization", "Bearer " + tokenPrestador))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Aulas de Calculo"));
    }

    @Test
    void calculoDaMedia() {
        assertEquals(0.0, AvaliacaoService.calcularMedia(0, 0));
        assertEquals(4.5, AvaliacaoService.calcularMedia(9, 2));
        assertEquals(4.3, AvaliacaoService.calcularMedia(13, 3));
    }
}
