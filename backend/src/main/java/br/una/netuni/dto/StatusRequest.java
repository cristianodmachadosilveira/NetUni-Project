package br.una.netuni.dto;

import br.una.netuni.model.StatusContratacao;
import jakarta.validation.constraints.NotNull;

/** Entrada para mudar o status de uma contratacao (RF10). */
public class StatusRequest {

    @NotNull(message = "e obrigatorio")
    public StatusContratacao status;
}
