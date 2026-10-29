package br.una.netuni.controller;

import br.una.netuni.dto.CadastroRequest;
import br.una.netuni.dto.LoginRequest;
import br.una.netuni.dto.LoginResponse;
import br.una.netuni.dto.PerfilRequest;
import br.una.netuni.dto.PortfolioResponse;
import br.una.netuni.dto.ResumoAvaliacoesResponse;
import br.una.netuni.dto.ServicoResponse;
import br.una.netuni.dto.UsuarioResponse;
import br.una.netuni.service.AvaliacaoService;
import br.una.netuni.service.PortfolioService;
import br.una.netuni.service.ServicoService;
import br.una.netuni.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** RF01, RF02, RF03 e consultas do perfil (servicos, avaliacoes e portfolio do aluno). */
@RestController
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final ServicoService servicoService;
    private final AvaliacaoService avaliacaoService;
    private final PortfolioService portfolioService;

    public UsuarioController(UsuarioService usuarioService, ServicoService servicoService,
                             AvaliacaoService avaliacaoService, PortfolioService portfolioService) {
        this.usuarioService = usuarioService;
        this.servicoService = servicoService;
        this.avaliacaoService = avaliacaoService;
        this.portfolioService = portfolioService;
    }

    /** RF01 - publico. */
    @PostMapping("/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastrar(@Valid @RequestBody CadastroRequest dados) {
        return usuarioService.cadastrar(dados);
    }

    /** RF02 - publico. */
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest dados) {
        return usuarioService.login(dados);
    }

    /** Perfil do proprio aluno logado. */
    @GetMapping("/usuarios/me")
    public UsuarioResponse meuPerfil(@RequestAttribute("alunoId") Long alunoId) {
        return usuarioService.buscarPerfil(alunoId, alunoId);
    }

    @GetMapping("/usuarios/{id}")
    public UsuarioResponse perfil(@PathVariable Long id, @RequestAttribute("alunoId") Long alunoId) {
        return usuarioService.buscarPerfil(id, alunoId);
    }

    /** RF03. */
    @PutMapping("/usuarios/{id}")
    public UsuarioResponse atualizar(@PathVariable Long id, @RequestAttribute("alunoId") Long alunoId,
                                     @Valid @RequestBody PerfilRequest dados) {
        return usuarioService.atualizarPerfil(id, alunoId, dados);
    }

    @GetMapping("/usuarios/{id}/servicos")
    public List<ServicoResponse> servicosDoAluno(@PathVariable Long id) {
        return servicoService.listarDoAluno(id);
    }

    /** RF11/RF12 - avaliacoes recebidas + media. */
    @GetMapping("/usuarios/{id}/avaliacoes")
    public ResumoAvaliacoesResponse avaliacoes(@PathVariable Long id) {
        return avaliacaoService.listarRecebidas(id);
    }

    /** RF12. */
    @GetMapping("/usuarios/{id}/portfolio")
    public List<PortfolioResponse> portfolio(@PathVariable Long id) {
        return portfolioService.listar(id);
    }
}
