package br.com.zyclos.model.repository;

import br.com.zyclos.model.entity.Anuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {
}
