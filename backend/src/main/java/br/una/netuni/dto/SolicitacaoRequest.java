package br.una.netuni.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Entrada do RF09 - Solicitacao de servico. */
public class SolicitacaoRequest {

    @NotNull(message = "e obrigatorio")
    public Long servicoId;

    @Size(max = 500, message = "maximo 500 caracteres")
    public String observacoes;
}
