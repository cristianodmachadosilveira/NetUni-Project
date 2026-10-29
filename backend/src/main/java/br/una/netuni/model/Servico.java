package br.una.netuni.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Pedido ou oferta publicado por um aluno (RF04). */
@Entity
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoServico tipo;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(length = 2000)
    private String descricao;

    /** Valor sugerido. O valor final e combinado na contratacao e pago FORA do NetUni. */
    @Column(precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "prazo_dias")
    private Integer prazoDias;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private StatusServico status = StatusServico.ABERTO;

    /** Autor do servico (aluno_id). */
    @ManyToOne(optional = false)
    @JoinColumn(name = "aluno_id")
    private Aluno autor;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoServico getTipo() { return tipo; }
    public void setTipo(TipoServico tipo) { this.tipo = tipo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public Integer getPrazoDias() { return prazoDias; }
    public void setPrazoDias(Integer prazoDias) { this.prazoDias = prazoDias; }

    public StatusServico getStatus() { return status; }
    public void setStatus(StatusServico status) { this.status = status; }

    public Aluno getAutor() { return autor; }
    public void setAutor(Aluno autor) { this.autor = autor; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}
