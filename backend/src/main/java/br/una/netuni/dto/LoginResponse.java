package br.una.netuni.dto;

public class LoginResponse {

    /** Enviar nas proximas requisicoes no cabecalho: Authorization: Bearer <token> */
    public String token;
    public UsuarioResponse usuario;

    public LoginResponse(String token, UsuarioResponse usuario) {
        this.token = token;
        this.usuario = usuario;
    }
}
