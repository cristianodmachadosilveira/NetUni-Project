package br.una.netuni.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Token de login (RF02). Tabela tecnica, fora do DER de negocio. */
@Entity
public class Sessao {

    @Id
    @Column(length = 64)
    private String token;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @Column(name = "expira_em", nullable = false)
    private LocalDateTime expiraEm;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }

    public LocalDateTime getExpiraEm() { return expiraEm; }
    public void setExpiraEm(LocalDateTime expiraEm) { this.expiraEm = expiraEm; }
}
