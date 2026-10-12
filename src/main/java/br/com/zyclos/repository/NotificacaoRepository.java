package br.com.zyclos.repository;

import br.com.zyclos.domain.Notificacao;
import br.com.zyclos.domain.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    List<Notificacao> findByUsuarioOrderByCriadoEmDesc(Usuario usuario);

    long countByUsuarioAndLidaFalse(Usuario usuario);

    @Modifying
    @Query("update Notificacao n set n.lida = true where n.usuario = :usuario and n.lida = false")
    int marcarTodasComoLidas(@Param("usuario") Usuario usuario);
}
