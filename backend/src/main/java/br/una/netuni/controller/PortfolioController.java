package br.una.netuni.controller;

import br.una.netuni.dto.PortfolioRequest;
import br.una.netuni.dto.PortfolioResponse;
import br.una.netuni.service.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** RF12. A listagem fica em GET /usuarios/{id}/portfolio. */
@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PortfolioResponse adicionar(@RequestAttribute("alunoId") Long alunoId,
                                       @Valid @RequestBody PortfolioRequest dados) {
        return portfolioService.adicionar(alunoId, dados);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id, @RequestAttribute("alunoId") Long alunoId) {
        portfolioService.remover(id, alunoId);
    }
}
