package br.una.netuni.controller;

import br.una.netuni.dto.ComunidadeRequest;
import br.una.netuni.dto.ComunidadeResponse;
import br.una.netuni.service.ComunidadeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** RF13 - Comunidades. Os anuncios de uma comunidade ficam em GET /servicos?comunidadeId={id}. */
@RestController
@RequestMapping("/comunidades")
public class ComunidadeController {

    private final ComunidadeService comunidadeService;

    public ComunidadeController(ComunidadeService comunidadeService) {
        this.comunidadeService = comunidadeService;
    }

    @GetMapping
    public List<ComunidadeResponse> listar() {
        return comunidadeService.listar();
    }

    @GetMapping("/{id}")
    public ComunidadeResponse detalhar(@PathVariable Long id) {
        return comunidadeService.detalhar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComunidadeResponse criar(@RequestAttribute("alunoId") Long alunoId,
                                    @Valid @RequestBody ComunidadeRequest dados) {
        return comunidadeService.criar(alunoId, dados);
    }
}
