package br.com.zyclos.repository;

import br.com.zyclos.domain.Seguidor;
import br.com.zyclos.domain.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeguidorRepository extends JpaRepository<Seguidor, Long> {

    Optional<Seguidor> findBySeguidorAndSeguido(Usuario seguidor, Usuario seguido);

    boolean existsBySeguidorAndSeguido(Usuario seguidor, Usuario seguido);

    /** Quantas pessoas acompanham este perfil. */
    long countBySeguido(Usuario seguido);

    /** Quantos perfis este usuario acompanha. */
    long countBySeguidor(Usuario seguidor);
}
