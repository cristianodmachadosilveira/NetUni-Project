package br.una.netuni.repository;

import br.una.netuni.model.Portfolio;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    List<Portfolio> findByAlunoIdOrderByDataDesc(Long alunoId);
}
