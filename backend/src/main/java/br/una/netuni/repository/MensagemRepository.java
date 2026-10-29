package br.una.netuni.repository;

import br.una.netuni.model.Mensagem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {

    List<Mensagem> findByContratacaoIdOrderByDataAscIdAsc(Long contratacaoId);
}
