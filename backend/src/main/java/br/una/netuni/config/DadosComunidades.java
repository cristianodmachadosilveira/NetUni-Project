package br.una.netuni.config;

import br.una.netuni.model.Aluno;
import br.una.netuni.model.Comentario;
import br.una.netuni.model.Comunidade;
import br.una.netuni.model.Post;
import br.una.netuni.model.Salvamento;
import br.una.netuni.model.TipoMidia;
import br.una.netuni.repository.AlunoRepository;
import br.una.netuni.repository.ComentarioRepository;
import br.una.netuni.repository.ComunidadeRepository;
import br.una.netuni.repository.PostRepository;
import br.una.netuni.repository.SalvamentoRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Comunidades e posts de demonstracao para o feed (roda depois de DadosDemonstracao).
 * As midias ficam em src/main/resources/static/demo e foram geradas pelo grupo.
 */
@Component
@Order(2)
public class DadosComunidades implements CommandLineRunner {

    private final AlunoRepository alunoRepository;
    private final ComunidadeRepository comunidadeRepository;
    private final PostRepository postRepository;
    private final SalvamentoRepository salvamentoRepository;
    private final ComentarioRepository comentarioRepository;
    private final boolean ativo;

    public DadosComunidades(AlunoRepository alunoRepository, ComunidadeRepository comunidadeRepository,
                            PostRepository postRepository, SalvamentoRepository salvamentoRepository,
                            ComentarioRepository comentarioRepository,
                            @Value("${netuni.dados-demonstracao:true}") boolean ativo) {
        this.alunoRepository = alunoRepository;
        this.comunidadeRepository = comunidadeRepository;
        this.postRepository = postRepository;
        this.salvamentoRepository = salvamentoRepository;
        this.comentarioRepository = comentarioRepository;
        this.ativo = ativo;
    }

    @Override
    public void run(String... args) {
        if (!ativo || comunidadeRepository.count() > 0) {
            return;
        }
        Optional<Aluno> ana = alunoRepository.findByEmail("ana@una.br");
        Optional<Aluno> bruno = alunoRepository.findByEmail("bruno@una.br");
        Optional<Aluno> carla = alunoRepository.findByEmail("carla@una.br");
        if (ana.isEmpty() || bruno.isEmpty() || carla.isEmpty()) {
            return;   // banco sem os alunos de demonstracao
        }
        Aluno a = ana.get();
        Aluno b = bruno.get();
        Aluno c = carla.get();

        Comunidade dev = criarComunidade("Dev UNA", "Programação, projetos e dúvidas de código.", "💻", "#2f6bff", b, a);
        Comunidade calculo = criarComunidade("Cálculo sem Medo", "Monitoria, listas resolvidas e dicas de prova.", "📐", "#19b8ff", b, c);
        Comunidade design = criarComunidade("Design & Criação", "Logos, paletas, portfólio e feedback.", "🎨", "#8b5cf6", a, c);
        Comunidade empreende = criarComunidade("Empreendedorismo UNA", "Ideias de negócio, pitch e empresa júnior.", "🚀", "#f59e0b", c, a);
        Comunidade campus = criarComunidade("Vida no Campus", "Grupos de estudo, organização e o dia a dia na UNA.", "☕", "#2ee6a0", c, a, b);

        Post p1 = criarPost(a, design, TipoMidia.VIDEO, "/demo/paleta.mp4",
                "Montei essa paleta inspirada em Netuno. Pode usar no seu projeto!", 1);
        Post p2 = criarPost(b, dev, TipoMidia.VIDEO, "/demo/codigo-for.mp4",
                "Calculei a média das notas com um for. Quem mais está aprendendo Java esse semestre?", 2);
        Post p3 = criarPost(c, empreende, TipoMidia.IMAGEM, "/demo/pitch-1-minuto.webp",
                "Estrutura que uso para treinar pitch. Quem topa praticar junto?", 3);
        Post p4 = criarPost(b, calculo, TipoMidia.VIDEO, "/demo/derivada.mp4",
                "Derivada em 7 segundos: é a inclinação da reta que encosta na curva. Monitoria quinta às 18h!", 5);
        Post p5 = criarPost(c, campus, TipoMidia.IMAGEM, "/demo/habitos-semestre.webp",
                "O que salvou meu semestre passado. Qual hábito você acrescentaria?", 8);
        criarPost(c, campus, TipoMidia.TEXTO, null,
                "Alguém topa montar um grupo de estudo de Estrutura de Dados na biblioteca? Quinta, 18h.", 0);
        criarPost(b, null, TipoMidia.TEXTO, null,
                "Dica: antes de pedir ajuda com código, escreva o que você esperava e o que aconteceu. "
                        + "Metade das vezes você acha o erro sozinho.", 12);
        Post p8 = criarPost(a, design, TipoMidia.IMAGEM, "/demo/logo-conceito.webp",
                "Estudo de logo para uma atlética. Feedback é bem-vindo!", 20);

        salvar(p1, b); salvar(p1, c);
        salvar(p2, a); salvar(p2, c);
        salvar(p3, a);
        salvar(p4, c); salvar(p4, a);
        salvar(p5, a); salvar(p5, b);
        salvar(p8, c);

        comentar(p1, c, "Vou usar no slide da A3!");
        comentar(p2, a, "Agora entendi o for each. Valeu!");
        comentar(p4, c, "Vou na monitoria de quinta.");
        comentar(p5, b, "Eu acrescentaria: dormir bem antes da prova.");
    }

    private Comunidade criarComunidade(String nome, String descricao, String icone, String cor, Aluno criador,
                                       Aluno... outrosMembros) {
        Comunidade comunidade = new Comunidade();
        comunidade.setNome(nome);
        comunidade.setDescricao(descricao);
        comunidade.setIcone(icone);
        comunidade.setCor(cor);
        comunidade.setCriador(criador);
        comunidade.getMembros().add(criador);
        for (Aluno membro : outrosMembros) {
            comunidade.getMembros().add(membro);
        }
        return comunidadeRepository.save(comunidade);
    }

    private Post criarPost(Aluno autor, Comunidade comunidade, TipoMidia tipo, String midiaUrl, String legenda,
                           int horasAtras) {
        Post post = new Post();
        post.setAutor(autor);
        post.setComunidade(comunidade);
        post.setTipoMidia(tipo);
        post.setMidiaUrl(midiaUrl);
        post.setLegenda(legenda);
        post.setData(LocalDateTime.now().minusHours(horasAtras).minusMinutes(horasAtras == 0 ? 30 : 0));
        return postRepository.save(post);
    }

    private void salvar(Post post, Aluno aluno) {
        Salvamento salvamento = new Salvamento();
        salvamento.setPost(post);
        salvamento.setAluno(aluno);
        salvamentoRepository.save(salvamento);
    }

    private void comentar(Post post, Aluno autor, String texto) {
        Comentario comentario = new Comentario();
        comentario.setPost(post);
        comentario.setAutor(autor);
        comentario.setTexto(texto);
        comentarioRepository.save(comentario);
    }
}
