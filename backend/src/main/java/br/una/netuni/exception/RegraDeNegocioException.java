package br.una.netuni.exception;

import org.springframework.http.HttpStatus;

/**
 * Erro previsto pelas regras do NetUni (ex.: e-mail duplicado, nota fora de 1 a 5).
 * Carrega o codigo HTTP que a API deve devolver.
 */
public class RegraDeNegocioException extends RuntimeException {

    private final HttpStatus status;

    public RegraDeNegocioException(HttpStatus status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static RegraDeNegocioException dadosInvalidos(String mensagem) {
        return new RegraDeNegocioException(HttpStatus.BAD_REQUEST, mensagem);
    }

    public static RegraDeNegocioException naoAutorizado(String mensagem) {
        return new RegraDeNegocioException(HttpStatus.UNAUTHORIZED, mensagem);
    }

    public static RegraDeNegocioException semPermissao(String mensagem) {
        return new RegraDeNegocioException(HttpStatus.FORBIDDEN, mensagem);
    }

    public static RegraDeNegocioException naoEncontrado(String mensagem) {
        return new RegraDeNegocioException(HttpStatus.NOT_FOUND, mensagem);
    }

    public static RegraDeNegocioException conflito(String mensagem) {
        return new RegraDeNegocioException(HttpStatus.CONFLICT, mensagem);
    }
}
