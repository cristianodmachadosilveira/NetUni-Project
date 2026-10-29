package br.una.netuni.dto;

import br.una.netuni.model.Comentario;
import java.time.LocalDateTime;

public class ComentarioResponse {

    public Long id;
    public AlunoResumo autor;
    public String texto;
    public LocalDateTime data;

    public static ComentarioResponse de(Comentario c) {
        ComentarioResponse r = new ComentarioResponse();
        r.id = c.getId();
        r.autor = AlunoResumo.de(c.getAutor());
        r.texto = c.getTexto();
        r.data = c.getData();
        return r;
    }
}
