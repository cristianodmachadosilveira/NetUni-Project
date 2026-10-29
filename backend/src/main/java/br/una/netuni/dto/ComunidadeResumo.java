package br.una.netuni.dto;

import br.una.netuni.model.Comunidade;

/** Versao curta da comunidade, usada dentro dos posts. */
public class ComunidadeResumo {

    public Long id;
    public String nome;
    public String icone;
    public String cor;

    public static ComunidadeResumo de(Comunidade c) {
        if (c == null) {
            return null;
        }
        ComunidadeResumo r = new ComunidadeResumo();
        r.id = c.getId();
        r.nome = c.getNome();
        r.icone = c.getIcone();
        r.cor = c.getCor();
        return r;
    }
}
