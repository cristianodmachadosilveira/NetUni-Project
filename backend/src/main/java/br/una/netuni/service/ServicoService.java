package br.una.netuni.service;

import br.una.netuni.dto.ServicoRequest;
import br.una.netuni.dto.ServicoResponse;
import br.una.netuni.exception.RegraDeNegocioException;
import br.una.netuni.model.ModalidadePreco;
import br.una.netuni.model.Servico;
import br.una.netuni.model.StatusServico;
import br.una.netuni.model.TipoServico;
import br.una.netuni.repository.ServicoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** RF04 Publicar, RF05 Gerenciar, RF06 Buscar, RF07 Visualizar servicos. */
@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final UsuarioService usuarioService;
    private final ComunidadeService comunidadeService;

    public ServicoService(ServicoRepository servicoRepository, UsuarioService usuarioService,
                          ComunidadeService comunidadeService) {
        this.servicoRepository = servicoRepository;
        this.usuarioService = usuarioService;
        this.comunidadeService = comunidadeService;
    }

    // RF04
    @Transactional
    public ServicoResponse criar(Long alunoLogadoId, ServicoRequest dados) {
        Servico servico = new Servico();
        servico.setAutor(usuarioService.buscarAluno(alunoLogadoId));
        preencher(servico, dados);
        servicoRepository.save(servico);
        return ServicoResponse.de(servico);
    }

    // RF05
    @Transactional
    public ServicoResponse atualizar(Long id, Long alunoLogadoId, ServicoRequest dados) {
        Servico servico = buscarDoAutor(id, alunoLogadoId);
        if (servico.getStatus() == StatusServico.ENCERRADO) {
            throw RegraDeNegocioException.conflito("Servico encerrado nao pode ser editado");
        }
        preencher(servico, dados);
        servicoRepository.save(servico);
        return ServicoResponse.de(servico);
    }

    /** RF05 - "Excluir" encerra o servico: ele sai da busca, mas o historico das contratacoes continua. */
    @Transactional
    public ServicoResponse encerrar(Long id, Long alunoLogadoId) {
        Servico servico = buscarDoAutor(id, alunoLogadoId);
        servico.setStatus(StatusServico.ENCERRADO);
        servicoRepository.save(servico);
        return ServicoResponse.de(servico);
    }

    // RF07
    @Transactional(readOnly = true)
    public ServicoResponse visualizar(Long id) {
        return ServicoResponse.de(buscarServico(id));
    }

    /**
     * RF06 - Busca. Entradas opcionais: tipo (PEDIDO/OFERTA), texto e comunidade (RF13).
     * Percorre os servicos abertos e mantem so os que passam nos filtros.
     * Sem comunidade, todos aparecem (com ou sem etiqueta): e a lista geral de anuncios.
     */
    @Transactional(readOnly = true)
    public List<ServicoResponse> buscar(TipoServico tipo, String texto, Long comunidadeId) {
        if (comunidadeId != null) {
            comunidadeService.buscarComunidade(comunidadeId);   // 404 se nao existir
        }
        List<Servico> abertos = servicoRepository.findByStatusOrderByDataCriacaoDesc(StatusServico.ABERTO);
        String termo = "";
        if (texto != null) {
            termo = texto.trim().toLowerCase();
        }

        List<ServicoResponse> resultado = new ArrayList<>();
        for (Servico servico : abertos) {
            if (tipo != null && servico.getTipo() != tipo) {
                continue;
            }
            if (!termo.isEmpty() && !contemTermo(servico, termo)) {
                continue;
            }
            if (comunidadeId != null && !daComunidade(servico, comunidadeId)) {
                continue;
            }
            resultado.add(ServicoResponse.de(servico));
        }
        return resultado;
    }

    @Transactional(readOnly = true)
    public List<ServicoResponse> listarDoAluno(Long alunoId) {
        usuarioService.buscarAluno(alunoId);
        List<ServicoResponse> resultado = new ArrayList<>();
        for (Servico servico : servicoRepository.findByAutorIdOrderByDataCriacaoDesc(alunoId)) {
            resultado.add(ServicoResponse.de(servico));
        }
        return resultado;
    }

    public Servico buscarServico(Long id) {
        Optional<Servico> servico = servicoRepository.findById(id);
        if (servico.isEmpty()) {
            throw RegraDeNegocioException.naoEncontrado("Servico nao encontrado");
        }
        return servico.get();
    }

    private Servico buscarDoAutor(Long id, Long alunoLogadoId) {
        Servico servico = buscarServico(id);
        if (!servico.getAutor().getId().equals(alunoLogadoId)) {
            throw RegraDeNegocioException.semPermissao("Apenas o autor pode alterar este servico");
        }
        return servico;
    }

    private boolean daComunidade(Servico servico, Long comunidadeId) {
        return servico.getComunidade() != null && servico.getComunidade().getId().equals(comunidadeId);
    }

    /** Procura o termo no titulo, na descricao e no texto da troca, sem diferenciar maiusculas. */
    private boolean contemTermo(Servico servico, String termo) {
        if (servico.getTitulo().toLowerCase().contains(termo)) {
            return true;
        }
        if (servico.getDescricao() != null && servico.getDescricao().toLowerCase().contains(termo)) {
            return true;
        }
        return servico.getTroca() != null && servico.getTroca().toLowerCase().contains(termo);
    }

    /**
     * Regra do menu "forma de pagamento":
     * VALOR exige preco; TROCA exige o texto do que aceita; A_COMBINAR nao exige nada.
     * O campo que nao faz sentido para a modalidade escolhida e apagado (fica null).
     */
    private void preencherPreco(Servico servico, ServicoRequest dados) {
        ModalidadePreco modalidade = dados.modalidade;
        if (modalidade == null) {
            modalidade = ModalidadePreco.VALOR;
        }
        servico.setModalidade(modalidade);
        servico.setPreco(null);
        servico.setTroca(null);

        if (modalidade == ModalidadePreco.VALOR) {
            if (dados.preco == null) {
                throw RegraDeNegocioException.dadosInvalidos("Informe o preco ou escolha 'A combinar' ou 'Troca'");
            }
            servico.setPreco(dados.preco);
        } else if (modalidade == ModalidadePreco.TROCA) {
            if (dados.troca == null || dados.troca.isBlank()) {
                throw RegraDeNegocioException.dadosInvalidos("Diga o que voce aceita em troca");
            }
            servico.setTroca(dados.troca.trim());
        }
    }

    private void preencher(Servico servico, ServicoRequest dados) {
        servico.setTipo(dados.tipo);
        servico.setTitulo(dados.titulo.trim());
        if (dados.descricao == null || dados.descricao.isBlank()) {
            servico.setDescricao(null);
        } else {
            servico.setDescricao(dados.descricao.trim());
        }
        preencherPreco(servico, dados);
        servico.setPrazoDias(dados.prazoDias);
        if (dados.comunidadeId == null) {
            servico.setComunidade(null);
        } else {
            servico.setComunidade(comunidadeService.buscarComunidade(dados.comunidadeId));
        }
    }
}
