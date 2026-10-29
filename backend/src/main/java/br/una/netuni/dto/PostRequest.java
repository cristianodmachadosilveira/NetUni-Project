package br.una.netuni.dto;

import br.una.netuni.model.TipoMidia;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Entrada do RF14 - publicar no feed. A midia e enviada antes em POST /midias. */
public class PostRequest {

    /** Opcional. Se vier, o aluno precisa participar da comunidade. */
    public Long comunidadeId;

    @NotNull(message = "e obrigatorio (TEXTO, IMAGEM ou VIDEO)")
    public TipoMidia tipoMidia;

    @Size(max = 500, message = "maximo 500 caracteres")
    public String legenda;

    @Size(max = 300, message = "maximo 300 caracteres")
    public String midiaUrl;
}
