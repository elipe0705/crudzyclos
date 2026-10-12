package br.com.zyclos.repository;

import br.com.zyclos.domain.StatusUsuario;
import br.com.zyclos.domain.Usuario;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    Optional<Usuario> findByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    long countByStatus(StatusUsuario status);

    @Query("""
            select u from Usuario u
            where (:termo is null
                   or lower(u.nome) like lower(concat('%', :termo, '%'))
                   or lower(u.username) like lower(concat('%', :termo, '%'))
                   or lower(u.email) like lower(concat('%', :termo, '%')))
              and (:status is null or u.status = :status)
            """)
    Page<Usuario> buscar(@Param("termo") String termo, @Param("status") StatusUsuario status, Pageable paginacao);

    /** Datas de cadastro a partir de um instante, usadas na serie mensal do painel. */
    @Query("select u.criadoEm from Usuario u where u.criadoEm >= :desde")
    List<Instant> cadastrosDesde(@Param("desde") Instant desde);
}
