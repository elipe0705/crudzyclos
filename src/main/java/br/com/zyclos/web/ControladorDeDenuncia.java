package br.com.zyclos.web;

import br.com.zyclos.dto.request.DenunciaRequest;
import br.com.zyclos.dto.response.DenunciaResponse;
import br.com.zyclos.security.UsuarioAutenticado;
import br.com.zyclos.service.ServicoDeDenuncia;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Abertura de denuncias pela comunidade. */
@RestController
@RequestMapping("/api/denuncias")
@Tag(name = "Denuncias", description = "Ocorrencias abertas pela comunidade")
public class ControladorDeDenuncia {

    private final ServicoDeDenuncia denuncias;

    public ControladorDeDenuncia(ServicoDeDenuncia denuncias) {
        this.denuncias = denuncias;
    }

    @PostMapping
    @Operation(summary = "Registra uma denuncia sobre um anuncio ou sobre um perfil")
    public ResponseEntity<DenunciaResponse> registrar(@Valid @RequestBody DenunciaRequest pedido,
                                                      @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        DenunciaResponse criada = denuncias.registrar(autenticado.getUsuario(), pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }
}
