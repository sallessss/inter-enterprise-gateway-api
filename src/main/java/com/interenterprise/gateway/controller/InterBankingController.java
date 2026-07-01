package com.interenterprise.gateway.controller;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interenterprise.gateway.dto.banking.BankingDtos.DarfRequest;
import com.interenterprise.gateway.dto.banking.BankingDtos.ExtratoFilter;
import com.interenterprise.gateway.dto.banking.BankingDtos.ExtratoResponse;
import com.interenterprise.gateway.dto.banking.BankingDtos.LoteResponse;
import com.interenterprise.gateway.dto.banking.BankingDtos.PagamentoLoteRequest;
import com.interenterprise.gateway.dto.banking.BankingDtos.PagamentoRequest;
import com.interenterprise.gateway.dto.banking.BankingDtos.PagamentoResponse;
import com.interenterprise.gateway.dto.banking.BankingDtos.PixPagamentoRequest;
import com.interenterprise.gateway.dto.banking.BankingDtos.PixPagamentoResponse;
import com.interenterprise.gateway.dto.banking.BankingDtos.RetryCallbacksRequest;
import com.interenterprise.gateway.dto.banking.BankingDtos.SaldoFilter;
import com.interenterprise.gateway.dto.banking.BankingDtos.SaldoResponse;
import com.interenterprise.gateway.dto.common.CallbackDtos.CallbackFilter;
import com.interenterprise.gateway.dto.common.CallbackDtos.CallbackPage;
import com.interenterprise.gateway.dto.common.RetryCallbackResponse;
import com.interenterprise.gateway.dto.common.WebhookRequest;
import com.interenterprise.gateway.service.InterBankingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inter/banking")
public class InterBankingController {

	private final InterBankingService service;

	public InterBankingController(InterBankingService service) {
		this.service = service;
	}

	@GetMapping("/extrato")
	public ResponseEntity<ExtratoResponse> extrato(
		@RequestParam(required = false) LocalDate dataInicio,
		@RequestParam(required = false) LocalDate dataFim,
		@RequestParam(required = false) String tipoOperacao,
		@RequestParam(required = false) Integer paginaAtual,
		@RequestParam(required = false) Integer itensPorPagina
	) {
		return ResponseEntity.ok(service.extrato(new ExtratoFilter(dataInicio, dataFim, tipoOperacao, paginaAtual, itensPorPagina)));
	}

	@GetMapping(value = "/extrato/exportar", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<byte[]> exportarExtrato(
		@RequestParam(required = false) LocalDate dataInicio,
		@RequestParam(required = false) LocalDate dataFim,
		@RequestParam(required = false) String tipoOperacao,
		@RequestParam(required = false) Integer paginaAtual,
		@RequestParam(required = false) Integer itensPorPagina
	) {
		return ResponseEntity.ok(service.exportarExtrato(new ExtratoFilter(dataInicio, dataFim, tipoOperacao, paginaAtual, itensPorPagina)));
	}

	@GetMapping("/extrato/completo")
	public ResponseEntity<ExtratoResponse> extratoCompleto(
		@RequestParam(required = false) LocalDate dataInicio,
		@RequestParam(required = false) LocalDate dataFim,
		@RequestParam(required = false) String tipoOperacao,
		@RequestParam(required = false) Integer paginaAtual,
		@RequestParam(required = false) Integer itensPorPagina
	) {
		return ResponseEntity.ok(service.extratoCompleto(new ExtratoFilter(dataInicio, dataFim, tipoOperacao, paginaAtual, itensPorPagina)));
	}

	@GetMapping("/saldo")
	public ResponseEntity<SaldoResponse> saldo(@RequestParam(required = false) LocalDate dataSaldo) {
		return ResponseEntity.ok(service.saldo(new SaldoFilter(dataSaldo)));
	}

	@PostMapping("/pagamento")
	public ResponseEntity<PagamentoResponse> incluirPagamento(@Valid @RequestBody PagamentoRequest request) {
		return ResponseEntity.ok(service.incluirPagamento(request));
	}

	@PostMapping("/pagamento/darf")
	public ResponseEntity<PagamentoResponse> incluirDarf(@Valid @RequestBody DarfRequest request) {
		return ResponseEntity.ok(service.incluirDarf(request));
	}

	@PostMapping("/pagamento/lote")
	public ResponseEntity<LoteResponse> incluirLote(@Valid @RequestBody PagamentoLoteRequest request) {
		return ResponseEntity.ok(service.incluirLote(request));
	}

	@GetMapping("/pagamento/lote/{idLote}")
	public ResponseEntity<LoteResponse> consultarLote(@PathVariable String idLote) {
		return ResponseEntity.ok(service.consultarLote(idLote));
	}

	@GetMapping("/pagamento/{codigoTransacao}")
	public ResponseEntity<PagamentoResponse> consultarPagamento(@PathVariable String codigoTransacao) {
		return ResponseEntity.ok(service.consultarPagamento(codigoTransacao));
	}

	@PostMapping("/pix")
	public ResponseEntity<PixPagamentoResponse> incluirPix(@Valid @RequestBody PixPagamentoRequest request) {
		return ResponseEntity.ok(service.incluirPix(request));
	}

	@GetMapping("/pix/{codigoSolicitacao}")
	public ResponseEntity<PixPagamentoResponse> consultarPix(@PathVariable String codigoSolicitacao) {
		return ResponseEntity.ok(service.consultarPix(codigoSolicitacao));
	}

	@PutMapping("/webhooks/{tipoWebhook}")
	public ResponseEntity<WebhookRequest> configurarWebhook(@PathVariable String tipoWebhook, @Valid @RequestBody WebhookRequest request) {
		return ResponseEntity.ok(service.configurarWebhook(tipoWebhook, request));
	}

	@GetMapping("/webhooks/{tipoWebhook}/callbacks")
	public ResponseEntity<CallbackPage> consultarCallbacks(
		@PathVariable String tipoWebhook,
		@RequestParam(required = false) OffsetDateTime dataHoraInicio,
		@RequestParam(required = false) OffsetDateTime dataHoraFim,
		@RequestParam(required = false) Integer paginaAtual,
		@RequestParam(required = false) Integer itensPorPagina
	) {
		return ResponseEntity.ok(service.consultarCallbacks(tipoWebhook, new CallbackFilter(dataHoraInicio, dataHoraFim, paginaAtual, itensPorPagina)));
	}

	@PostMapping("/webhooks/{tipoWebhook}/callbacks/retry")
	public ResponseEntity<RetryCallbackResponse> reenviarCallback(
		@PathVariable String tipoWebhook,
		@Valid @RequestBody RetryCallbacksRequest request
	) {
		return ResponseEntity.ok(service.reenviarCallback(tipoWebhook, request));
	}
}
