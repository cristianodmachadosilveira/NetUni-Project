package br.una.netuni.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Mensagem do chat de uma contratacao (RF08). */
@Entity
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "contratacao_id")
    private Contratacao contratacao;

    /** null = aviso automatico do sistema (ex.: "Fulano aceitou"). */
    @ManyToOne
    @JoinColumn(name = "remetente_id")
    private Aluno remetente;

    @Column(nullable = false, length = 1000)
    private String conteudo;

    @Column(nullable = false)
    private LocalDateTime data = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Contratacao getContratacao() { return contratacao; }
    public void setContratacao(Contratacao contratacao) { this.contratacao = contratacao; }

    public Aluno getRemetente() { return remetente; }
    public void setRemetente(Aluno remetente) { this.remetente = remetente; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }
}
