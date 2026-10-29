package br.una.netuni.dto;

import br.una.netuni.model.TipoServico;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/** Entrada do RF04/RF05 - Publicar ou editar servico. */
public class ServicoRequest {

    @NotNull(message = "e obrigatorio (PEDIDO ou OFERTA)")
    public TipoServico tipo;

    @NotBlank(message = "e obrigatorio")
    @Size(max = 120, message = "maximo 120 caracteres")
    public String titulo;

    @Size(max = 2000, message = "maximo 2000 caracteres")
    public String descricao;

    @NotNull(message = "e obrigatorio")
    @PositiveOrZero(message = "nao pode ser negativo")
    public BigDecimal preco;

    @NotNull(message = "e obrigatorio")
    @Positive(message = "deve ser maior que zero")
    @Max(value = 365, message = "maximo 365 dias")
    public Integer prazoDias;
}
