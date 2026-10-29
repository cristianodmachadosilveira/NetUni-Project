package br.una.netuni.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** RF13 - Comunidade (ex.: "Dev UNA"). Funciona como etiqueta dos anuncios: o servico pode ser ligado a uma. */
@Entity
public class Comunidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String nome;

    @Column(length = 300)
    private String descricao;

    /** Um emoji que aparece como "foto" da comunidade. */
    @Column(length = 16)
    private String icone;

    /** Cor da comunidade em hexadecimal, ex.: #2f6bff. */
    @Column(length = 7)
    private String cor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "criador_id")
    private Aluno criador;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getIcone() { return icone; }
    public void setIcone(String icone) { this.icone = icone; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public Aluno getCriador() { return criador; }
    public void setCriador(Aluno criador) { this.criador = criador; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}
