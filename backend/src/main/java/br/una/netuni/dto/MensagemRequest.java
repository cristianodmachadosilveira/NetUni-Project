package br.una.netuni.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Entrada do RF08 - Comunicacao. */
public class MensagemRequest {

    @NotBlank(message = "nao pode ser vazio")
    @Size(max = 1000, message = "maximo 1000 caracteres")
    public String conteudo;
}
