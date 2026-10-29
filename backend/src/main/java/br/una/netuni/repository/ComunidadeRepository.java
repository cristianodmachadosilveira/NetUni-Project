package br.una.netuni.repository;

import br.una.netuni.model.Comunidade;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ComunidadeRepository extends JpaRepository<Comunidade, Long> {

    List<Comunidade> findAllByOrderByNomeAsc();

    boolean existsByNomeIgnoreCase(String nome);

    /** Ids das comunidades de que o aluno participa (usado no ranking do feed). */
    @Query("select c.id from Comunidade c join c.membros m where m.id = :alunoId")
    List<Long> idsDasComunidadesDoAluno(@Param("alunoId") Long alunoId);
}
