package br.una.netuni.dto;

import br.una.netuni.model.Avaliacao;
import java.time.LocalDateTime;

public class AvaliacaoResponse {

    public Long id;
    public Long contratacaoId;
    public String servicoTitulo;
    public String avaliadorNome;
    public Integer nota;
    public String comentario;
    public LocalDateTime data;

    public static AvaliacaoResponse de(Avaliacao a) {
        AvaliacaoResponse r = new AvaliacaoResponse();
        r.id = a.getId();
        r.contratacaoId = a.getContratacao().getId();
        r.servicoTitulo = a.getContratacao().getServico().getTitulo();
        r.avaliadorNome = a.getAvaliador().getNome();
        r.nota = a.getNota();
        r.comentario = a.getComentario();
        r.data = a.getData();
        return r;
    }
}
