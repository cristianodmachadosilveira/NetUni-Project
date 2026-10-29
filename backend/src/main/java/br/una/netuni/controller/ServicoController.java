package br.una.netuni.controller;

import br.una.netuni.dto.ServicoRequest;
import br.una.netuni.dto.ServicoResponse;
import br.una.netuni.model.TipoServico;
import br.una.netuni.service.ServicoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** RF04 a RF07. */
@RestController
@RequestMapping("/servicos")
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServicoResponse criar(@RequestAttribute("alunoId") Long alunoId, @Valid @RequestBody ServicoRequest dados) {
        return servicoService.criar(alunoId, dados);
    }

    /** RF06 - ex.: GET /servicos?tipo=OFERTA&busca=calculo */
    @GetMapping
    public List<ServicoResponse> buscar(@RequestParam(required = false) TipoServico tipo,
                                        @RequestParam(required = false) String busca) {
        return servicoService.buscar(tipo, busca);
    }

    @GetMapping("/{id}")
    public ServicoResponse visualizar(@PathVariable Long id) {
        return servicoService.visualizar(id);
    }

    @PutMapping("/{id}")
    public ServicoResponse atualizar(@PathVariable Long id, @RequestAttribute("alunoId") Long alunoId,
                                     @Valid @RequestBody ServicoRequest dados) {
        return servicoService.atualizar(id, alunoId, dados);
    }

    /** Encerra o servico (sai da busca). */
    @DeleteMapping("/{id}")
    public ServicoResponse encerrar(@PathVariable Long id, @RequestAttribute("alunoId") Long alunoId) {
        return servicoService.encerrar(id, alunoId);
    }
}
