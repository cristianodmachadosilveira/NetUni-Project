package br.una.netuni.dto;

import br.una.netuni.model.Aluno;

/** Versao curta do aluno, usada dentro de servicos e contratacoes. */
public class AlunoResumo {

    public Long id;
    public String nome;
    public String curso;

    public static AlunoResumo de(Aluno aluno) {
        AlunoResumo r = new AlunoResumo();
        r.id = aluno.getId();
        r.nome = aluno.getNome();
        r.curso = aluno.getCurso();
        return r;
    }
}
