package br.una.netuni.repository;

import br.una.netuni.model.Avaliacao;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    Optional<Avaliacao> findByContratacaoId(Long contratacaoId);

    /** Avaliacoes recebidas por um aluno: ele e o prestador da contratacao avaliada. */
    List<Avaliacao> findByContratacaoPrestadorIdOrderByDataDesc(Long prestadorId);
}
