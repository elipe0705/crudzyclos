package br.com.zyclos.model.repository;

import br.com.zyclos.model.entity.AnuncioImagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnuncioImagemRepository extends JpaRepository<AnuncioImagem, Long> {
}
