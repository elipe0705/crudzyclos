package br.com.zyclos.repository;

import br.com.zyclos.domain.Anuncio;
import br.com.zyclos.domain.Favorito;
import br.com.zyclos.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    Optional<Favorito> findByUsuarioAndAnuncio(Usuario usuario, Anuncio anuncio);

    boolean existsByUsuarioAndAnuncio(Usuario usuario, Anuncio anuncio);

    List<Favorito> findByUsuarioOrderByCriadoEmDesc(Usuario usuario);

    long countByAnuncio(Anuncio anuncio);

    /** Ids dos anuncios favoritados pelo usuario, usados para marcar o coracao nos cards. */
    @Query("select f.anuncio.id from Favorito f where f.usuario = :usuario")
    List<Long> idsDosAnunciosFavoritados(Usuario usuario);
}
