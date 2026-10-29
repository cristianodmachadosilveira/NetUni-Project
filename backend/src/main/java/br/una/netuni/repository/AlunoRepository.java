package br.una.netuni.repository;

import br.una.netuni.model.Aluno;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Optional<Aluno> findByEmail(String email);
}
