package br.com.zyclos.dto.response;

import java.util.List;

/** Visao geral do painel administrativo. */
public record DashboardResponse(
        List<IndicadorResponse> indicadores,
        List<PontoSerieResponse> crescimento,
        List<AtividadeResponse> atividadeRecente,
        List<AnuncioResumoResponse> anunciosRecentes) {
}
