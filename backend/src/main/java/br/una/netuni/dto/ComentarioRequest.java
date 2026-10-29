package br.una.netuni.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ComentarioRequest {

    @NotBlank(message = "nao pode ser vazio")
    @Size(max = 500, message = "maximo 500 caracteres")
    public String texto;
}
