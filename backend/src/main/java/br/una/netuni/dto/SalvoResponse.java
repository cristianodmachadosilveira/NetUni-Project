package br.una.netuni.dto;

/** Resultado de salvar/tirar dos salvos: o estado novo e quantas vezes o post foi salvo. */
public class SalvoResponse {

    public boolean salvou;
    public long salvos;

    public SalvoResponse(boolean salvou, long salvos) {
        this.salvou = salvou;
        this.salvos = salvos;
    }
}
