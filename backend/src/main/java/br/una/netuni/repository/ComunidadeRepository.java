package br.una.netuni.repository;

import br.una.netuni.model.Comunidade;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComunidadeRepository extends JpaRepository<Comunidade, Long> {

    List<Comunidade> findAllByOrderByNomeAsc();

    boolean existsByNomeIgnoreCase(String nome);
}
