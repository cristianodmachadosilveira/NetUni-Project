package br.una.netuni.service;

import br.una.netuni.dto.AlunoResumo;
import br.una.netuni.dto.ContratacaoResponse;
import br.una.netuni.dto.MensagemRequest;
import br.una.netuni.dto.MensagemResponse;
import br.una.netuni.dto.NegociacaoRequest;
import br.una.netuni.dto.ServicoResponse;
import br.una.netuni.dto.SolicitacaoRequest;
import br.una.netuni.exception.RegraDeNegocioException;
import br.una.netuni.model.Aluno;
import br.una.netuni.model.Contratacao;
import br.una.netuni.model.Mensagem;
import br.una.netuni.model.Servico;
import br.una.netuni.model.StatusContratacao;
import br.una.netuni.model.StatusServico;
import br.una.netuni.model.TipoServico;
import br.una.netuni.repository.AvaliacaoRepository;
import br.una.netuni.repository.ContratacaoRepository;
import br.una.netuni.repository.MensagemRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RF08 Comunicacao, RF09 Solicitacao, RF10 Gerenciamento da contratacao.
 *
 * Regra de pagamento: o NetUni so REGISTRA valor e forma de pagamento combinados.
 * Nenhum pagamento e processado aqui.
 *
 * Estados: SOLICITADA -> ACEITA -> EM_ANDAMENTO -> CONCLUIDA
 *          (CANCELADA a partir de SOLICITADA, ACEITA ou EM_ANDAMENTO)
 */
@Service
public class ContratacaoService {

    private final ContratacaoRepository contratacaoRepository;
    private final MensagemRepository mensagemRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final ServicoService servicoService;
    private final UsuarioService usuarioService;
    private final PortfolioService portfolioService;

    public ContratacaoService(ContratacaoRepository contratacaoRepository, MensagemRepository mensagemRepository,
                              AvaliacaoRepository avaliacaoRepository, ServicoService servicoService,
                              UsuarioService usuarioService, PortfolioService portfolioService) {
        this.contratacaoRepository = contratacaoRepository;
        this.mensagemRepository = mensagemRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.servicoService = servicoService;
        this.usuarioService = usuarioService;
        this.portfolioService = portfolioService;
    }

    // =============== RF09 - Solicitacao de servico ===============
    @Transactional
    public ContratacaoResponse solicitar(Long alunoLogadoId, SolicitacaoRequest dados) {
        Servico servico = servicoService.buscarServico(dados.servicoId);   // 404 se nao existir
        Aluno solicitante = usuarioService.buscarAluno(alunoLogadoId);

        if (servico.getStatus() == StatusServico.ENCERRADO) {
            throw RegraDeNegocioException.conflito("Este servico esta encerrado");
        }
        if (servico.getAutor().getId().equals(alunoLogadoId)) {
            throw RegraDeNegocioException.dadosInvalidos("Voce nao pode solicitar o seu proprio servico");
        }

        // Percorre as contratacoes do servico procurando uma ativa do mesmo aluno (duplicada).
        List<Contratacao> existentes = contratacaoRepository.findByServicoId(servico.getId());
        for (Contratacao existente : existentes) {
            boolean mesmoSolicitante = existente.getSolicitante().getId().equals(alunoLogadoId);
            if (mesmoSolicitante && estaAtiva(existente.getStatus())) {
                throw RegraDeNegocioException.conflito("Voce ja tem uma solicitacao ativa para este servico");
            }
        }

        Contratacao contratacao = new Contratacao();
        contratacao.setServico(servico);
        contratacao.setSolicitante(solicitante);
        // OFERTA: o autor oferece -> quem solicita e o contratante.
        // PEDIDO: o autor precisa -> quem solicita e o prestador.
        if (servico.getTipo() == TipoServico.OFERTA) {
            contratacao.setContratante(solicitante);
            contratacao.setPrestador(servico.getAutor());
        } else {
            contratacao.setContratante(servico.getAutor());
            contratacao.setPrestador(solicitante);
        }
        contratacao.setValorNegociado(servico.getPreco());
        contratacao.setObservacoes(dados.observacoes);
        contratacaoRepository.save(contratacao);

        registrarAviso(contratacao, solicitante.getNome() + " solicitou o servico.");
        return montarResposta(contratacao, alunoLogadoId);
    }

    // =============== RF10 - Gerenciamento da contratacao ===============
    @Transactional(readOnly = true)
    public List<ContratacaoResponse> listarMinhas(Long alunoLogadoId) {
        List<Contratacao> contratacoes =
                contratacaoRepository.findByContratanteIdOrPrestadorIdOrderByDataDesc(alunoLogadoId, alunoLogadoId);
        List<ContratacaoResponse> resultado = new ArrayList<>();
        for (Contratacao c : contratacoes) {
            resultado.add(montarResposta(c, alunoLogadoId));
        }
        return resultado;
    }

    @Transactional(readOnly = true)
    public ContratacaoResponse detalhar(Long id, Long alunoLogadoId) {
        return montarResposta(buscarComoParticipante(id, alunoLogadoId), alunoLogadoId);
    }

    /** Registra o valor e a forma de pagamento combinados no chat. */
    @Transactional
    public ContratacaoResponse negociar(Long id, Long alunoLogadoId, NegociacaoRequest dados) {
        Contratacao contratacao = buscarComoParticipante(id, alunoLogadoId);
        StatusContratacao status = contratacao.getStatus();
        if (status != StatusContratacao.SOLICITADA && status != StatusContratacao.ACEITA) {
            throw RegraDeNegocioException.conflito("Valor e forma de pagamento so podem mudar antes do servico comecar");
        }
        contratacao.setValorNegociado(dados.valorNegociado);
        contratacao.setFormaPagamento(dados.formaPagamento.trim());
        if (dados.observacoes != null) {
            contratacao.setObservacoes(dados.observacoes);
        }
        contratacaoRepository.save(contratacao);

        String quem = usuarioService.buscarAluno(alunoLogadoId).getNome();
        registrarAviso(contratacao, quem + " registrou: R$ " + dados.valorNegociado + " via "
                + contratacao.getFormaPagamento() + " (pagamento feito fora do NetUni).");
        return montarResposta(contratacao, alunoLogadoId);
    }

    @Transactional
    public ContratacaoResponse alterarStatus(Long id, Long alunoLogadoId, StatusContratacao novoStatus) {
        Contratacao contratacao = buscarComoParticipante(id, alunoLogadoId);
        validarTransicao(contratacao, alunoLogadoId, novoStatus);

        contratacao.setStatus(novoStatus);
        if (novoStatus == StatusContratacao.CONCLUIDA) {
            contratacao.setDataConclusao(LocalDateTime.now());
        }
        contratacaoRepository.save(contratacao);

        String quem = usuarioService.buscarAluno(alunoLogadoId).getNome();
        registrarAviso(contratacao, quem + " mudou o status para " + novoStatus + ".");
        if (novoStatus == StatusContratacao.CONCLUIDA) {
            portfolioService.registrarServicoConcluido(contratacao);
        }
        return montarResposta(contratacao, alunoLogadoId);
    }

    /**
     * Regras de quem pode levar a contratacao para cada status.
     * Qualquer combinacao fora daqui e um "status invalido" (HTTP 409).
     */
    private void validarTransicao(Contratacao c, Long alunoId, StatusContratacao novo) {
        StatusContratacao atual = c.getStatus();
        boolean ehAutorDoServico = c.getServico().getAutor().getId().equals(alunoId);
        boolean ehPrestador = c.getPrestador().getId().equals(alunoId);
        boolean ehContratante = c.getContratante().getId().equals(alunoId);

        if (novo == StatusContratacao.ACEITA) {
            exigirStatus(atual, StatusContratacao.SOLICITADA, novo);
            if (!ehAutorDoServico) {
                throw RegraDeNegocioException.semPermissao("Apenas o autor do servico pode aceitar a solicitacao");
            }
        } else if (novo == StatusContratacao.EM_ANDAMENTO) {
            exigirStatus(atual, StatusContratacao.ACEITA, novo);
            if (!ehPrestador) {
                throw RegraDeNegocioException.semPermissao("Apenas o prestador pode iniciar o servico");
            }
            if (c.getValorNegociado() == null || c.getFormaPagamento() == null) {
                throw RegraDeNegocioException.dadosInvalidos("Registre o valor e a forma de pagamento antes de iniciar");
            }
        } else if (novo == StatusContratacao.CONCLUIDA) {
            exigirStatus(atual, StatusContratacao.EM_ANDAMENTO, novo);
            if (!ehContratante) {
                throw RegraDeNegocioException.semPermissao("Apenas o contratante confirma a conclusao");
            }
        } else if (novo == StatusContratacao.CANCELADA) {
            if (!estaAtiva(atual)) {
                throw RegraDeNegocioException.conflito("Status invalido: a contratacao ja esta " + atual);
            }
        } else {
            throw RegraDeNegocioException.conflito("Status invalido: nao e possivel voltar para " + novo);
        }
    }

    private void exigirStatus(StatusContratacao atual, StatusContratacao esperado, StatusContratacao novo) {
        if (atual != esperado) {
            throw RegraDeNegocioException.conflito(
                    "Status invalido: para ir para " + novo + " a contratacao precisa estar " + esperado
                            + " (esta " + atual + ")");
        }
    }

    // =============== RF08 - Comunicacao ===============
    @Transactional(readOnly = true)
    public List<MensagemResponse> listarMensagens(Long id, Long alunoLogadoId) {
        buscarComoParticipante(id, alunoLogadoId);
        List<MensagemResponse> resultado = new ArrayList<>();
        for (Mensagem m : mensagemRepository.findByContratacaoIdOrderByDataAscIdAsc(id)) {
            resultado.add(MensagemResponse.de(m));
        }
        return resultado;
    }

    @Transactional
    public MensagemResponse enviarMensagem(Long id, Long alunoLogadoId, MensagemRequest dados) {
        Contratacao contratacao = buscarComoParticipante(id, alunoLogadoId);
        Mensagem mensagem = new Mensagem();
        mensagem.setContratacao(contratacao);
        mensagem.setRemetente(usuarioService.buscarAluno(alunoLogadoId));
        mensagem.setConteudo(dados.conteudo.trim());
        mensagemRepository.save(mensagem);
        return MensagemResponse.de(mensagem);
    }

    // =============== auxiliares ===============
    public Contratacao buscarComoParticipante(Long id, Long alunoLogadoId) {
        Optional<Contratacao> encontrada = contratacaoRepository.findById(id);
        if (encontrada.isEmpty()) {
            throw RegraDeNegocioException.naoEncontrado("Contratacao nao encontrada");
        }
        Contratacao c = encontrada.get();
        boolean participa = c.getContratante().getId().equals(alunoLogadoId)
                || c.getPrestador().getId().equals(alunoLogadoId);
        if (!participa) {
            throw RegraDeNegocioException.semPermissao("Voce nao participa desta contratacao");
        }
        return c;
    }

    private boolean estaAtiva(StatusContratacao status) {
        return status == StatusContratacao.SOLICITADA
                || status == StatusContratacao.ACEITA
                || status == StatusContratacao.EM_ANDAMENTO;
    }

    /** Mensagem automatica no chat, sem remetente, para registrar o historico. */
    private void registrarAviso(Contratacao contratacao, String texto) {
        Mensagem aviso = new Mensagem();
        aviso.setContratacao(contratacao);
        aviso.setConteudo(texto);
        mensagemRepository.save(aviso);
    }

    private ContratacaoResponse montarResposta(Contratacao c, Long alunoLogadoId) {
        ContratacaoResponse r = new ContratacaoResponse();
        r.id = c.getId();
        r.servico = ServicoResponse.de(c.getServico());
        r.contratante = AlunoResumo.de(c.getContratante());
        r.prestador = AlunoResumo.de(c.getPrestador());
        r.solicitanteId = c.getSolicitante().getId();
        r.status = c.getStatus();
        r.valorNegociado = c.getValorNegociado();
        r.formaPagamento = c.getFormaPagamento();
        r.observacoes = c.getObservacoes();
        r.data = c.getData();
        r.dataConclusao = c.getDataConclusao();
        r.avaliada = avaliacaoRepository.findByContratacaoId(c.getId()).isPresent();
        r.acoesPermitidas = calcularAcoes(c, alunoLogadoId, r.avaliada);
        return r;
    }

    /** Lista de botoes que o app deve mostrar para o aluno logado. */
    private List<String> calcularAcoes(Contratacao c, Long alunoId, boolean avaliada) {
        List<String> acoes = new ArrayList<>();
        boolean ehAutorDoServico = c.getServico().getAutor().getId().equals(alunoId);
        boolean ehPrestador = c.getPrestador().getId().equals(alunoId);
        boolean ehContratante = c.getContratante().getId().equals(alunoId);
        StatusContratacao status = c.getStatus();

        if (status == StatusContratacao.SOLICITADA || status == StatusContratacao.ACEITA) {
            acoes.add("NEGOCIAR");
        }
        if (status == StatusContratacao.SOLICITADA && ehAutorDoServico) {
            acoes.add("ACEITAR");
        }
        if (status == StatusContratacao.ACEITA && ehPrestador) {
            acoes.add("INICIAR");
        }
        if (status == StatusContratacao.EM_ANDAMENTO && ehContratante) {
            acoes.add("CONCLUIR");
        }
        if (status == StatusContratacao.CONCLUIDA && ehContratante && !avaliada) {
            acoes.add("AVALIAR");
        }
        if (estaAtiva(status)) {
            acoes.add("CANCELAR");
        }
        return acoes;
    }
}
