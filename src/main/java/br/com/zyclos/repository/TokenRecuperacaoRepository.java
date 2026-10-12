package br.com.zyclos.repository;

import br.com.zyclos.domain.TokenRecuperacao;
import br.com.zyclos.domain.Usuario;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TokenRecuperacaoRepository extends JpaRepository<TokenRecuperacao, Long> {

    Optional<TokenRecuperacao> findByCodigo(String codigo);

    /** Invalida os codigos ainda abertos de um usuario antes de emitir um novo. */
    @Modifying
    @Query("update TokenRecuperacao t set t.usadoEm = :agora where t.usuario = :usuario and t.usadoEm is null")
    int invalidarPendentes(@Param("usuario") Usuario usuario, @Param("agora") Instant agora);
}
