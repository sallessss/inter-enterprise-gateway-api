package com.interenterprise.gateway.dto.pix;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.interenterprise.gateway.dto.common.QueryParams;

public final class PixDtos {

	private PixDtos() {
	}

	public record CobRequest(
		@NotNull @Valid Calendario calendario,
		Devedor devedor,
		Loc loc,
		@NotNull @Valid Valor valor,
		@NotBlank @Size(max = 77) String chave,
		@Size(max = 140) String solicitacaoPagador,
		List<InfoAdicional> infoAdicionais
	) {
	}

	public record CobResponse(
		String txid,
		Integer revisao,
		String status,
		Calendario calendario,
		Devedor devedor,
		Loc loc,
		Valor valor,
		String chave,
		String solicitacaoPagador,
		List<InfoAdicional> infoAdicionais,
		String pixCopiaECola,
		List<PixRecebido> pix
	) {
	}

	public record Calendario(Integer expiracao, OffsetDateTime criacao, OffsetDateTime apresentacao) {
	}

	public record Valor(
		@NotBlank @Pattern(regexp = "\\d{1,10}\\.\\d{2}") String original,
		Integer modalidadeAlteracao
	) {
	}

	public record Devedor(PessoaFisica pessoaFisica, PessoaJuridica pessoaJuridica) {
	}

	public record PessoaFisica(@NotBlank String cpf, @NotBlank String nome) {
	}

	public record PessoaJuridica(@NotBlank String cnpj, @NotBlank String nome) {
	}

	public record Loc(Long id, String location, String tipoCob, OffsetDateTime criacao) {
	}

	public record InfoAdicional(@NotBlank @Size(max = 50) String nome, @NotBlank @Size(max = 200) String valor) {
	}

	public record PagarCobrancaPixRequest(@NotNull BigDecimal valor) {
	}

	public record PagarCobrancaPixResponse(String e2e) {
	}

	public record PixRecebido(String endToEndId, String txid, String valor, OffsetDateTime horario, Devedor pagador) {
	}

	public record PixListResponse(Parametros parametros, List<PixRecebido> pix) {
	}

	public record Parametros(OffsetDateTime inicio, OffsetDateTime fim, Paginacao paginacao) {
	}

	public record Paginacao(Integer paginaAtual, Integer itensPorPagina, Integer quantidadeDePaginas, Integer quantidadeTotalDeItens) {
	}

	public record DevolucaoRequest(BigDecimal valor, String natureza, String descricao) {
	}

	public record DevolucaoResponse(String id, String rtrId, String valor, String horario, String status) {
	}

	public record RetryCallbacksRequest(@NotNull @Size(min = 1, max = 50) List<String> txId, @NotBlank String chavePix) {
	}

	public record PixFilter(
		OffsetDateTime inicio,
		OffsetDateTime fim,
		String txid,
		String cpf,
		String cnpj,
		Integer paginaAtual,
		Integer itensPorPagina
	) implements QueryParams {

		@Override
		public Map<String, ?> toQueryParams() {
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("inicio", inicio);
			params.put("fim", fim);
			params.put("txid", txid);
			params.put("cpf", cpf);
			params.put("cnpj", cnpj);
			params.put("paginacao.paginaAtual", paginaAtual);
			params.put("paginacao.itensPorPagina", itensPorPagina);
			return params;
		}
	}
}
