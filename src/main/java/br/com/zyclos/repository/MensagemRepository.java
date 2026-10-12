package br.com.zyclos.repository;

import br.com.zyclos.domain.Conversa;
import br.com.zyclos.domain.Mensagem;
import br.com.zyclos.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {

    List<Mensagem> findByConversaOrderByEnviadaEmAsc(Conversa conversa);

    Optional<Mensagem> findFirstByConversaOrderByEnviadaEmDesc(Conversa conversa);

    /** Mensagens ainda nao lidas que o usuario recebeu nesta conversa. */
    @Query("select count(m) from Mensagem m where m.conversa = :conversa and m.remetente <> :usuario and m.lida = false")
    long naoLidasNaConversa(@Param("conversa") Conversa conversa, @Param("usuario") Usuario usuario);

    @Modifying
    @Query("update Mensagem m set m.lida = true where m.conversa = :conversa and m.remetente <> :usuario and m.lida = false")
    int marcarRecebidasComoLidas(@Param("conversa") Conversa conversa, @Param("usuario") Usuario usuario);
}
