package br.com.zyclos.web;

import br.com.zyclos.dto.response.AnuncioResumoResponse;
import br.com.zyclos.security.UsuarioAutenticado;
import br.com.zyclos.service.ServicoDeFavorito;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Pecas salvas pelo usuario. */
@RestController
@RequestMapping("/api/favoritos")
@Tag(name = "Favoritos", description = "Pecas salvas pelo usuario")
public class ControladorDeFavorito {

    private final ServicoDeFavorito favoritos;

    public ControladorDeFavorito(ServicoDeFavorito favoritos) {
        this.favoritos = favoritos;
    }

    @GetMapping
    @Operation(summary = "Lista os anuncios favoritados")
    public List<AnuncioResumoResponse> listar(@AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return favoritos.listar(autenticado.getUsuario());
    }

    @GetMapping("/ids")
    @Operation(summary = "Devolve apenas os ids favoritados, para marcar os cards")
    public List<Long> ids(@AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return favoritos.ids(autenticado.getUsuario());
    }

    @PostMapping("/{anuncioId}")
    @Operation(summary = "Favorita ou desfavorita um anuncio")
    public Map<String, Boolean> alternar(@PathVariable Long anuncioId,
                                         @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return Map.of("favorito", favoritos.alternar(autenticado.getUsuario(), anuncioId));
    }

    @DeleteMapping("/{anuncioId}")
    @Operation(summary = "Remove o anuncio dos favoritos")
    public ResponseEntity<Void> remover(@PathVariable Long anuncioId,
                                        @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        favoritos.remover(autenticado.getUsuario(), anuncioId);
        return ResponseEntity.noContent().build();
    }
}
