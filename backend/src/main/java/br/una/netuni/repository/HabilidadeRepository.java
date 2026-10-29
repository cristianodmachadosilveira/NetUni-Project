package br.una.netuni.repository;

import br.una.netuni.model.Habilidade;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabilidadeRepository extends JpaRepository<Habilidade, Long> {

    Optional<Habilidade> findByNomeIgnoreCase(String nome);
}
