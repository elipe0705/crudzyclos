package br.com.zyclos.model.repository;

import br.com.zyclos.model.entity.Seguidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeguidorRepository extends JpaRepository<Seguidor, Long> {
}
