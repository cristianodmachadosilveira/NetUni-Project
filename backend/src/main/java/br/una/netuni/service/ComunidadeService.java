package br.una.netuni.service;

import br.una.netuni.dto.ComunidadeRequest;
import br.una.netuni.dto.ComunidadeResponse;
import br.una.netuni.exception.RegraDeNegocioException;
import br.una.netuni.model.Comunidade;
import br.una.netuni.model.StatusServico;
import br.una.netuni.repository.ComunidadeRepository;
import br.una.netuni.repository.ServicoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RF13 - Comunidades. Sao etiquetas: o anunciante liga o servico a uma comunidade
 * e a aba Comunidades usa isso como filtro (GET /servicos?comunidadeId=...).
 */
@Service
public class ComunidadeService {

    public static final String COR_PADRAO = "#2f6bff";
    public static final String ICONE_PADRAO = "🪐";

    private final ComunidadeRepository comunidadeRepository;
    private final ServicoRepository servicoRepository;
    private final UsuarioService usuarioService;

    public ComunidadeService(ComunidadeRepository comunidadeRepository, ServicoRepository servicoRepository,
                             UsuarioService usuarioService) {
        this.comunidadeRepository = comunidadeRepository;
        this.servicoRepository = servicoRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public List<ComunidadeResponse> listar() {
        List<ComunidadeResponse> lista = new ArrayList<>();
        for (Comunidade c : comunidadeRepository.findAllByOrderByNomeAsc()) {
            lista.add(montarResposta(c));
        }
        return lista;
    }

    @Transactional(readOnly = true)
    public ComunidadeResponse detalhar(Long id) {
        return montarResposta(buscarComunidade(id));
    }

    @Transactional
    public ComunidadeResponse criar(Long alunoLogadoId, ComunidadeRequest dados) {
        String nome = dados.nome.trim();
        if (comunidadeRepository.existsByNomeIgnoreCase(nome)) {
            throw RegraDeNegocioException.conflito("Ja existe uma comunidade com esse nome");
        }
        Comunidade c = new Comunidade();
        c.setNome(nome);
        c.setDescricao(dados.descricao);
        c.setIcone(textoOuPadrao(dados.icone, ICONE_PADRAO));
        c.setCor(textoOuPadrao(dados.cor, COR_PADRAO));
        c.setCriador(usuarioService.buscarAluno(alunoLogadoId));
        comunidadeRepository.save(c);
        return montarResposta(c);
    }

    public Comunidade buscarComunidade(Long id) {
        Optional<Comunidade> c = comunidadeRepository.findById(id);
        if (c.isEmpty()) {
            throw RegraDeNegocioException.naoEncontrado("Comunidade nao encontrada");
        }
        return c.get();
    }

    private ComunidadeResponse montarResposta(Comunidade c) {
        return ComunidadeResponse.de(c, servicoRepository.countByComunidadeIdAndStatus(c.getId(), StatusServico.ABERTO));
    }

    private static String textoOuPadrao(String valor, String padrao) {
        if (valor == null || valor.isBlank()) {
            return padrao;
        }
        return valor.trim();
    }
}
