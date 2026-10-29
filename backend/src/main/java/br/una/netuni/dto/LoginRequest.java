package br.una.netuni.dto;

import jakarta.validation.constraints.NotBlank;

/** Entrada do RF02 - Login. */
public class LoginRequest {

    @NotBlank(message = "e obrigatorio")
    public String email;

    @NotBlank(message = "e obrigatoria")
    public String senha;
}
