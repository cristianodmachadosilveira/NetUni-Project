package br.una.netuni.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Entrada do RF13 - criar comunidade. */
public class ComunidadeRequest {

    @NotBlank(message = "e obrigatorio")
    @Size(min = 3, max = 60, message = "deve ter entre 3 e 60 caracteres")
    public String nome;

    @Size(max = 300, message = "maximo 300 caracteres")
    public String descricao;

    @Size(max = 16, message = "maximo 16 caracteres")
    public String icone;

    @Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "deve ser uma cor no formato #2f6bff")
    public String cor;
}
