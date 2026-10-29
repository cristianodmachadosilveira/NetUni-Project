package br.una.netuni.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Entrada do RF11 - Avaliacao. A faixa da nota (1 a 5) e conferida no AvaliacaoService. */
public class AvaliacaoRequest {

    @NotNull(message = "e obrigatorio")
    public Long contratacaoId;

    @NotNull(message = "e obrigatoria")
    public Integer nota;

    @Size(max = 500, message = "maximo 500 caracteres")
    public String comentario;
}
