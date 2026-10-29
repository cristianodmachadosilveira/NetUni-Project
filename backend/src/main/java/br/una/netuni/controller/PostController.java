package br.una.netuni.controller;

import br.una.netuni.dto.ComentarioRequest;
import br.una.netuni.dto.ComentarioResponse;
import br.una.netuni.dto.PostRequest;
import br.una.netuni.dto.PostResponse;
import br.una.netuni.dto.SalvoResponse;
import br.una.netuni.service.PostService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** RF14 / RF15 - Feed, posts, salvos e comentarios. */
@RestController
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /** GET /feed (Para voce, ranqueado) ou /feed?comunidadeId=2 (so daquela comunidade). */
    @GetMapping("/feed")
    public List<PostResponse> feed(@RequestAttribute("alunoId") Long alunoId,
                                   @RequestParam(required = false) Long comunidadeId,
                                   @RequestParam(required = false) Integer limite) {
        return postService.feed(alunoId, comunidadeId, limite);
    }

    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse publicar(@RequestAttribute("alunoId") Long alunoId, @Valid @RequestBody PostRequest dados) {
        return postService.publicar(alunoId, dados);
    }

    @DeleteMapping("/posts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id, @RequestAttribute("alunoId") Long alunoId) {
        postService.remover(id, alunoId);
    }

    /** Salvar ou tirar dos salvos (alterna). */
    @PostMapping("/posts/{id}/salvo")
    public SalvoResponse salvar(@PathVariable Long id, @RequestAttribute("alunoId") Long alunoId) {
        return postService.alternarSalvo(id, alunoId);
    }

    /** Aba "Salvos" do perfil do aluno logado. */
    @GetMapping("/usuarios/me/salvos")
    public List<PostResponse> meusSalvos(@RequestAttribute("alunoId") Long alunoId) {
        return postService.salvosDoAluno(alunoId);
    }

    @GetMapping("/posts/{id}/comentarios")
    public List<ComentarioResponse> comentarios(@PathVariable Long id) {
        return postService.listarComentarios(id);
    }

    @PostMapping("/posts/{id}/comentarios")
    @ResponseStatus(HttpStatus.CREATED)
    public ComentarioResponse comentar(@PathVariable Long id, @RequestAttribute("alunoId") Long alunoId,
                                       @Valid @RequestBody ComentarioRequest dados) {
        return postService.comentar(id, alunoId, dados);
    }

    @GetMapping("/usuarios/{id}/posts")
    public List<PostResponse> postsDoAluno(@PathVariable Long id, @RequestAttribute("alunoId") Long alunoId) {
        return postService.postsDoAluno(id, alunoId);
    }
}
