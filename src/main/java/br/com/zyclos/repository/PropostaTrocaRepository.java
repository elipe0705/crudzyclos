package br.com.zyclos.repository;

import br.com.zyclos.domain.Anuncio;
import br.com.zyclos.domain.PropostaTroca;
import br.com.zyclos.domain.StatusProposta;
import br.com.zyclos.domain.Usuario;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PropostaTrocaRepository extends JpaRepository<PropostaTroca, Long> {

    List<PropostaTroca> findByProponenteAndStatusInOrderByCriadoEmDesc(
            Usuario proponente, Collection<StatusProposta> status);

    List<PropostaTroca> findByDestinatarioAndStatusInOrderByCriadoEmDesc(
            Usuario destinatario, Collection<StatusProposta> status);

    boolean existsByProponenteAndAnuncioDesejadoAndStatus(
            Usuario proponente, Anuncio anuncioDesejado, StatusProposta status);

    long countByStatus(StatusProposta status);

    /** Propostas ja encerradas em que o usuario participou, usadas na aba Historico. */
    @Query("""
            select p from PropostaTroca p
            where (p.proponente = :usuario or p.destinatario = :usuario)
              and p.status in :status
            order by p.atualizadoEm desc
            """)
    List<PropostaTroca> historico(@Param("usuario") Usuario usuario, @Param("status") Collection<StatusProposta> status);
}
