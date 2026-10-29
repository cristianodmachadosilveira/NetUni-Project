package br.una.netuni.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Item do portfolio de um aluno (RF12). Pode nascer de uma contratacao concluida. */
@Entity
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    /** Opcional: preenchido quando o item vem de um servico concluido no NetUni. */
    @ManyToOne
    @JoinColumn(name = "contratacao_id")
    private Contratacao contratacao;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(length = 1000)
    private String descricao;

    @Column(name = "link_projeto", length = 300)
    private String linkProjeto;

    @Column(nullable = false)
    private LocalDateTime data = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }

    public Contratacao getContratacao() { return contratacao; }
    public void setContratacao(Contratacao contratacao) { this.contratacao = contratacao; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getLinkProjeto() { return linkProjeto; }
    public void setLinkProjeto(String linkProjeto) { this.linkProjeto = linkProjeto; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }
}
