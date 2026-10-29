package br.una.netuni.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/** Estudante da UNA. Pode ser contratante, prestador ou os dois. */
@Entity
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    /** Nunca a senha em texto puro: "salt:hash" gerado pelo SenhaService. */
    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Column(length = 100)
    private String curso;

    private Integer semestre;

    @Column(length = 500)
    private String descricao;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro = LocalDateTime.now();

    /** Aluno N:N Habilidade, resolvido pela tabela aluno_habilidade. */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "aluno_habilidade",
            joinColumns = @JoinColumn(name = "aluno_id"),
            inverseJoinColumns = @JoinColumn(name = "habilidade_id"))
    private Set<Habilidade> habilidades = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    public Integer getSemestre() { return semestre; }
    public void setSemestre(Integer semestre) { this.semestre = semestre; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public Set<Habilidade> getHabilidades() { return habilidades; }
    public void setHabilidades(Set<Habilidade> habilidades) { this.habilidades = habilidades; }
}
