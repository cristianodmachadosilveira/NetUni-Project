package br.una.netuni.service;

import br.una.netuni.dto.ComentarioRequest;
import br.una.netuni.dto.ComentarioResponse;
import br.una.netuni.dto.CurtidaResponse;
import br.una.netuni.dto.PostRequest;
import br.una.netuni.dto.PostResponse;
import br.una.netuni.exception.RegraDeNegocioException;
import br.una.netuni.model.Aluno;
import br.una.netuni.model.Comentario;
import br.una.netuni.model.Comunidade;
import br.una.netuni.model.Curtida;
import br.una.netuni.model.Post;
import br.una.netuni.model.TipoMidia;
import br.una.netuni.repository.ComentarioRepository;
import br.una.netuni.repository.CurtidaRepository;
import br.una.netuni.repository.PostRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** RF14 / RF15 - Feed no estilo de video curto: publicar, ranquear, curtir e comentar. */
@Service
public class PostService {

    // Pesos do algoritmo do feed "Para voce".
    public static final int PESO_CURTIDA = 2;
    public static final int PESO_COMENTARIO = 3;
    public static final int BONUS_MINHA_COMUNIDADE = 10;
    public static final double HORAS_PARA_PERDER_METADE = 12.0;

    public static final int LIMITE_PADRAO = 30;
    public static final int LIMITE_MAXIMO = 50;

    private final PostRepository postRepository;
    private final CurtidaRepository curtidaRepository;
    private final ComentarioRepository comentarioRepository;
    private final ComunidadeService comunidadeService;
    private final UsuarioService usuarioService;

    public PostService(PostRepository postRepository, CurtidaRepository curtidaRepository,
                       ComentarioRepository comentarioRepository, ComunidadeService comunidadeService,
                       UsuarioService usuarioService) {
        this.postRepository = postRepository;
        this.curtidaRepository = curtidaRepository;
        this.comentarioRepository = comentarioRepository;
        this.comunidadeService = comunidadeService;
        this.usuarioService = usuarioService;
    }

    // =============== RF14 - Publicar ===============
    @Transactional
    public PostResponse publicar(Long alunoLogadoId, PostRequest dados) {
        Aluno autor = usuarioService.buscarAluno(alunoLogadoId);
        String legenda = dados.legenda == null ? "" : dados.legenda.trim();
        String midia = dados.midiaUrl == null ? "" : dados.midiaUrl.trim();

        if (dados.tipoMidia == TipoMidia.TEXTO) {
            if (legenda.isEmpty()) {
                throw RegraDeNegocioException.dadosInvalidos("Escreva alguma coisa no post de texto");
            }
            midia = "";
        } else if (midia.isEmpty()) {
            throw RegraDeNegocioException.dadosInvalidos("Envie a imagem ou o video antes de publicar");
        } else if (!enderecoDeMidiaValido(midia)) {
            throw RegraDeNegocioException.dadosInvalidos("Endereco de midia invalido");
        }

        Post post = new Post();
        post.setAutor(autor);
        post.setTipoMidia(dados.tipoMidia);
        post.setLegenda(legenda.isEmpty() ? null : legenda);
        post.setMidiaUrl(midia.isEmpty() ? null : midia);

        if (dados.comunidadeId != null) {
            Comunidade comunidade = comunidadeService.buscarComunidade(dados.comunidadeId);
            if (!comunidadeService.ehMembro(comunidade, alunoLogadoId)) {
                throw RegraDeNegocioException.semPermissao("Entre na comunidade para publicar nela");
            }
            post.setComunidade(comunidade);
        }
        postRepository.save(post);
        return montarResposta(post, alunoLogadoId);
    }

    // =============== RF15 - Feed ===============
    /**
     * Sem comunidade: feed "Para voce", ranqueado pela pontuacao.
     * Com comunidade: posts daquela comunidade, do mais novo para o mais antigo.
     */
    @Transactional(readOnly = true)
    public List<PostResponse> feed(Long alunoLogadoId, Long comunidadeId, Integer limite) {
        int quantidade = LIMITE_PADRAO;
        if (limite != null) {
            if (limite < 1) {
                quantidade = 1;
            } else if (limite > LIMITE_MAXIMO) {
                quantidade = LIMITE_MAXIMO;
            } else {
                quantidade = limite;
            }
        }

        List<Post> candidatos;
        if (comunidadeId != null) {
            comunidadeService.buscarComunidade(comunidadeId);   // 404 se nao existir
            candidatos = postRepository.findByComunidadeIdOrderByDataDescIdDesc(comunidadeId);
        } else {
            candidatos = postRepository.findTop100ByOrderByDataDescIdDesc();
        }

        Set<Long> minhasComunidades = comunidadeService.idsDasComunidadesDoAluno(alunoLogadoId);
        LocalDateTime agora = LocalDateTime.now();

        // Percorre os candidatos montando a resposta e calculando a pontuacao de cada um.
        List<PostResponse> itens = new ArrayList<>();
        for (Post post : candidatos) {
            PostResponse r = montarResposta(post, alunoLogadoId);
            boolean daMinhaComunidade = post.getComunidade() != null
                    && minhasComunidades.contains(post.getComunidade().getId());
            long horas = Duration.between(post.getData(), agora).toHours();
            r.pontuacao = calcularPontuacao(r.curtidas, r.comentarios, daMinhaComunidade, horas);
            itens.add(r);
        }

        if (comunidadeId == null) {
            // Maior pontuacao primeiro. O sort e estavel: em empate fica o mais recente.
            itens.sort(Comparator.comparingDouble((PostResponse p) -> p.pontuacao).reversed());
        }

        List<PostResponse> resultado = new ArrayList<>();
        for (int i = 0; i < itens.size() && i < quantidade; i++) {
            resultado.add(itens.get(i));
        }
        return resultado;
    }

    /**
     * Algoritmo do feed: engajamento + bonus de comunidade, dividido pelo "envelhecimento".
     * Um post com 12 horas vale metade de um igual recem-publicado.
     */
    public static double calcularPontuacao(long curtidas, long comentarios, boolean daMinhaComunidade,
                                           long horasDesdePost) {
        double pontos = 1 + curtidas * PESO_CURTIDA + comentarios * PESO_COMENTARIO;
        if (daMinhaComunidade) {
            pontos = pontos + BONUS_MINHA_COMUNIDADE;
        }
        if (horasDesdePost < 0) {
            horasDesdePost = 0;
        }
        double envelhecimento = 1 + horasDesdePost / HORAS_PARA_PERDER_METADE;
        return Math.round(pontos / envelhecimento * 100) / 100.0;
    }

    @Transactional(readOnly = true)
    public List<PostResponse> postsDoAluno(Long autorId, Long alunoLogadoId) {
        usuarioService.buscarAluno(autorId);
        List<PostResponse> lista = new ArrayList<>();
        for (Post post : postRepository.findByAutorIdOrderByDataDescIdDesc(autorId)) {
            lista.add(montarResposta(post, alunoLogadoId));
        }
        return lista;
    }

    @Transactional
    public void remover(Long postId, Long alunoLogadoId) {
        Post post = buscarPost(postId);
        if (!post.getAutor().getId().equals(alunoLogadoId)) {
            throw RegraDeNegocioException.semPermissao("Voce so pode apagar os seus posts");
        }
        curtidaRepository.deleteByPostId(postId);
        comentarioRepository.deleteByPostId(postId);
        postRepository.delete(post);
    }

    // =============== Curtir / descurtir ===============
    @Transactional
    public CurtidaResponse alternarCurtida(Long postId, Long alunoLogadoId) {
        Post post = buscarPost(postId);
        Optional<Curtida> existente = curtidaRepository.findByPostIdAndAlunoId(postId, alunoLogadoId);
        boolean curtiu;
        if (existente.isPresent()) {
            curtidaRepository.delete(existente.get());
            curtiu = false;
        } else {
            Curtida curtida = new Curtida();
            curtida.setPost(post);
            curtida.setAluno(usuarioService.buscarAluno(alunoLogadoId));
            curtidaRepository.save(curtida);
            curtiu = true;
        }
        curtidaRepository.flush();
        return new CurtidaResponse(curtiu, curtidaRepository.countByPostId(postId));
    }

    // =============== Comentarios ===============
    @Transactional(readOnly = true)
    public List<ComentarioResponse> listarComentarios(Long postId) {
        buscarPost(postId);
        List<ComentarioResponse> lista = new ArrayList<>();
        for (Comentario c : comentarioRepository.findByPostIdOrderByDataAscIdAsc(postId)) {
            lista.add(ComentarioResponse.de(c));
        }
        return lista;
    }

    @Transactional
    public ComentarioResponse comentar(Long postId, Long alunoLogadoId, ComentarioRequest dados) {
        Comentario c = new Comentario();
        c.setPost(buscarPost(postId));
        c.setAutor(usuarioService.buscarAluno(alunoLogadoId));
        c.setTexto(dados.texto.trim());
        comentarioRepository.save(c);
        return ComentarioResponse.de(c);
    }

    // =============== Apoio ===============
    public Post buscarPost(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw RegraDeNegocioException.naoEncontrado("Post nao encontrado");
        }
        return post.get();
    }

    private PostResponse montarResposta(Post post, Long alunoLogadoId) {
        long curtidas = curtidaRepository.countByPostId(post.getId());
        long comentarios = comentarioRepository.countByPostId(post.getId());
        boolean curtiu = curtidaRepository.findByPostIdAndAlunoId(post.getId(), alunoLogadoId).isPresent();
        return PostResponse.de(post, curtidas, comentarios, curtiu, alunoLogadoId);
    }

    /** Aceita arquivos enviados ao NetUni (/midias/...), os de demonstracao (/demo/...) ou links https. */
    static boolean enderecoDeMidiaValido(String url) {
        if (url.contains("..") || url.contains(" ")) {
            return false;
        }
        return url.startsWith("/midias/") || url.startsWith("/demo/") || url.startsWith("https://");
    }
}
