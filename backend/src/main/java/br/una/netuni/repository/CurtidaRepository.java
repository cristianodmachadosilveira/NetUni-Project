package br.una.netuni.repository;

import br.una.netuni.model.Curtida;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurtidaRepository extends JpaRepository<Curtida, Long> {

    long countByPostId(Long postId);

    Optional<Curtida> findByPostIdAndAlunoId(Long postId, Long alunoId);

    void deleteByPostId(Long postId);
}
