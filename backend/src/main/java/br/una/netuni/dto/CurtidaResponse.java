package br.una.netuni.dto;

/** Resultado de curtir/descurtir: o estado novo e o total de curtidas do post. */
public class CurtidaResponse {

    public boolean curtiu;
    public long curtidas;

    public CurtidaResponse(boolean curtiu, long curtidas) {
        this.curtiu = curtiu;
        this.curtidas = curtidas;
    }
}
