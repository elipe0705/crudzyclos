package br.com.zyclos.web;

import br.com.zyclos.dto.response.CategoriaResponse;
import br.com.zyclos.service.ServicoDeCategoria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Categorias usadas no catalogo e na home. */
@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "Categorias disponiveis no catalogo")
public class ControladorDeCategoria {

    private final ServicoDeCategoria categorias;

    public ControladorDeCategoria(ServicoDeCategoria categorias) {
        this.categorias = categorias;
    }

    @GetMapping
    @SecurityRequirements
    @Operation(summary = "Lista as categorias ativas com a contagem de anuncios")
    public List<CategoriaResponse> listar() {
        return categorias.listarAtivas();
    }
}
