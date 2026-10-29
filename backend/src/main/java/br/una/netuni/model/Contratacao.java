package br.una.netuni.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Solicitacao/contratacao de um servico entre dois alunos (RF09, RF10). */
@Entity
public class Contratacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "servico_id")
    private Servico servico;

    /** Quem recebe o servico. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "contratante_id")
    private Aluno contratante;

    /** Quem executa o servico. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "prestador_id")
    private Aluno prestador;

    /** Quem abriu a solicitacao. O outro lado (autor do servico) e quem aceita. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "solicitante_id")
    private Aluno solicitante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusContratacao status = StatusContratacao.SOLICITADA;

    /** Apenas registro do que foi combinado. NAO ha processamento de pagamento. */
    @Column(name = "valor_negociado", precision = 10, scale = 2)
    private BigDecimal valorNegociado;

    /** Ex.: Pix, dinheiro, link de pagamento. Pagamento acontece fora do app. */
    @Column(name = "forma_pagamento", length = 60)
    private String formaPagamento;

    @Column(length = 500)
    private String observacoes;

    @Column(nullable = false)
    private LocalDateTime data = LocalDateTime.now();

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Servico getServico() { return servico; }
    public void setServico(Servico servico) { this.servico = servico; }

    public Aluno getContratante() { return contratante; }
    public void setContratante(Aluno contratante) { this.contratante = contratante; }

    public Aluno getPrestador() { return prestador; }
    public void setPrestador(Aluno prestador) { this.prestador = prestador; }

    public Aluno getSolicitante() { return solicitante; }
    public void setSolicitante(Aluno solicitante) { this.solicitante = solicitante; }

    public StatusContratacao getStatus() { return status; }
    public void setStatus(StatusContratacao status) { this.status = status; }

    public BigDecimal getValorNegociado() { return valorNegociado; }
    public void setValorNegociado(BigDecimal valorNegociado) { this.valorNegociado = valorNegociado; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }

    public LocalDateTime getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }
}
