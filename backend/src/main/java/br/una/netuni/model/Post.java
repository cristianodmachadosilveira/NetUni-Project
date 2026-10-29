package br.una.netuni.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** RF14 - Publicacao do feed: texto, imagem ou video curto, opcionalmente dentro de uma comunidade. */
@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "autor_id")
    private Aluno autor;

    /** null = post publico, fora de comunidade. */
    @ManyToOne
    @JoinColumn(name = "comunidade_id")
    private Comunidade comunidade;

    @Column(length = 500)
    private String legenda;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_midia", nullable = false, length = 10)
    private TipoMidia tipoMidia;

    /** Endereco da imagem ou do video (ex.: /midias/abc.mp4). Vazio para TEXTO. */
    @Column(name = "midia_url", length = 300)
    private String midiaUrl;

    @Column(nullable = false)
    private LocalDateTime data = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Aluno getAutor() { return autor; }
    public void setAutor(Aluno autor) { this.autor = autor; }

    public Comunidade getComunidade() { return comunidade; }
    public void setComunidade(Comunidade comunidade) { this.comunidade = comunidade; }

    public String getLegenda() { return legenda; }
    public void setLegenda(String legenda) { this.legenda = legenda; }

    public TipoMidia getTipoMidia() { return tipoMidia; }
    public void setTipoMidia(TipoMidia tipoMidia) { this.tipoMidia = tipoMidia; }

    public String getMidiaUrl() { return midiaUrl; }
    public void setMidiaUrl(String midiaUrl) { this.midiaUrl = midiaUrl; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }
}
