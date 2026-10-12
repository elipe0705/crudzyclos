package br.com.zyclos.repository;

import br.com.zyclos.domain.Categoria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findByNomeIgnoreCase(String nome);

    Optional<Categoria> findBySlugIgnoreCase(String slug);

    boolean existsByNomeIgnoreCase(String nome);

    List<Categoria> findAllByAtivaTrueOrderByNomeAsc();

    List<Categoria> findAllByOrderByNomeAsc();
}
