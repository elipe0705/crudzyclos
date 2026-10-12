package br.com.zyclos.web;

import br.com.zyclos.domain.StatusAnuncio;
import br.com.zyclos.domain.StatusDenuncia;
import br.com.zyclos.domain.StatusUsuario;
import br.com.zyclos.dto.request.CategoriaRequest;
import br.com.zyclos.dto.request.ResolverDenunciaRequest;
import br.com.zyclos.dto.request.StatusUsuarioRequest;
import br.com.zyclos.dto.response.AnuncioResumoResponse;
import br.com.zyclos.dto.response.CategoriaResponse;
import br.com.zyclos.dto.response.DashboardResponse;
import br.com.zyclos.dto.response.DenunciaResponse;
import br.com.zyclos.dto.response.PaginaResponse;
import br.com.zyclos.dto.response.RelatorioResponse;
import br.com.zyclos.dto.response.UsuarioAdminResponse;
import br.com.zyclos.security.UsuarioAutenticado;
import br.com.zyclos.service.ServicoAdministrativo;
import br.com.zyclos.service.ServicoDeCategoria;
import br.com.zyclos.service.ServicoDeDenuncia;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Painel administrativo.
 *
 * <p>Toda a arvore /api/admin exige o papel ADMIN, conforme a configuracao de seguranca.
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Administracao", description = "Painel de gestao da plataforma")
public class ControladorAdministrativo {

    private final ServicoAdministrativo administracao;
    private final ServicoDeCategoria categorias;
    private final ServicoDeDenuncia denuncias;

    public ControladorAdministrativo(ServicoAdministrativo administracao, ServicoDeCategoria categorias,
                                     ServicoDeDenuncia denuncias) {
        this.administracao = administracao;
        this.categorias = categorias;
        this.denuncias = denuncias;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Indicadores, crescimento mensal e atividade recente")
    public DashboardResponse dashboard() {
        return administracao.visaoGeral();
    }

    @GetMapping("/relatorios")
    @Operation(summary = "Relatorio por periodo")
    public RelatorioResponse relatorios(@RequestParam(defaultValue = "30 dias") String periodo) {
        return administracao.relatorio(periodo);
    }

    @GetMapping("/anuncios")
    @Operation(summary = "Lista todos os anuncios, incluindo pausados e removidos")
    public PaginaResponse<AnuncioResumoResponse> anuncios(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) StatusAnuncio status,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanhoDaPagina) {
        return administracao.listarAnuncios(q, status, pagina, tamanhoDaPagina);
    }

    @GetMapping("/usuarios")
    @Operation(summary = "Lista as contas da comunidade")
    public PaginaResponse<UsuarioAdminResponse> usuarios(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) StatusUsuario status,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanhoDaPagina) {
        return administracao.listarUsuarios(q, status, pagina, tamanhoDaPagina);
    }

    @PatchMapping("/usuarios/{id}/status")
    @Operation(summary = "Ativa, suspende ou inativa uma conta")
    public UsuarioAdminResponse alterarStatus(@PathVariable Long id,
                                              @Valid @RequestBody StatusUsuarioRequest pedido,
                                              @AuthenticationPrincipal UsuarioAutenticado autenticado) {
        return administracao.alterarStatus(autenticado.getUsuario(), id, pedido);
    }

    @GetMapping("/categorias")
    @Operation(summary = "Lista todas as categorias, ativas e inativas")
    public List<CategoriaResponse> categorias() {
        return categorias.listarTodas();
    }

    @PostMapping("/categorias")
    @Operation(summary = "Cria uma categoria")
    public ResponseEntity<CategoriaResponse> criarCategoria(@Valid @RequestBody CategoriaRequest pedido) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categorias.criar(pedido));
    }

    @PutMapping("/categorias/{id}")
    @Operation(summary = "Renomeia ou ativa e desativa uma categoria")
    public CategoriaResponse atualizarCategoria(@PathVariable Long id,
                                                @Valid @RequestBody CategoriaRequest pedido) {
        return categorias.atualizar(id, pedido);
    }

    @DeleteMapping("/categorias/{id}")
    @Operation(summary = "Remove uma categoria sem anuncios ativos")
    public ResponseEntity<Void> removerCategoria(@PathVariable Long id) {
        categorias.remover(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/denuncias")
    @Operation(summary = "Lista as denuncias da moderacao")
    public PaginaResponse<DenunciaResponse> denuncias(
            @RequestParam(required = false) StatusDenuncia status,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanhoDaPagina) {
        return denuncias.listar(status, pagina, tamanhoDaPagina);
    }

    @PatchMapping("/denuncias/{id}")
    @Operation(summary = "Registra o parecer da moderacao sobre a denuncia")
    public DenunciaResponse resolverDenuncia(@PathVariable Long id,
                                             @Valid @RequestBody ResolverDenunciaRequest pedido) {
        return denuncias.resolver(id, pedido);
    }
}
