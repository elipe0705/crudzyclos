package br.com.zyclos.repository;

import br.com.zyclos.domain.Denuncia;
import br.com.zyclos.domain.StatusDenuncia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DenunciaRepository extends JpaRepository<Denuncia, Long> {

    Page<Denuncia> findByStatusOrderByCriadoEmDesc(StatusDenuncia status, Pageable paginacao);

    Page<Denuncia> findAllByOrderByCriadoEmDesc(Pageable paginacao);

    long countByStatus(StatusDenuncia status);
}
