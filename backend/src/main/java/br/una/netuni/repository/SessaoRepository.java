package br.una.netuni.repository;

import br.una.netuni.model.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessaoRepository extends JpaRepository<Sessao, String> {
}
