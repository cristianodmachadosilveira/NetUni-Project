package br.una.netuni.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Post salvo por um aluno (aparece na aba "Salvos" do perfil). Cada aluno salva um post uma vez. */
@Entity
@Table(name = "salvamento", uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "aluno_id"}))
public class Salvamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @Column(nullable = false)
    private LocalDateTime data = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }

    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }
}
