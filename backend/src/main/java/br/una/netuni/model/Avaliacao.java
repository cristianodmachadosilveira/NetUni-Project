package br.una.netuni.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Avaliacao do contratante sobre o prestador. 1:1 com Contratacao (RF11). */
@Entity
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** unique = true garante no maximo uma avaliacao por contratacao. */
    @OneToOne(optional = false)
    @JoinColumn(name = "contratacao_id", unique = true)
    private Contratacao contratacao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "avaliador_id")
    private Aluno avaliador;

    @Column(nullable = false)
    private Integer nota;

    @Column(length = 500)
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime data = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Contratacao getContratacao() { return contratacao; }
    public void setContratacao(Contratacao contratacao) { this.contratacao = contratacao; }

    public Aluno getAvaliador() { return avaliador; }
    public void setAvaliador(Aluno avaliador) { this.avaliador = avaliador; }

    public Integer getNota() { return nota; }
    public void setNota(Integer nota) { this.nota = nota; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }
}
