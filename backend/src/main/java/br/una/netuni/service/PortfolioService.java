package br.una.netuni.service;

import br.una.netuni.dto.PortfolioRequest;
import br.una.netuni.dto.PortfolioResponse;
import br.una.netuni.exception.RegraDeNegocioException;
import br.una.netuni.model.Contratacao;
import br.una.netuni.model.Portfolio;
import br.una.netuni.repository.PortfolioRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** RF12 - Portfolio. */
@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UsuarioService usuarioService;

    public PortfolioService(PortfolioRepository portfolioRepository, UsuarioService usuarioService) {
        this.portfolioRepository = portfolioRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public List<PortfolioResponse> listar(Long alunoId) {
        usuarioService.buscarAluno(alunoId);
        List<PortfolioResponse> itens = new ArrayList<>();
        for (Portfolio item : portfolioRepository.findByAlunoIdOrderByDataDesc(alunoId)) {
            itens.add(PortfolioResponse.de(item));
        }
        return itens;
    }

    /** Item cadastrado manualmente pelo aluno (ex.: um projeto feito fora do NetUni). */
    @Transactional
    public PortfolioResponse adicionar(Long alunoLogadoId, PortfolioRequest dados) {
        Portfolio item = new Portfolio();
        item.setAluno(usuarioService.buscarAluno(alunoLogadoId));
        item.setTitulo(dados.titulo.trim());
        item.setDescricao(dados.descricao);
        item.setLinkProjeto(dados.linkProjeto);
        portfolioRepository.save(item);
        return PortfolioResponse.de(item);
    }

    @Transactional
    public void remover(Long id, Long alunoLogadoId) {
        Optional<Portfolio> item = portfolioRepository.findById(id);
        if (item.isEmpty()) {
            throw RegraDeNegocioException.naoEncontrado("Item de portfolio nao encontrado");
        }
        if (!item.get().getAluno().getId().equals(alunoLogadoId)) {
            throw RegraDeNegocioException.semPermissao("Voce so pode remover itens do seu portfolio");
        }
        portfolioRepository.delete(item.get());
    }

    /** Chamado quando uma contratacao e concluida: o servico entra no portfolio do prestador. */
    @Transactional
    public void registrarServicoConcluido(Contratacao contratacao) {
        Portfolio item = new Portfolio();
        item.setAluno(contratacao.getPrestador());
        item.setContratacao(contratacao);
        item.setTitulo(contratacao.getServico().getTitulo());
        item.setDescricao("Servico concluido pelo NetUni para " + contratacao.getContratante().getNome() + ".");
        portfolioRepository.save(item);
    }
}
