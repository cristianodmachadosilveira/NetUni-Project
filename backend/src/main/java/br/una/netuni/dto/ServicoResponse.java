package br.una.netuni.dto;

import br.una.netuni.model.Servico;
import br.una.netuni.model.StatusServico;
import br.una.netuni.model.TipoServico;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ServicoResponse {

    public Long id;
    public TipoServico tipo;
    public String titulo;
    public String descricao;
    public BigDecimal preco;
    public Integer prazoDias;
    public StatusServico status;
    public AlunoResumo autor;
    public LocalDateTime dataCriacao;

    public static ServicoResponse de(Servico s) {
        ServicoResponse r = new ServicoResponse();
        r.id = s.getId();
        r.tipo = s.getTipo();
        r.titulo = s.getTitulo();
        r.descricao = s.getDescricao();
        r.preco = s.getPreco();
        r.prazoDias = s.getPrazoDias();
        r.status = s.getStatus();
        r.autor = AlunoResumo.de(s.getAutor());
        r.dataCriacao = s.getDataCriacao();
        return r;
    }
}
