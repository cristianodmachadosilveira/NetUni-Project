package br.una.netuni.service;

import br.una.netuni.dto.CadastroRequest;
import br.una.netuni.dto.LoginRequest;
import br.una.netuni.dto.LoginResponse;
import br.una.netuni.dto.PerfilRequest;
import br.una.netuni.dto.UsuarioResponse;
import br.una.netuni.exception.RegraDeNegocioException;
import br.una.netuni.model.Aluno;
import br.una.netuni.model.Habilidade;
import br.una.netuni.model.Sessao;
import br.una.netuni.repository.AlunoRepository;
import br.una.netuni.repository.HabilidadeRepository;
import br.una.netuni.repository.SessaoRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** RF01 Cadastro, RF02 Login, RF03 Perfil e habilidades. */
@Service
public class UsuarioService {

    public static final int MAXIMO_HABILIDADES = 15;

    private final AlunoRepository alunoRepository;
    private final HabilidadeRepository habilidadeRepository;
    private final SessaoRepository sessaoRepository;
    private final SenhaService senhaService;
    private final int validadeTokenDias;

    public UsuarioService(AlunoRepository alunoRepository, HabilidadeRepository habilidadeRepository,
                          SessaoRepository sessaoRepository, SenhaService senhaService,
                          @Value("${netuni.validade-token-dias:7}") int validadeTokenDias) {
        this.alunoRepository = alunoRepository;
        this.habilidadeRepository = habilidadeRepository;
        this.sessaoRepository = sessaoRepository;
        this.senhaService = senhaService;
        this.validadeTokenDias = validadeTokenDias;
    }

    // ---------------- RF01 - Cadastro ----------------
    @Transactional
    public UsuarioResponse cadastrar(CadastroRequest dados) {
        String email = normalizarEmail(dados.email);

        if (alunoRepository.findByEmail(email).isPresent()) {
            throw RegraDeNegocioException.conflito("E-mail ja cadastrado");
        }

        Aluno aluno = new Aluno();
        aluno.setNome(dados.nome.trim());
        aluno.setEmail(email);
        aluno.setSenhaHash(senhaService.gerarHash(dados.senha));
        aluno.setCurso(textoOuNulo(dados.curso));
        aluno.setSemestre(dados.semestre);
        alunoRepository.save(aluno);

        return UsuarioResponse.de(aluno, true);
    }

    // ---------------- RF02 - Login ----------------
    @Transactional
    public LoginResponse login(LoginRequest dados) {
        String email = normalizarEmail(dados.email);
        Optional<Aluno> encontrado = alunoRepository.findByEmail(email);

        // Mesma mensagem para "nao existe" e "senha errada": nao revela quais e-mails estao cadastrados.
        if (encontrado.isEmpty()) {
            throw RegraDeNegocioException.naoAutorizado("E-mail ou senha invalidos");
        }
        Aluno aluno = encontrado.get();
        if (!senhaService.confere(dados.senha, aluno.getSenhaHash())) {
            throw RegraDeNegocioException.naoAutorizado("E-mail ou senha invalidos");
        }

        Sessao sessao = new Sessao();
        sessao.setToken(UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", ""));
        sessao.setAluno(aluno);
        sessao.setExpiraEm(LocalDateTime.now().plusDays(validadeTokenDias));
        sessaoRepository.save(sessao);

        return new LoginResponse(sessao.getToken(), UsuarioResponse.de(aluno, true));
    }

    // ---------------- RF03 - Perfil ----------------
    @Transactional(readOnly = true)
    public UsuarioResponse buscarPerfil(Long id, Long alunoLogadoId) {
        Aluno aluno = buscarAluno(id);
        boolean ehOProprio = aluno.getId().equals(alunoLogadoId);
        return UsuarioResponse.de(aluno, ehOProprio);
    }

    @Transactional
    public UsuarioResponse atualizarPerfil(Long id, Long alunoLogadoId, PerfilRequest dados) {
        if (!id.equals(alunoLogadoId)) {
            throw RegraDeNegocioException.semPermissao("Voce so pode alterar o seu proprio perfil");
        }
        Aluno aluno = buscarAluno(id);
        aluno.setNome(dados.nome.trim());
        aluno.setCurso(textoOuNulo(dados.curso));
        aluno.setSemestre(dados.semestre);
        aluno.setDescricao(textoOuNulo(dados.descricao));
        aluno.setHabilidades(montarHabilidades(dados.habilidades));
        alunoRepository.save(aluno);
        return UsuarioResponse.de(aluno, true);
    }

    /**
     * Percorre a lista de nomes digitados, ignora vazios e repetidos,
     * reaproveita habilidades que ja existem no banco e cria as novas.
     */
    private Set<Habilidade> montarHabilidades(List<String> nomes) {
        Set<Habilidade> resultado = new HashSet<>();
        Set<String> jaAdicionados = new HashSet<>();
        if (nomes == null) {
            return resultado;
        }
        for (String nomeDigitado : nomes) {
            if (nomeDigitado == null || nomeDigitado.isBlank()) {
                continue;
            }
            String nome = nomeDigitado.trim();
            if (nome.length() > 60) {
                throw RegraDeNegocioException.dadosInvalidos("Habilidade com mais de 60 caracteres: " + nome);
            }
            if (jaAdicionados.contains(nome.toLowerCase())) {
                continue;
            }
            Optional<Habilidade> existente = habilidadeRepository.findByNomeIgnoreCase(nome);
            Habilidade habilidade;
            if (existente.isPresent()) {
                habilidade = existente.get();
            } else {
                habilidade = habilidadeRepository.save(new Habilidade(nome));
            }
            resultado.add(habilidade);
            jaAdicionados.add(nome.toLowerCase());
        }
        if (resultado.size() > MAXIMO_HABILIDADES) {
            throw RegraDeNegocioException.dadosInvalidos("Informe no maximo " + MAXIMO_HABILIDADES + " habilidades");
        }
        return resultado;
    }

    public Aluno buscarAluno(Long id) {
        Optional<Aluno> aluno = alunoRepository.findById(id);
        if (aluno.isEmpty()) {
            throw RegraDeNegocioException.naoEncontrado("Aluno nao encontrado");
        }
        return aluno.get();
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String textoOuNulo(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return texto.trim();
    }
}
