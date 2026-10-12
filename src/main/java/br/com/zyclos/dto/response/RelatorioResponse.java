package br.com.zyclos.dto.response;

import java.util.List;

/** Relatorio por periodo do painel administrativo. */
public record RelatorioResponse(
        String periodo,
        List<PontoSerieResponse> serie,
        List<IndicadorResponse> variacoes,
        long totalUsuarios,
        long totalAnuncios,
        long totalTrocas,
        long totalDenuncias) {
}
