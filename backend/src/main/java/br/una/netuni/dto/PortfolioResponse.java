package br.una.netuni.dto;

import br.una.netuni.model.Portfolio;
import java.time.LocalDateTime;

public class PortfolioResponse {

    public Long id;
    public String titulo;
    public String descricao;
    public String linkProjeto;
    /** Preenchido quando o item veio de um servico concluido no NetUni. */
    public Long contratacaoId;
    public LocalDateTime data;

    public static PortfolioResponse de(Portfolio p) {
        PortfolioResponse r = new PortfolioResponse();
        r.id = p.getId();
        r.titulo = p.getTitulo();
        r.descricao = p.getDescricao();
        r.linkProjeto = p.getLinkProjeto();
        if (p.getContratacao() != null) {
            r.contratacaoId = p.getContratacao().getId();
        }
        r.data = p.getData();
        return r;
    }
}
