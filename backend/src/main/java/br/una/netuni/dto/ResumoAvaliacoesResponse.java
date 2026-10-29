package br.una.netuni.dto;

import java.util.ArrayList;
import java.util.List;

/** Avaliacoes recebidas por um aluno + media calculada (RF11/RF12). */
public class ResumoAvaliacoesResponse {

    public double media;
    public int total;
    public List<AvaliacaoResponse> avaliacoes = new ArrayList<>();
}
