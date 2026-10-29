package br.una.netuni.controller;

import br.una.netuni.dto.MidiaResponse;
import br.una.netuni.service.MidiaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Upload de foto ou video (multipart, campo "arquivo"). Exige login.
 * Depois o app usa a "url" devolvida no POST /posts.
 */
@RestController
public class MidiaController {

    private final MidiaService midiaService;

    public MidiaController(MidiaService midiaService) {
        this.midiaService = midiaService;
    }

    @PostMapping("/midias")
    @ResponseStatus(HttpStatus.CREATED)
    public MidiaResponse enviar(@RequestParam("arquivo") MultipartFile arquivo) {
        return midiaService.salvar(arquivo);
    }
}
