package br.una.netuni.controller;

import br.una.netuni.dto.ComunidadeRequest;
import br.una.netuni.dto.ComunidadeResponse;
import br.una.netuni.service.ComunidadeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** RF13 - Comunidades. O feed de uma comunidade fica em GET /feed?comunidadeId={id}. */
@RestController
@RequestMapping("/comunidades")
public class ComunidadeController {

    private final ComunidadeService comunidadeService;

    public ComunidadeController(ComunidadeService comunidadeService) {
        this.comunidadeService = comunidadeService;
    }

    @GetMapping
    public List<ComunidadeResponse> listar(@RequestAttribute("alunoId") Long alunoId) {
        return comunidadeService.listar(alunoId);
    }

    @GetMapping("/{id}")
    public ComunidadeResponse detalhar(@PathVariable Long id, @RequestAttribute("alunoId") Long alunoId) {
        return comunidadeService.detalhar(id, alunoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComunidadeResponse criar(@RequestAttribute("alunoId") Long alunoId,
                                    @Valid @RequestBody ComunidadeRequest dados) {
        return comunidadeService.criar(alunoId, dados);
    }

    /** Entrar na comunidade. */
    @PostMapping("/{id}/membros")
    public ComunidadeResponse entrar(@PathVariable Long id, @RequestAttribute("alunoId") Long alunoId) {
        return comunidadeService.entrar(id, alunoId);
    }

    /** Sair da comunidade. */
    @DeleteMapping("/{id}/membros")
    public ComunidadeResponse sair(@PathVariable Long id, @RequestAttribute("alunoId") Long alunoId) {
        return comunidadeService.sair(id, alunoId);
    }
}
