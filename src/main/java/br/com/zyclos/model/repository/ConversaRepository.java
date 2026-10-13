package br.com.zyclos.model.repository;

import br.com.zyclos.model.entity.Conversa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversaRepository extends JpaRepository<Conversa, Long> {
}
