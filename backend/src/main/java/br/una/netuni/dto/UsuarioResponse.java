package br.una.netuni.dto;

import br.una.netuni.model.Aluno;
import br.una.netuni.model.Habilidade;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Saida com os dados do aluno. Nunca inclui a senha. */
public class UsuarioResponse {

    public Long id;
    public String nome;
    /** So preenchido quando o proprio aluno consulta o seu perfil. */
    public String email;
    public String curso;
    public Integer semestre;
    public String descricao;
    public List<String> habilidades = new ArrayList<>();
    public LocalDateTime dataCadastro;

    public static UsuarioResponse de(Aluno aluno, boolean mostrarEmail) {
        UsuarioResponse r = new UsuarioResponse();
        r.id = aluno.getId();
        r.nome = aluno.getNome();
        if (mostrarEmail) {
            r.email = aluno.getEmail();
        }
        r.curso = aluno.getCurso();
        r.semestre = aluno.getSemestre();
        r.descricao = aluno.getDescricao();
        r.dataCadastro = aluno.getDataCadastro();
        for (Habilidade h : aluno.getHabilidades()) {
            r.habilidades.add(h.getNome());
        }
        Collections.sort(r.habilidades);
        return r;
    }
}
