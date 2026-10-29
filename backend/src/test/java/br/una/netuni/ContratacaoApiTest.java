package br.una.netuni;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** RF08 Comunicacao, RF09 Solicitacao, RF10 Gerenciamento da contratacao. */
class ContratacaoApiTest extends ApiTesteBase {

    private String tokenPrestador;
    private String tokenContratante;
    private long ofertaId;

    @BeforeEach
    void preparar() throws Exception {
        tokenPrestador = cadastrarELogar("Bruno", "bruno@una.br");
        tokenContratante = cadastrarELogar("Carla", "carla@una.br");
        ofertaId = criarServico(tokenPrestador, "OFERTA", "Aulas de Calculo");
    }

    // ---------- RF09 ----------
    @Test
    void solicitarServicoExistente() throws Exception {
        postar("/contratacoes", tokenContratante, "{\"servicoId\":" + ofertaId + "}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SOLICITADA"))
                .andExpect(jsonPath("$.contratante.nome").value("Carla"))
                .andExpect(jsonPath("$.prestador.nome").value("Bruno"));
    }

    @Test
    void solicitarPedidoInverteOsPapeis() throws Exception {
        long pedidoId = criarServico(tokenContratante, "PEDIDO", "Preciso de planilha");
        postar("/contratacoes", tokenPrestador, "{\"servicoId\":" + pedidoId + "}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contratante.nome").value("Carla"))
                .andExpect(jsonPath("$.prestador.nome").value("Bruno"));
    }

    @Test
    void solicitarServicoInexistente() throws Exception {
        postar("/contratacoes", tokenContratante, "{\"servicoId\":99999}")
                .andExpect(status().isNotFound());
    }

    @Test
    void solicitarContratacaoDuplicada() throws Exception {
        solicitar(tokenContratante, ofertaId);
        postar("/contratacoes", tokenContratante, "{\"servicoId\":" + ofertaId + "}")
                .andExpect(status().isConflict());
    }

    @Test
    void naoPodeSolicitarOProprioServico() throws Exception {
        postar("/contratacoes", tokenPrestador, "{\"servicoId\":" + ofertaId + "}")
                .andExpect(status().isBadRequest());
    }

    // ---------- RF10 ----------
    @Test
    void fluxoCompletoAteConcluir() throws Exception {
        long id = solicitar(tokenContratante, ofertaId);
        negociar(tokenContratante, id).andExpect(status().isOk())
                .andExpect(jsonPath("$.formaPagamento").value("Pix"));
        mudarStatus(tokenPrestador, id, "ACEITA").andExpect(status().isOk());
        mudarStatus(tokenPrestador, id, "EM_ANDAMENTO").andExpect(status().isOk());
        mudarStatus(tokenContratante, id, "CONCLUIDA").andExpect(status().isOk())
                .andExpect(jsonPath("$.acoesPermitidas[0]").value("AVALIAR"));
    }

    @Test
    void statusInvalidoPularEtapa() throws Exception {
        long id = solicitar(tokenContratante, ofertaId);
        mudarStatus(tokenContratante, id, "CONCLUIDA").andExpect(status().isConflict());
    }

    @Test
    void statusInvalidoVoltarParaSolicitada() throws Exception {
        long id = solicitar(tokenContratante, ofertaId);
        mudarStatus(tokenPrestador, id, "ACEITA").andExpect(status().isOk());
        mudarStatus(tokenPrestador, id, "SOLICITADA").andExpect(status().isConflict());
    }

    @Test
    void statusQueNaoExiste() throws Exception {
        long id = solicitar(tokenContratante, ofertaId);
        mudarStatus(tokenPrestador, id, "PAGO").andExpect(status().isBadRequest());
    }

    @Test
    void solicitanteNaoPodeAceitarAPropriaSolicitacao() throws Exception {
        long id = solicitar(tokenContratante, ofertaId);
        mudarStatus(tokenContratante, id, "ACEITA").andExpect(status().isForbidden());
    }

    @Test
    void naoIniciaSemFormaDePagamento() throws Exception {
        long id = solicitar(tokenContratante, ofertaId);
        mudarStatus(tokenPrestador, id, "ACEITA").andExpect(status().isOk());
        mudarStatus(tokenPrestador, id, "EM_ANDAMENTO").andExpect(status().isBadRequest());
    }

    @Test
    void terceiroNaoVeContratacao() throws Exception {
        long id = solicitar(tokenContratante, ofertaId);
        String tokenIntruso = cadastrarELogar("Davi", "davi@una.br");
        mvc.perform(get("/contratacoes/" + id).header("Authorization", "Bearer " + tokenIntruso))
                .andExpect(status().isForbidden());
    }

    // ---------- RF08 ----------
    @Test
    void conversarNaContratacao() throws Exception {
        long id = solicitar(tokenContratante, ofertaId);
        postar("/contratacoes/" + id + "/mensagens", tokenContratante, "{\"conteudo\":\"Pode ser sabado?\"}")
                .andExpect(status().isCreated());
        postar("/contratacoes/" + id + "/mensagens", tokenPrestador, "{\"conteudo\":\"   \"}")
                .andExpect(status().isBadRequest());

        // 1 aviso automatico ("solicitou o servico") + 1 mensagem da Carla
        mvc.perform(get("/contratacoes/" + id + "/mensagens").header("Authorization", "Bearer " + tokenPrestador))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].conteudo").value("Pode ser sabado?"));
    }
}
