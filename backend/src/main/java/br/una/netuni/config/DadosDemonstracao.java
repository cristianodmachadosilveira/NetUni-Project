package br.una.netuni.config;

import br.una.netuni.model.Aluno;
import br.una.netuni.model.Habilidade;
import br.una.netuni.model.Servico;
import br.una.netuni.model.TipoServico;
import br.una.netuni.repository.AlunoRepository;
import br.una.netuni.repository.HabilidadeRepository;
import br.una.netuni.repository.ServicoRepository;
import br.una.netuni.service.SenhaService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Cria dados para a demonstracao da A3 quando o banco esta vazio.
 * Logins (senha 123456): ana@una.br, bruno@una.br, carla@una.br
 */
@Component
public class DadosDemonstracao implements CommandLineRunner {

    private final AlunoRepository alunoRepository;
    private final HabilidadeRepository habilidadeRepository;
    private final ServicoRepository servicoRepository;
    private final SenhaService senhaService;
    private final boolean ativo;

    public DadosDemonstracao(AlunoRepository alunoRepository, HabilidadeRepository habilidadeRepository,
                             ServicoRepository servicoRepository, SenhaService senhaService,
                             @Value("${netuni.dados-demonstracao:true}") boolean ativo) {
        this.alunoRepository = alunoRepository;
        this.habilidadeRepository = habilidadeRepository;
        this.servicoRepository = servicoRepository;
        this.senhaService = senhaService;
        this.ativo = ativo;
    }

    @Override
    public void run(String... args) {
        if (!ativo || alunoRepository.count() > 0) {
            return;
        }
        Aluno ana = criarAluno("Ana Souza", "ana@una.br", "Design Grafico", 4,
                "Crio logos e posts para redes sociais.", new String[] {"Design", "Canva"});
        Aluno bruno = criarAluno("Bruno Lima", "bruno@una.br", "Ciencia da Computacao", 6,
                "Monitor de Calculo e desenvolvedor web.", new String[] {"Java", "Calculo"});
        Aluno carla = criarAluno("Carla Dias", "carla@una.br", "Administracao", 2,
                "Organizada e boa com planilhas.", new String[] {"Excel"});

        criarServico(TipoServico.OFERTA, "Criacao de logo e identidade visual",
                "Logo + paleta de cores para seu projeto, atletica ou empresa junior.", "150.00", 5, ana);
        criarServico(TipoServico.OFERTA, "Aulas de reforco em Calculo I",
                "Aulas online de 1h com lista de exercicios resolvida.", "40.00", 7, bruno);
        criarServico(TipoServico.PEDIDO, "Planilha de controle financeiro",
                "Preciso de uma planilha simples de entradas e saidas para a republica.", "80.00", 3, carla);
        criarServico(TipoServico.PEDIDO, "Formatacao de TCC nas normas ABNT",
                "Trabalho de 40 paginas, preciso de revisao da formatacao.", "60.00", 4, ana);
    }

    private Aluno criarAluno(String nome, String email, String curso, int semestre, String descricao,
                             String[] habilidades) {
        Aluno aluno = new Aluno();
        aluno.setNome(nome);
        aluno.setEmail(email);
        aluno.setSenhaHash(senhaService.gerarHash("123456"));
        aluno.setCurso(curso);
        aluno.setSemestre(semestre);
        aluno.setDescricao(descricao);
        for (String nomeHabilidade : habilidades) {
            aluno.getHabilidades().add(habilidadeRepository.save(new Habilidade(nomeHabilidade)));
        }
        return alunoRepository.save(aluno);
    }

    private void criarServico(TipoServico tipo, String titulo, String descricao, String preco, int prazoDias,
                              Aluno autor) {
        Servico servico = new Servico();
        servico.setTipo(tipo);
        servico.setTitulo(titulo);
        servico.setDescricao(descricao);
        servico.setPreco(new BigDecimal(preco));
        servico.setPrazoDias(prazoDias);
        servico.setAutor(autor);
        servicoRepository.save(servico);
    }
}
