package com.interenterprise.gateway.dto.common;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public final class CallbackDtos {

	private CallbackDtos() {
	}

	public record CallbackPage(
		Long totalElementos,
		Integer totalPaginas,
		Boolean primeiraPagina,
		Boolean ultimaPagina,
		List<CallbackAttempt> data
	) {
	}

	public record CallbackAttempt(
		String webhookUrl,
		String payload,
		Integer numeroTentativa,
		OffsetDateTime dataHoraDisparo,
		Boolean sucesso,
		Integer httpStatus,
		String mensagemErro
	) {
	}

	public record CallbackFilter(
		OffsetDateTime dataHoraInicio,
		OffsetDateTime dataHoraFim,
		Integer paginaAtual,
		Integer itensPorPagina
	) implements QueryParams {

		@Override
		public Map<String, ?> toQueryParams() {
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("dataHoraInicio", dataHoraInicio);
			params.put("dataHoraFim", dataHoraFim);
			params.put("paginaAtual", paginaAtual);
			params.put("itensPorPagina", itensPorPagina);
			return params;
		}
	}
}
