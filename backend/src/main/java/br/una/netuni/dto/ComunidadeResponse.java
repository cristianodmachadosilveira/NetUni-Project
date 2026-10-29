package br.una.netuni.dto;

import br.una.netuni.model.Comunidade;
import java.time.LocalDateTime;

public class ComunidadeResponse {

    public Long id;
    public String nome;
    public String descricao;
    public String icone;
    public String cor;
    public AlunoResumo criador;
    /** Quantos anuncios abertos usam esta comunidade como etiqueta. */
    public long anuncios;
    public LocalDateTime dataCriacao;

    public static ComunidadeResponse de(Comunidade c, long anuncios) {
        ComunidadeResponse r = new ComunidadeResponse();
        r.id = c.getId();
        r.nome = c.getNome();
        r.descricao = c.getDescricao();
        r.icone = c.getIcone();
        r.cor = c.getCor();
        r.criador = AlunoResumo.de(c.getCriador());
        r.anuncios = anuncios;
        r.dataCriacao = c.getDataCriacao();
        return r;
    }
}
