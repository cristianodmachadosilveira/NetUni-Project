package br.una.netuni.repository;

import br.una.netuni.model.Comentario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    List<Comentario> findByPostIdOrderByDataAscIdAsc(Long postId);

    long countByPostId(Long postId);

    void deleteByPostId(Long postId);
}
