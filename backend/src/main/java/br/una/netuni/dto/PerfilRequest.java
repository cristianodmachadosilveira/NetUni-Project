package br.una.netuni.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/** Entrada do RF03 - Gerenciamento do perfil (inclui habilidades). */
public class PerfilRequest {

    @NotBlank(message = "e obrigatorio")
    @Size(max = 100, message = "maximo 100 caracteres")
    public String nome;

    @Size(max = 100, message = "maximo 100 caracteres")
    public String curso;

    @Min(value = 1, message = "minimo 1")
    @Max(value = 12, message = "maximo 12")
    public Integer semestre;

    @Size(max = 500, message = "maximo 500 caracteres")
    public String descricao;

    /** Nomes das habilidades, ex.: ["Java", "Design"]. Substitui a lista atual. */
    public List<String> habilidades = new ArrayList<>();
}
