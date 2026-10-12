package br.com.zyclos.repository;

import br.com.zyclos.domain.Conversa;
import br.com.zyclos.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConversaRepository extends JpaRepository<Conversa, Long> {

    @Query("""
            select c from Conversa c
            where c.participanteA = :usuario or c.participanteB = :usuario
            order by c.atualizadoEm desc
            """)
    List<Conversa> daPessoa(@Param("usuario") Usuario usuario);

    @Query("""
            select c from Conversa c
            where c.participanteA = :a and c.participanteB = :b
              and ((:anuncioId is null and c.anuncio is null) or c.anuncio.id = :anuncioId)
            """)
    Optional<Conversa> existente(@Param("a") Usuario a, @Param("b") Usuario b, @Param("anuncioId") Long anuncioId);
}
