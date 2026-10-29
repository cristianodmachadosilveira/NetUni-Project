package br.una.netuni.dto;

import br.una.netuni.model.TipoMidia;

/** Resposta do upload: o endereco para usar no post e se e IMAGEM ou VIDEO. */
public class MidiaResponse {

    public String url;
    public TipoMidia tipoMidia;

    public MidiaResponse(String url, TipoMidia tipoMidia) {
        this.url = url;
        this.tipoMidia = tipoMidia;
    }
}
