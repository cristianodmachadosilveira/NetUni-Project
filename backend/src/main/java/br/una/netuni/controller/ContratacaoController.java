package br.una.netuni.controller;

import br.una.netuni.dto.ContratacaoResponse;
import br.una.netuni.dto.MensagemRequest;
import br.una.netuni.dto.MensagemResponse;
import br.una.netuni.dto.NegociacaoRequest;
import br.una.netuni.dto.SolicitacaoRequest;
import br.una.netuni.dto.StatusRequest;
import br.una.netuni.service.ContratacaoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** RF08, RF09, RF10. */
@RestController
@RequestMapping("/contratacoes")
public class ContratacaoController {

    private final ContratacaoService contratacaoService;

    public ContratacaoController(ContratacaoService contratacaoService) {
        this.contratacaoService = contratacaoService;
    }

    /** RF09 - Solicitar servico. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContratacaoResponse solicitar(@RequestAttribute("alunoId") Long alunoId,
                                         @Valid @RequestBody SolicitacaoRequest dados) {
        return contratacaoService.solicitar(alunoId, dados);
    }

    @GetMapping
    public List<ContratacaoResponse> minhas(@RequestAttribute("alunoId") Long alunoId) {
        return contratacaoService.listarMinhas(alunoId);
    }

    @GetMapping("/{id}")
    public ContratacaoResponse detalhar(@PathVariable Long id, @RequestAttribute("alunoId") Long alunoId) {
        return contratacaoService.detalhar(id, alunoId);
    }

    /** Negociacao: registra valor e forma de pagamento (pagamento fora do app). */
    @PutMapping("/{id}")
    public ContratacaoResponse negociar(@PathVariable Long id, @RequestAttribute("alunoId") Long alunoId,
                                        @Valid @RequestBody NegociacaoRequest dados) {
        return contratacaoService.negociar(id, alunoId, dados);
    }

    @PutMapping("/{id}/status")
    public ContratacaoResponse alterarStatus(@PathVariable Long id, @RequestAttribute("alunoId") Long alunoId,
                                             @Valid @RequestBody StatusRequest dados) {
        return contratacaoService.alterarStatus(id, alunoId, dados.status);
    }

    /** RF08 - Chat simplificado da contratacao. */
    @GetMapping("/{id}/mensagens")
    public List<MensagemResponse> mensagens(@PathVariable Long id, @RequestAttribute("alunoId") Long alunoId) {
        return contratacaoService.listarMensagens(id, alunoId);
    }

    @PostMapping("/{id}/mensagens")
    @ResponseStatus(HttpStatus.CREATED)
    public MensagemResponse enviar(@PathVariable Long id, @RequestAttribute("alunoId") Long alunoId,
                                   @Valid @RequestBody MensagemRequest dados) {
        return contratacaoService.enviarMensagem(id, alunoId, dados);
    }
}
