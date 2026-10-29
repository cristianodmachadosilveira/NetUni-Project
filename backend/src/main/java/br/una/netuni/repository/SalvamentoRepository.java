package br.una.netuni.repository;

import br.una.netuni.model.Salvamento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalvamentoRepository extends JpaRepository<Salvamento, Long> {

    long countByPostId(Long postId);

    Optional<Salvamento> findByPostIdAndAlunoId(Long postId, Long alunoId);

    /** Aba "Salvos": o que o aluno salvou, do mais recente para o mais antigo. */
    List<Salvamento> findByAlunoIdOrderByDataDescIdDesc(Long alunoId);

    void deleteByPostId(Long postId);
}
