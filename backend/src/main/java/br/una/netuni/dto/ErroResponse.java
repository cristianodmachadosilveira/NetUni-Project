package br.una.netuni.dto;

import java.time.LocalDateTime;

public class ErroResponse {

    public int status;
    public String erro;
    public LocalDateTime data = LocalDateTime.now();

    public ErroResponse(int status, String erro) {
        this.status = status;
        this.erro = erro;
    }
}
