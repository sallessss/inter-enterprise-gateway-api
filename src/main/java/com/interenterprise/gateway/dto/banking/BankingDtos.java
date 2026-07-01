package com.interenterprise.gateway.dto.banking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.interenterprise.gateway.dto.common.QueryParams;

public final class BankingDtos {

	private BankingDtos() {
	}

	public record ExtratoResponse(List<Lancamento> transacoes) {
	}

	public record Lancamento(String tipoOperacao, String descricao, BigDecimal valor, LocalDate dataEntrada, String titulo) {
	}

	public record SaldoResponse(BigDecimal disponivel, BigDecimal bloqueado, BigDecimal limite) {
	}

	public record PagamentoRequest(@NotBlank String codigoBarras, BigDecimal valor, LocalDate dataPagamento) {
	}

	public record DarfRequest(@NotBlank String codigoReceita, @NotNull BigDecimal valor, LocalDate dataPagamento) {
	}

	public record PagamentoLoteRequest(@NotNull List<PagamentoRequest> pagamentos) {
	}

	public record PagamentoResponse(String codigoTransacao, String status, String mensagem) {
	}

	public record LoteResponse(String idLote, String status, List<PagamentoResponse> pagamentos) {
	}

	public record PixPagamentoRequest(@NotBlank String chave, @NotNull BigDecimal valor, String descricao) {
	}

	public record PixPagamentoResponse(String codigoSolicitacao, String status, String mensagem) {
	}

	public record RetryCallbacksRequest(List<String> codigosSolicitacao) {
	}

	public record ExtratoFilter(LocalDate dataInicio, LocalDate dataFim, String tipoOperacao, Integer paginaAtual, Integer itensPorPagina) implements QueryParams {

		@Override
		public Map<String, ?> toQueryParams() {
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("dataInicio", dataInicio);
			params.put("dataFim", dataFim);
			params.put("tipoOperacao", tipoOperacao);
			params.put("paginaAtual", paginaAtual);
			params.put("itensPorPagina", itensPorPagina);
			return params;
		}
	}

	public record SaldoFilter(LocalDate dataSaldo) implements QueryParams {

		@Override
		public Map<String, ?> toQueryParams() {
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("dataSaldo", dataSaldo);
			return params;
		}
	}
}
