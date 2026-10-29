package br.una.netuni.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Entrada da negociacao (RF10). So REGISTRA o combinado:
 * o pagamento e feito fora do NetUni (Pix, dinheiro, link...).
 */
public class NegociacaoRequest {

    @NotNull(message = "e obrigatorio")
    @PositiveOrZero(message = "nao pode ser negativo")
    public BigDecimal valorNegociado;

    @NotBlank(message = "e obrigatoria")
    @Size(max = 60, message = "maximo 60 caracteres")
    public String formaPagamento;

    @Size(max = 500, message = "maximo 500 caracteres")
    public String observacoes;
}
