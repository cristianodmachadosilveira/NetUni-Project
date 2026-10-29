package br.una.netuni.service;

import br.una.netuni.dto.ComunidadeRequest;
import br.una.netuni.dto.ComunidadeResponse;
import br.una.netuni.exception.RegraDeNegocioException;
import br.una.netuni.model.Aluno;
import br.una.netuni.model.Comunidade;
import br.una.netuni.repository.ComunidadeRepository;
import br.una.netuni.repository.PostRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** RF13 - Comunidades: listar, criar, entrar e sair. */
@Service
public class ComunidadeService {

    public static final String COR_PADRAO = "#2f6bff";
    public static final String ICONE_PADRAO = "🪐";

    private final ComunidadeRepository comunidadeRepository;
    private final PostRepository postRepository;
    private final UsuarioService usuarioService;

    public ComunidadeService(ComunidadeRepository comunidadeRepository, PostRepository postRepository,
                             UsuarioService usuarioService) {
        this.comunidadeRepository = comunidadeRepository;
        this.postRepository = postRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public List<ComunidadeResponse> listar(Long alunoLogadoId) {
        List<ComunidadeResponse> lista = new ArrayList<>();
        for (Comunidade c : comunidadeRepository.findAllByOrderByNomeAsc()) {
            lista.add(montarResposta(c, alunoLogadoId));
        }
        return lista;
    }

    @Transactional(readOnly = true)
    public ComunidadeResponse detalhar(Long id, Long alunoLogadoId) {
        return montarResposta(buscarComunidade(id), alunoLogadoId);
    }

    @Transactional
    public ComunidadeResponse criar(Long alunoLogadoId, ComunidadeRequest dados) {
        String nome = dados.nome.trim();
        if (comunidadeRepository.existsByNomeIgnoreCase(nome)) {
            throw RegraDeNegocioException.conflito("Ja existe uma comunidade com esse nome");
        }
        Aluno criador = usuarioService.buscarAluno(alunoLogadoId);

        Comunidade c = new Comunidade();
        c.setNome(nome);
        c.setDescricao(dados.descricao);
        c.setIcone(textoOuPadrao(dados.icone, ICONE_PADRAO));
        c.setCor(textoOuPadrao(dados.cor, COR_PADRAO));
        c.setCriador(criador);
        c.getMembros().add(criador);   // quem cria ja participa
        comunidadeRepository.save(c);
        return montarResposta(c, alunoLogadoId);
    }

    @Transactional
    public ComunidadeResponse entrar(Long id, Long alunoLogadoId) {
        Comunidade c = buscarComunidade(id);
        if (ehMembro(c, alunoLogadoId)) {
            throw RegraDeNegocioException.conflito("Voce ja participa desta comunidade");
        }
        c.getMembros().add(usuarioService.buscarAluno(alunoLogadoId));
        return montarResposta(c, alunoLogadoId);
    }

    @Transactional
    public ComunidadeResponse sair(Long id, Long alunoLogadoId) {
        Comunidade c = buscarComunidade(id);
        if (c.getCriador().getId().equals(alunoLogadoId)) {
            throw RegraDeNegocioException.dadosInvalidos("Quem criou a comunidade nao pode sair dela");
        }
        // Percorre os membros e remove o aluno logado, se encontrar.
        boolean removido = false;
        Iterator<Aluno> it = c.getMembros().iterator();
        while (it.hasNext()) {
            if (it.next().getId().equals(alunoLogadoId)) {
                it.remove();
                removido = true;
            }
        }
        if (!removido) {
            throw RegraDeNegocioException.conflito("Voce nao participa desta comunidade");
        }
        return montarResposta(c, alunoLogadoId);
    }

    /** Percorre a lista de membros procurando o aluno. */
    public boolean ehMembro(Comunidade c, Long alunoId) {
        for (Aluno membro : c.getMembros()) {
            if (membro.getId().equals(alunoId)) {
                return true;
            }
        }
        return false;
    }

    public Comunidade buscarComunidade(Long id) {
        Optional<Comunidade> c = comunidadeRepository.findById(id);
        if (c.isEmpty()) {
            throw RegraDeNegocioException.naoEncontrado("Comunidade nao encontrada");
        }
        return c.get();
    }

    public Set<Long> idsDasComunidadesDoAluno(Long alunoId) {
        return new HashSet<>(comunidadeRepository.idsDasComunidadesDoAluno(alunoId));
    }

    private ComunidadeResponse montarResposta(Comunidade c, Long alunoLogadoId) {
        long posts = postRepository.countByComunidadeId(c.getId());
        return ComunidadeResponse.de(c, posts, ehMembro(c, alunoLogadoId));
    }

    private static String textoOuPadrao(String valor, String padrao) {
        if (valor == null || valor.isBlank()) {
            return padrao;
        }
        return valor.trim();
    }
}
