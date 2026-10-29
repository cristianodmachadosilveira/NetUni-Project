package br.una.netuni.controller;

import br.una.netuni.dto.AvaliacaoRequest;
import br.una.netuni.dto.AvaliacaoResponse;
import br.una.netuni.service.AvaliacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** RF11. A listagem fica em GET /usuarios/{id}/avaliacoes. */
@RestController
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @PostMapping("/avaliacoes")
    @ResponseStatus(HttpStatus.CREATED)
    public AvaliacaoResponse avaliar(@RequestAttribute("alunoId") Long alunoId,
                                     @Valid @RequestBody AvaliacaoRequest dados) {
        return avaliacaoService.avaliar(alunoId, dados);
    }
}
