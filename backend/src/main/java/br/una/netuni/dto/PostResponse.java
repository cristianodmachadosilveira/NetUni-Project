package br.una.netuni.dto;

import br.una.netuni.model.Post;
import br.una.netuni.model.TipoMidia;
import java.time.LocalDateTime;

public class PostResponse {

    public Long id;
    public AlunoResumo autor;
    public ComunidadeResumo comunidade;
    public String legenda;
    public TipoMidia tipoMidia;
    public String midiaUrl;
    public long salvos;
    public long comentarios;
    public boolean salvou;
    public boolean meu;
    /** Nota calculada pelo algoritmo do feed (so faz sentido no "Para voce"). */
    public double pontuacao;
    public LocalDateTime data;

    public static PostResponse de(Post p, long salvos, long comentarios, boolean salvou, Long alunoLogadoId) {
        PostResponse r = new PostResponse();
        r.id = p.getId();
        r.autor = AlunoResumo.de(p.getAutor());
        r.comunidade = ComunidadeResumo.de(p.getComunidade());
        r.legenda = p.getLegenda();
        r.tipoMidia = p.getTipoMidia();
        r.midiaUrl = p.getMidiaUrl();
        r.salvos = salvos;
        r.comentarios = comentarios;
        r.salvou = salvou;
        r.meu = p.getAutor().getId().equals(alunoLogadoId);
        r.data = p.getData();
        return r;
    }
}
