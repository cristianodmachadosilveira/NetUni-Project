package br.una.netuni.repository;

import br.una.netuni.model.Servico;
import br.una.netuni.model.StatusServico;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

    List<Servico> findByStatusOrderByDataCriacaoDesc(StatusServico status);

    List<Servico> findByAutorIdOrderByDataCriacaoDesc(Long autorId);
}
