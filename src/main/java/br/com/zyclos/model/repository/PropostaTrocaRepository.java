package br.com.zyclos.model.repository;

import br.com.zyclos.model.entity.PropostaTroca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropostaTrocaRepository extends JpaRepository<PropostaTroca, Long> {
}
