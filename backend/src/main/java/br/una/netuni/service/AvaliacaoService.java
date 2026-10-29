package br.una.netuni.service;

import br.una.netuni.dto.AvaliacaoRequest;
import br.una.netuni.dto.AvaliacaoResponse;
import br.una.netuni.dto.ResumoAvaliacoesResponse;
import br.una.netuni.exception.RegraDeNegocioException;
import br.una.netuni.model.Avaliacao;
import br.una.netuni.model.Contratacao;
import br.una.netuni.model.StatusContratacao;
import br.una.netuni.repository.AvaliacaoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** RF11 - Avaliacao. */
@Service
public class AvaliacaoService {

    public static final int NOTA_MINIMA = 1;
    public static final int NOTA_MAXIMA = 5;

    private final AvaliacaoRepository avaliacaoRepository;
    private final ContratacaoService contratacaoService;
    private final UsuarioService usuarioService;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository, ContratacaoService contratacaoService,
                            UsuarioService usuarioService) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.contratacaoService = contratacaoService;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public AvaliacaoResponse avaliar(Long alunoLogadoId, AvaliacaoRequest dados) {
        if (dados.nota < NOTA_MINIMA || dados.nota > NOTA_MAXIMA) {
            throw RegraDeNegocioException.dadosInvalidos(
                    "A nota deve ser de " + NOTA_MINIMA + " a " + NOTA_MAXIMA);
        }

        Contratacao contratacao = contratacaoService.buscarComoParticipante(dados.contratacaoId, alunoLogadoId);
        if (!contratacao.getContratante().getId().equals(alunoLogadoId)) {
            throw RegraDeNegocioException.semPermissao("Apenas o contratante avalia o servico");
        }
        if (contratacao.getStatus() != StatusContratacao.CONCLUIDA) {
            throw RegraDeNegocioException.conflito("So e possivel avaliar uma contratacao CONCLUIDA");
        }
        if (avaliacaoRepository.findByContratacaoId(contratacao.getId()).isPresent()) {
            throw RegraDeNegocioException.conflito("Esta contratacao ja foi avaliada");
        }

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setContratacao(contratacao);
        avaliacao.setAvaliador(contratacao.getContratante());
        avaliacao.setNota(dados.nota);
        if (dados.comentario != null && !dados.comentario.isBlank()) {
            avaliacao.setComentario(dados.comentario.trim());
        }
        avaliacaoRepository.save(avaliacao);
        return AvaliacaoResponse.de(avaliacao);
    }

    /** Avaliacoes recebidas por um aluno como prestador, com a media calculada num laco. */
    @Transactional(readOnly = true)
    public ResumoAvaliacoesResponse listarRecebidas(Long alunoId) {
        usuarioService.buscarAluno(alunoId);
        List<Avaliacao> recebidas = avaliacaoRepository.findByContratacaoPrestadorIdOrderByDataDesc(alunoId);

        ResumoAvaliacoesResponse resumo = new ResumoAvaliacoesResponse();
        int soma = 0;
        for (Avaliacao avaliacao : recebidas) {
            soma = soma + avaliacao.getNota();
            resumo.avaliacoes.add(AvaliacaoResponse.de(avaliacao));
        }
        resumo.total = recebidas.size();
        resumo.media = calcularMedia(soma, resumo.total);
        return resumo;
    }

    /** Media com uma casa decimal. Sem avaliacoes, a media e 0. */
    public static double calcularMedia(int soma, int quantidade) {
        if (quantidade == 0) {
            return 0.0;
        }
        double media = (double) soma / quantidade;
        return Math.round(media * 10) / 10.0;
    }
}
