package com.interenterprise.gateway.dto.pixautomatico;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class PixAutomaticoDtos {

	private PixAutomaticoDtos() {
	}

	public record RecorrenciaRequest(String idRec, CalendarioRecorrencia calendario, ValorRecorrencia valor, Recebedor recebedor, Vinculo vinculo) {
	}

	public record RecorrenciaResponse(String idRec, String status, CalendarioRecorrencia calendario, ValorRecorrencia valor) {
	}

	public record CalendarioRecorrencia(LocalDate dataInicial, LocalDate dataFinal, String periodicidade) {
	}

	public record ValorRecorrencia(BigDecimal valorRec, BigDecimal valorMinimoRecebedor) {
	}

	public record Recebedor(String convenio, String nome, String cpf, String cnpj) {
	}

	public record Vinculo(String contrato, String devedor, String objeto) {
	}

	public record SolicRecRequest(@NotBlank String idRec, Destinatario destinatario, CalendarioSolicitacao calendario) {
	}

	public record SolicRecResponse(String idSolicRec, String idRec, String status) {
	}

	public record Destinatario(String cpf, String cnpj, String nome) {
	}

	public record CalendarioSolicitacao(LocalDate dataExpiracaoSolicitacao) {
	}

	public record CobrRequest(@NotBlank String idRec, @NotNull LocalDate dataDeVencimento, @NotNull BigDecimal valor) {
	}

	public record CobrResponse(String txid, String idRec, String status, BigDecimal valor) {
	}

	public record RetentativaRequest(String motivo) {
	}

	public record LocRecRequest(String idRec) {
	}

	public record LocRecResponse(Long id, String location, String idRec) {
	}

	public record WebhookResponse(String webhookUrl, String status) {
	}

	public record ListaResponse<T>(List<T> data) {
	}
}
