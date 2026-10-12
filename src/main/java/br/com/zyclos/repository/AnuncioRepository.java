package br.com.zyclos.repository;

import br.com.zyclos.domain.Anuncio;
import br.com.zyclos.domain.StatusAnuncio;
import br.com.zyclos.domain.Usuario;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnuncioRepository extends JpaRepository<Anuncio, Long>, JpaSpecificationExecutor<Anuncio> {

    @Query("""
            select a from Anuncio a
            join fetch a.vendedor
            join fetch a.categoria
            where a.id = :id
            """)
    Optional<Anuncio> buscarComRelacionamentos(Long id);

    Page<Anuncio> findByVendedorAndStatus(Usuario vendedor, StatusAnuncio status, Pageable paginacao);

    List<Anuncio> findByVendedorAndStatusOrderByCriadoEmDesc(Usuario vendedor, StatusAnuncio status);

    long countByStatus(StatusAnuncio status);

    long countByVendedorAndStatus(Usuario vendedor, StatusAnuncio status);

    /** Marcas distintas usadas nos anuncios ativos, para alimentar o filtro do catalogo. */
    @Query("select distinct a.marca from Anuncio a where a.status = 'ATIVO' and a.marca is not null order by a.marca")
    List<String> marcasDisponiveis();

    /** Tamanhos distintos usados nos anuncios ativos, para alimentar o filtro do catalogo. */
    @Query("select distinct a.tamanho from Anuncio a where a.status = 'ATIVO' order by a.tamanho")
    List<String> tamanhosDisponiveis();

    /** Datas de publicacao a partir de um instante, usadas na serie mensal do painel. */
    @Query("select a.criadoEm from Anuncio a where a.criadoEm >= :desde")
    List<Instant> publicacoesDesde(@Param("desde") Instant desde);

    /** Ultimos anuncios publicados, com vendedor e categoria ja carregados. */
    @Query("""
            select a from Anuncio a
            join fetch a.vendedor
            join fetch a.categoria
            order by a.criadoEm desc
            """)
    List<Anuncio> maisRecentes(Pageable limite);
}
