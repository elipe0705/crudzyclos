package br.com.zyclos.repository;

import br.com.zyclos.domain.Avaliacao;
import br.com.zyclos.domain.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    List<Avaliacao> findByAvaliadoOrderByCriadoEmDesc(Usuario avaliado);

    long countByAvaliado(Usuario avaliado);

    boolean existsByAutorAndAvaliado(Usuario autor, Usuario avaliado);

    /** Media das notas recebidas, ou nulo quando o perfil ainda nao tem avaliacoes. */
    @Query("select avg(a.nota) from Avaliacao a where a.avaliado = :avaliado")
    Double mediaDoPerfil(@Param("avaliado") Usuario avaliado);
}
