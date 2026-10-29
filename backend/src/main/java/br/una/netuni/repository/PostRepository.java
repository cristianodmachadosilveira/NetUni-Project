package br.una.netuni.repository;

import br.una.netuni.model.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    /** Os 100 posts mais recentes: sao os candidatos do feed "Para voce". */
    List<Post> findTop100ByOrderByDataDescIdDesc();

    List<Post> findByComunidadeIdOrderByDataDescIdDesc(Long comunidadeId);

    List<Post> findByAutorIdOrderByDataDescIdDesc(Long autorId);

    long countByComunidadeId(Long comunidadeId);
}
