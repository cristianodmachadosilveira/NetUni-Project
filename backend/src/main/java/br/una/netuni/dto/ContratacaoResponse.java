package br.una.netuni.dto;

import br.una.netuni.model.StatusContratacao;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ContratacaoResponse {

    public Long id;
    public ServicoResponse servico;
    public AlunoResumo contratante;
    public AlunoResumo prestador;
    public Long solicitanteId;
    public StatusContratacao status;
    public BigDecimal valorNegociado;
    public String formaPagamento;
    public String observacoes;
    public LocalDateTime data;
    public LocalDateTime dataConclusao;
    public boolean avaliada;
    /** O que o aluno logado pode fazer agora. O app so mostra esses botoes. */
    public List<String> acoesPermitidas = new ArrayList<>();
}
