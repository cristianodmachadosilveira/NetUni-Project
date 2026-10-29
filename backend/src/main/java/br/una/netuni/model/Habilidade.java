package br.una.netuni.model;

import jakarta.persistence.*;

/** Habilidade que um aluno informa no perfil (ex.: Design, Java, Excel). */
@Entity
public class Habilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String nome;

    public Habilidade() {
    }

    public Habilidade(String nome) {
        this.nome = nome;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
