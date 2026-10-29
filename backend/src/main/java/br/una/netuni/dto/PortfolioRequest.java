package br.una.netuni.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Entrada do RF12 - adicionar item manual ao portfolio. */
public class PortfolioRequest {

    @NotBlank(message = "e obrigatorio")
    @Size(max = 120, message = "maximo 120 caracteres")
    public String titulo;

    @Size(max = 1000, message = "maximo 1000 caracteres")
    public String descricao;

    @Size(max = 300, message = "maximo 300 caracteres")
    public String linkProjeto;
}
