package br.una.netuni;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** RF04 Publicacao, RF05 Gerenciamento, RF06 Busca, RF07 Visualizacao. */
class ServicoApiTest extends ApiTesteBase {

    private String token;

    @BeforeEach
    void preparar() throws Exception {
        token = cadastrarELogar("Ana", "ana@una.br");
    }

    @Test
    void servicoValido() throws Exception {
        postar("/servicos", token,
                "{\"tipo\":\"OFERTA\",\"titulo\":\"Logo\",\"descricao\":\"Crio logos\",\"preco\":100,\"prazoDias\":5}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ABERTO"))
                .andExpect(jsonPath("$.autor.nome").value("Ana"));
    }

    @Test
    void servicoComTituloVazio() throws Exception {
        postar("/servicos", token, "{\"tipo\":\"OFERTA\",\"titulo\":\"  \",\"preco\":100,\"prazoDias\":5}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void servicoComPrecoNegativo() throws Exception {
        postar("/servicos", token, "{\"tipo\":\"OFERTA\",\"titulo\":\"Logo\",\"preco\":-1,\"prazoDias\":5}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void servicoComPrecoQueNaoENumero() throws Exception {
        postar("/servicos", token, "{\"tipo\":\"OFERTA\",\"titulo\":\"Logo\",\"preco\":\"abc\",\"prazoDias\":5}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void servicoComPrazoZero() throws Exception {
        postar("/servicos", token, "{\"tipo\":\"OFERTA\",\"titulo\":\"Logo\",\"preco\":10,\"prazoDias\":0}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void servicoSemTipo() throws Exception {
        postar("/servicos", token, "{\"titulo\":\"Logo\",\"preco\":10,\"prazoDias\":2}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscaFiltraPorTipoETexto() throws Exception {
        criarServico(token, "OFERTA", "Aulas de Calculo");
        criarServico(token, "OFERTA", "Edicao de video");
        criarServico(token, "PEDIDO", "Preciso de aulas de Calculo");

        mvc.perform(get("/servicos").param("tipo", "OFERTA").param("busca", "calculo")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Aulas de Calculo"));
    }

    @Test
    void servicoEncerradoSaiDaBusca() throws Exception {
        long id = criarServico(token, "PEDIDO", "Planilha");
        mvc.perform(delete("/servicos/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENCERRADO"));

        mvc.perform(get("/servicos").header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void outroAlunoNaoEditaServico() throws Exception {
        long id = criarServico(token, "OFERTA", "Logo");
        String outro = cadastrarELogar("Bruno", "bruno@una.br");
        alterar("/servicos/" + id, outro, "{\"tipo\":\"OFERTA\",\"titulo\":\"Hack\",\"preco\":1,\"prazoDias\":1}")
                .andExpect(status().isForbidden());
    }

    @Test
    void visualizarServicoInexistente() throws Exception {
        mvc.perform(get("/servicos/99999").header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
