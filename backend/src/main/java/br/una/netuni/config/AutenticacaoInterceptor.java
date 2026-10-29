package br.una.netuni.config;

import br.una.netuni.exception.RegraDeNegocioException;
import br.una.netuni.model.Sessao;
import br.una.netuni.repository.SessaoRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * RF02 / RNF03 - Roda antes de cada rota protegida.
 * Le o cabecalho "Authorization: Bearer <token>", confere a sessao no banco
 * e guarda o id do aluno logado na requisicao (atributo "alunoId").
 */
@Component
public class AutenticacaoInterceptor implements HandlerInterceptor {

    public static final String ATRIBUTO_ALUNO_ID = "alunoId";

    private final SessaoRepository sessaoRepository;

    public AutenticacaoInterceptor(SessaoRepository sessaoRepository) {
        this.sessaoRepository = sessaoRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String metodo = request.getMethod();
        String caminho = request.getRequestURI();

        // Rotas publicas: cadastro, login e o "preflight" CORS do navegador.
        if (metodo.equals("OPTIONS")) {
            return true;
        }
        if (metodo.equals("POST") && (caminho.equals("/usuarios") || caminho.equals("/login"))) {
            return true;
        }
        // Fotos e videos do feed: a tag <video>/<img> do navegador nao manda o token.
        if ((metodo.equals("GET") || metodo.equals("HEAD"))
                && (caminho.startsWith("/midias/") || caminho.startsWith("/demo/"))) {
            return true;
        }

        String cabecalho = request.getHeader("Authorization");
        if (cabecalho == null || !cabecalho.startsWith("Bearer ")) {
            throw RegraDeNegocioException.naoAutorizado("Faca login para continuar");
        }

        String token = cabecalho.substring("Bearer ".length()).trim();
        Optional<Sessao> sessao = sessaoRepository.findById(token);
        if (sessao.isEmpty()) {
            throw RegraDeNegocioException.naoAutorizado("Sessao invalida. Faca login novamente");
        }
        if (sessao.get().getExpiraEm().isBefore(LocalDateTime.now())) {
            throw RegraDeNegocioException.naoAutorizado("Sessao expirada. Faca login novamente");
        }

        request.setAttribute(ATRIBUTO_ALUNO_ID, sessao.get().getAluno().getId());
        return true;
    }
}
