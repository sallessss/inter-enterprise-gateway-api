package com.interenterprise.gateway.dto.cobranca;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.interenterprise.gateway.dto.common.QueryParams;

public final class CobrancaDtos {

	private CobrancaDtos() {
	}

	public record EmitirCobrancaRequest(
		@NotBlank @Size(max = 15) String seuNumero,
		@NotNull @DecimalMin("2.5") @DecimalMax("99999999.99") BigDecimal valorNominal,
		@NotNull LocalDate dataVencimento,
		@NotNull @Min(0) @Max(60) Integer numDiasAgenda,
		@NotNull @Valid Pagador pagador,
		Desconto desconto,
		Multa multa,
		Mora mora,
		Mensagem mensagem,
		BeneficiarioFinal beneficiarioFinal,
		List<String> formasRecebimento
	) {
	}

	public record EmitirCobrancaResponse(String codigoSolicitacao) {
	}

	public record CobrancaDetalhadaResponse(
		Cobranca cobranca,
		Boleto boleto,
		Pix pix
	) {
	}

	public record CobrancasResponse(
		Integer totalPaginas,
		Integer totalElementos,
		Integer tamanhoPagina,
		Boolean primeiraPagina,
		Boolean ultimaPagina,
		Integer numeroDeElementos,
		List<CobrancaDetalhadaResponse> cobrancas
	) {
	}

	public record Cobranca(
		String codigoSolicitacao,
		String seuNumero,
		LocalDate dataEmissao,
		LocalDate dataVencimento,
		BigDecimal valorNominal,
		String tipoCobranca,
		String situacao,
		LocalDate dataSituacao,
		String valorTotalRecebido,
		String origemRecebimento,
		String motivoCancelamento,
		Boolean arquivada,
		Pagador pagador
	) {
	}

	public record Pagador(
		@NotBlank String cpfCnpj,
		@NotBlank String nome,
		String tipoPessoa,
		String email,
		String ddd,
		String telefone,
		String cep,
		String numero,
		String complemento,
		String bairro,
		String cidade,
		String uf,
		String endereco
	) {
	}

	public record BeneficiarioFinal(String cpfCnpj, String nome) {
	}

	public record Mensagem(
		@Size(max = 78) String linha1,
		@Size(max = 78) String linha2,
		@Size(max = 78) String linha3,
		@Size(max = 78) String linha4,
		@Size(max = 78) String linha5
	) {
	}

	public record Desconto(String codigo, Integer quantidadeDias, BigDecimal taxa, BigDecimal valor) {
	}

	public record Multa(String codigo, BigDecimal taxa, BigDecimal valor) {
	}

	public record Mora(String codigo, BigDecimal taxa, BigDecimal valor) {
	}

	public record Boleto(String nossoNumero, String codigoBarras, String linhaDigitavel) {
	}

	public record Pix(String txid, String pixCopiaECola) {
	}

	public record EditarCobrancaRequest(LocalDate dataVencimento, BigDecimal valorNominal) {
	}

	public record EditarCobrancaResponse(String status, String mensagem, String codigoEdicao) {
	}

	public record CancelarCobrancaRequest(@NotBlank @Size(max = 50) String motivoCancelamento) {
	}

	public record PagarCobrancaRequest(@NotBlank String pagarCom) {
	}

	public record RetryCallbacksRequest(@NotNull @Size(min = 1, max = 50) List<String> codigoSolicitacao) {
	}

	public record SumarioCobrancasResponse(List<ItemSumarioCobrancas> itens) {
	}

	public record ItemSumarioCobrancas(String situacao, BigDecimal valor, Long quantidade) {
	}

	public record CobrancaFilter(
		LocalDate dataInicial,
		LocalDate dataFinal,
		String filtrarDataPor,
		String situacao,
		String pessoaPagadora,
		String cpfCnpjPessoaPagadora,
		String seuNumero,
		String tipoCobranca,
		Integer itensPorPagina,
		Integer paginaAtual,
		String ordenarPor,
		String tipoOrdenacao
	) implements QueryParams {

		@Override
		public Map<String, ?> toQueryParams() {
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("dataInicial", dataInicial);
			params.put("dataFinal", dataFinal);
			params.put("filtrarDataPor", filtrarDataPor);
			params.put("situacao", situacao);
			params.put("pessoaPagadora", pessoaPagadora);
			params.put("cpfCnpjPessoaPagadora", cpfCnpjPessoaPagadora);
			params.put("seuNumero", seuNumero);
			params.put("tipoCobranca", tipoCobranca);
			params.put("itensPorPagina", itensPorPagina);
			params.put("paginaAtual", paginaAtual);
			params.put("ordenarPor", ordenarPor);
			params.put("tipoOrdenacao", tipoOrdenacao);
			return params;
		}
	}
}
