package br.com.zyclos.web;

import br.com.zyclos.domain.Usuario;
import br.com.zyclos.dto.request.AtualizarPerfilRequest;
import br.com.zyclos.dto.request.AvaliacaoRequest;
import br.com.zyclos.dto.request.PreferenciasRequest;
import br.com.zyclos.dto.response.AvaliacaoResponse;
import br.com.zyclos.dto.response.PerfilResponse;
import br.com.zyclos.dto.response.PreferenciasResponse;
import br.com.zyclos.dto.response.UsuarioResponse;
import br.com.zyclos.security.UsuarioAutenticado;
import br.com.zyclos.service.ServicoDePerfil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Perfis publicos, seguidores, avaliacoes e preferencias da conta. */
@RestController
@RequestMapping("/api")
@Tag(name = "Perfis", description = "Perfis publicos, seguidores, avaliacoes e preferencias")
public class ControladorDePerfil {

    private final ServicoDePerfil perfis;

    public ControladorDePerfil(ServicoDePerfil perfis) {
        this.perfis = perfis;
    }

    @GetMapping("/perfis/{username}")
    @SecurityRequirements
    @Operation(summary = "Perfil publico com anuncios e indicadores")
    public PerfilResponse perfil(@PathVariable String username,
                                 @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return perfis.perfil(username, conta(autenticado));
    }

    @GetMapping("/perfis/{username}/avaliacoes")
    @SecurityRequirements
    @Operation(summary = "Avaliacoes recebidas por um perfil")
    public List<AvaliacaoResponse> avaliacoes(@PathVariable String username) {
        return perfis.avaliacoesDe(username);
    }

    @PostMapping("/perfis/{username}/avaliacoes")
    @Operation(summary = "Avalia outro perfil, uma vez por pessoa")
    public ResponseEntity<AvaliacaoResponse> avaliar(@PathVariable String username,
                                                     @Valid @RequestBody AvaliacaoRequest pedido,
                                                     @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        AvaliacaoResponse criada = perfis.avaliar(autenticado.getUsuario(), username, pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PostMapping("/perfis/{username}/seguir")
    @Operation(summary = "Segue ou deixa de seguir um perfil")
    public Map<String, Boolean> seguir(@PathVariable String username,
                                       @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return Map.of("seguindo", perfis.alternarSeguir(autenticado.getUsuario(), username));
    }

    @GetMapping("/perfil")
    @Operation(summary = "Perfil da conta autenticada")
    public PerfilResponse meuPerfil(@AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return perfis.perfil(autenticado.getUsuario().getUsername(), autenticado.getUsuario());
    }

    @PutMapping("/perfil")
    @Operation(summary = "Atualiza os dados da conta autenticada")
    public UsuarioResponse atualizar(@Valid @RequestBody AtualizarPerfilRequest pedido,
                                     @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return perfis.atualizar(autenticado.getUsuario(), pedido);
    }

    @GetMapping("/configuracoes")
    @Operation(summary = "Preferencias da conta")
    public PreferenciasResponse preferencias(@AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return perfis.preferencias(autenticado.getUsuario());
    }

    @PutMapping("/configuracoes")
    @Operation(summary = "Salva as preferencias da conta")
    public PreferenciasResponse salvar(@Valid @RequestBody PreferenciasRequest pedido,
                                       @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return perfis.salvarPreferencias(autenticado.getUsuario(), pedido);
    }

    private static Usuario conta(UsuarioAutenticado autenticado) {
        return autenticado == null ? null : autenticado.getUsuario();
    }
}
