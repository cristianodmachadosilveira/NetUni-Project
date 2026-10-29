package br.una.netuni.repository;

import br.una.netuni.model.Contratacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContratacaoRepository extends JpaRepository<Contratacao, Long> {

    /** Contratacoes em que o aluno participa, como contratante OU como prestador. */
    List<Contratacao> findByContratanteIdOrPrestadorIdOrderByDataDesc(Long contratanteId, Long prestadorId);

    List<Contratacao> findByServicoId(Long servicoId);
}
