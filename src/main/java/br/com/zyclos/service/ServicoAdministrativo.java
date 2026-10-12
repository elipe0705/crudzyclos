package br.com.zyclos.service;

import br.com.zyclos.domain.Anuncio;
import br.com.zyclos.domain.StatusAnuncio;
import br.com.zyclos.domain.StatusDenuncia;
import br.com.zyclos.domain.StatusProposta;
import br.com.zyclos.domain.StatusUsuario;
import br.com.zyclos.domain.Usuario;
import br.com.zyclos.dto.request.StatusUsuarioRequest;
import br.com.zyclos.dto.response.AnuncioResumoResponse;
import br.com.zyclos.dto.response.AtividadeResponse;
import br.com.zyclos.dto.response.DashboardResponse;
import br.com.zyclos.dto.response.IndicadorResponse;
import br.com.zyclos.dto.response.PaginaResponse;
import br.com.zyclos.dto.response.PontoSerieResponse;
import br.com.zyclos.dto.response.RelatorioResponse;
import br.com.zyclos.dto.response.UsuarioAdminResponse;
import br.com.zyclos.exception.RecursoNaoEncontradoException;
import br.com.zyclos.exception.RegraDeNegocioException;
import br.com.zyclos.mapper.Conversores;
import br.com.zyclos.repository.AnuncioRepository;
import br.com.zyclos.repository.DenunciaRepository;
import br.com.zyclos.repository.EspecificacoesDeAnuncio;
import br.com.zyclos.repository.PropostaTrocaRepository;
import br.com.zyclos.repository.UsuarioRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Consultas e acoes do painel administrativo. */
@Service
public class ServicoAdministrativo {

    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");
    private static final Locale BRASIL = Locale.forLanguageTag("pt-BR");
    private static final int MESES_NO_GRAFICO = 6;

    private final UsuarioRepository usuarios;
    private final AnuncioRepository anuncios;
    private final PropostaTrocaRepository propostas;
    private final DenunciaRepository denuncias;

    public ServicoAdministrativo(UsuarioRepository usuarios, AnuncioRepository anuncios,
                                 PropostaTrocaRepository propostas, DenunciaRepository denuncias) {
        this.usuarios = usuarios;
        this.anuncios = anuncios;
        this.propostas = propostas;
        this.denuncias = denuncias;
    }

    @Transactional(readOnly = true)
    public DashboardResponse visaoGeral() {
        long totalAnuncios = anuncios.countByStatus(StatusAnuncio.ATIVO);
        long totalUsuarios = usuarios.countByStatus(StatusUsuario.ATIVO);
        long trocasConcluidas = propostas.countByStatus(StatusProposta.CONCLUIDA);
        long denunciasPendentes = denuncias.countByStatus(StatusDenuncia.PENDENTE);

        List<PontoSerieResponse> serie = serieMensal(MESES_NO_GRAFICO);
        List<IndicadorResponse> indicadores = List.of(
                new IndicadorResponse("Total de anuncios", totalAnuncios, variacao(serie, true)),
                new IndicadorResponse("Usuarios cadastrados", totalUsuarios, variacao(serie, false)),
                new IndicadorResponse("Trocas realizadas", trocasConcluidas, null),
                new IndicadorResponse("Denuncias pendentes", denunciasPendentes, null));

        List<Anuncio> recentes = anuncios.maisRecentes(PageRequest.of(0, 5));
        List<AnuncioResumoResponse> ultimos = recentes.stream()
                .map(anuncio -> Conversores.resumo(anuncio, Set.of()))
                .toList();
        List<AtividadeResponse> atividades = recentes.stream()
                .map(anuncio -> new AtividadeResponse(
                        "Novo anuncio publicado: " + anuncio.getTitulo(), "anuncio", anuncio.getCriadoEm()))
                .toList();

        return new DashboardResponse(indicadores, serie, atividades, ultimos);
    }

    @Transactional(readOnly = true)
    public RelatorioResponse relatorio(String periodo) {
        int meses = switch (periodo == null ? "" : periodo.toLowerCase()) {
            case "hoje", "7 dias", "7dias" -> 1;
            case "12 meses", "12meses" -> 12;
            default -> MESES_NO_GRAFICO;
        };
        List<PontoSerieResponse> serie = serieMensal(meses);
        List<IndicadorResponse> variacoes = List.of(
                new IndicadorResponse("Usuarios", totalNaSerie(serie, false), variacao(serie, false)),
                new IndicadorResponse("Anuncios", totalNaSerie(serie, true), variacao(serie, true)));

        return new RelatorioResponse(
                periodo == null ? "30 dias" : periodo,
                serie,
                variacoes,
                usuarios.count(),
                anuncios.count(),
                propostas.countByStatus(StatusProposta.CONCLUIDA),
                denuncias.count());
    }

    @Transactional(readOnly = true)
    public PaginaResponse<UsuarioAdminResponse> listarUsuarios(String termo, StatusUsuario status,
                                                               int pagina, int tamanho) {
        Pageable paginacao = PageRequest.of(Math.max(pagina, 0), tamanho <= 0 ? 20 : Math.min(tamanho, 100),
                Sort.by(Sort.Direction.DESC, "criadoEm"));
        String busca = termo == null || termo.isBlank() ? null : termo.trim();
        return PaginaResponse.de(usuarios.buscar(busca, status, paginacao), usuario -> new UsuarioAdminResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getPapel(),
                usuario.getStatus(),
                anuncios.countByVendedorAndStatus(usuario, StatusAnuncio.ATIVO),
                usuario.getCriadoEm()));
    }

    @Transactional
    public UsuarioAdminResponse alterarStatus(Usuario administrador, Long id, StatusUsuarioRequest pedido) {
        Usuario alvo = usuarios.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Usuario", id));
        if (Objects.equals(alvo.getId(), administrador.getId())) {
            throw new RegraDeNegocioException("Voce nao pode alterar o status da propria conta.");
        }
        alvo.setStatus(pedido.status());
        usuarios.save(alvo);
        return new UsuarioAdminResponse(
                alvo.getId(), alvo.getNome(), alvo.getUsername(), alvo.getEmail(),
                alvo.getPapel(), alvo.getStatus(),
                anuncios.countByVendedorAndStatus(alvo, StatusAnuncio.ATIVO), alvo.getCriadoEm());
    }

    @Transactional(readOnly = true)
    public PaginaResponse<AnuncioResumoResponse> listarAnuncios(String termo, StatusAnuncio status,
                                                                int pagina, int tamanho) {
        var criterios = org.springframework.data.jpa.domain.Specification.allOf(
                EspecificacoesDeAnuncio.contendo(termo),
                EspecificacoesDeAnuncio.comStatus(status));
        Pageable paginacao = PageRequest.of(Math.max(pagina, 0), tamanho <= 0 ? 20 : Math.min(tamanho, 100),
                Sort.by(Sort.Direction.DESC, "criadoEm"));
        return PaginaResponse.de(anuncios.findAll(criterios, paginacao),
                anuncio -> Conversores.resumo(anuncio, Set.of()));
    }

    /** Contagem de cadastros e publicacoes por mes, do mais antigo para o mais recente. */
    private List<PontoSerieResponse> serieMensal(int meses) {
        YearMonth atual = YearMonth.now(FUSO);
        YearMonth inicio = atual.minusMonths(Math.max(meses, 1) - 1L);
        Instant desde = inicio.atDay(1).atStartOfDay(FUSO).toInstant();

        Map<YearMonth, Long> porMesUsuarios = agrupar(usuarios.cadastrosDesde(desde));
        Map<YearMonth, Long> porMesAnuncios = agrupar(anuncios.publicacoesDesde(desde));

        List<PontoSerieResponse> serie = new ArrayList<>();
        for (YearMonth mes = inicio; !mes.isAfter(atual); mes = mes.plusMonths(1)) {
            serie.add(new PontoSerieResponse(
                    nomeDoMes(mes),
                    porMesUsuarios.getOrDefault(mes, 0L),
                    porMesAnuncios.getOrDefault(mes, 0L)));
        }
        return serie;
    }

    private static Map<YearMonth, Long> agrupar(List<Instant> datas) {
        return datas.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        data -> YearMonth.from(LocalDate.ofInstant(data, FUSO)), Collectors.counting()));
    }

    private static String nomeDoMes(YearMonth mes) {
        String nome = mes.getMonth().getDisplayName(TextStyle.SHORT, BRASIL).replace(".", "");
        return nome.substring(0, 1).toUpperCase(BRASIL) + nome.substring(1);
    }

    /** Variacao percentual do ultimo mes em relacao ao anterior, no formato usado nos cartoes. */
    private static String variacao(List<PontoSerieResponse> serie, boolean sobreAnuncios) {
        if (serie.size() < 2) {
            return null;
        }
        long ultimo = valor(serie.get(serie.size() - 1), sobreAnuncios);
        long anterior = valor(serie.get(serie.size() - 2), sobreAnuncios);
        if (anterior == 0) {
            return ultimo == 0 ? "0%" : "+100%";
        }
        long percentual = Math.round((ultimo - anterior) * 100.0 / anterior);
        return (percentual >= 0 ? "+" : "") + percentual + "%";
    }

    private static long totalNaSerie(List<PontoSerieResponse> serie, boolean sobreAnuncios) {
        return serie.stream().mapToLong(ponto -> valor(ponto, sobreAnuncios)).sum();
    }

    private static long valor(PontoSerieResponse ponto, boolean sobreAnuncios) {
        return sobreAnuncios ? ponto.anuncios() : ponto.usuarios();
    }
}
