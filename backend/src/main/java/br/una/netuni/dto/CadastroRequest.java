package br.una.netuni.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Entrada do RF01 - Cadastro de usuario. */
public class CadastroRequest {

    @NotBlank(message = "e obrigatorio")
    @Size(max = 100, message = "maximo 100 caracteres")
    public String nome;

    @NotBlank(message = "e obrigatorio")
    @Email(message = "formato invalido")
    @Size(max = 120, message = "maximo 120 caracteres")
    public String email;

    @NotBlank(message = "e obrigatoria")
    @Size(min = 6, max = 60, message = "deve ter entre 6 e 60 caracteres")
    public String senha;

    @Size(max = 100, message = "maximo 100 caracteres")
    public String curso;

    @Min(value = 1, message = "minimo 1")
    @Max(value = 12, message = "maximo 12")
    public Integer semestre;
}
