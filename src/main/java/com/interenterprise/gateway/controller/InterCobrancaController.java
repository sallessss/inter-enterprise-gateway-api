package com.interenterprise.gateway.controller;

import java.time.LocalDate;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.CancelarCobrancaRequest;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.CobrancaDetalhadaResponse;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.CobrancaFilter;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.CobrancasResponse;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.EditarCobrancaRequest;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.EditarCobrancaResponse;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.EmitirCobrancaRequest;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.EmitirCobrancaResponse;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.PagarCobrancaRequest;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.RetryCallbacksRequest;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.SumarioCobrancasResponse;
import com.interenterprise.gateway.dto.common.CallbackDtos.CallbackFilter;
import com.interenterprise.gateway.dto.common.CallbackDtos.CallbackPage;
import com.interenterprise.gateway.dto.common.RetryCallbackResponse;
import com.interenterprise.gateway.dto.common.WebhookRequest;
import com.interenterprise.gateway.service.InterCobrancaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inter/cobrancas")
public class InterCobrancaController {

	private final InterCobrancaService service;

	public InterCobrancaController(InterCobrancaService service) {
		this.service = service;
	}

	@GetMapping
	public ResponseEntity<CobrancasResponse> listar(
		@RequestParam(required = false) LocalDate dataInicial,
		@RequestParam(required = false) LocalDate dataFinal,
		@RequestParam(required = false) String filtrarDataPor,
		@RequestParam(required = false) String situacao,
		@RequestParam(required = false) String pessoaPagadora,
		@RequestParam(required = false) String cpfCnpjPessoaPagadora,
		@RequestParam(required = false) String seuNumero,
		@RequestParam(required = false) String tipoCobranca,
		@RequestParam(required = false) Integer itensPorPagina,
		@RequestParam(required = false) Integer paginaAtual,
		@RequestParam(required = false) String ordenarPor,
		@RequestParam(required = false) String tipoOrdenacao
	) {
		CobrancaFilter filter = new CobrancaFilter(dataInicial, dataFinal, filtrarDataPor, situacao, pessoaPagadora,
			cpfCnpjPessoaPagadora, seuNumero, tipoCobranca, itensPorPagina, paginaAtual, ordenarPor, tipoOrdenacao);
		return ResponseEntity.ok(service.listar(filter));
	}

	@PostMapping
	public ResponseEntity<EmitirCobrancaResponse> emitir(@Valid @RequestBody EmitirCobrancaRequest request) {
		return ResponseEntity.ok(service.emitir(request));
	}

	@GetMapping("/{codigoSolicitacao}")
	public ResponseEntity<CobrancaDetalhadaResponse> consultar(@PathVariable String codigoSolicitacao) {
		return ResponseEntity.ok(service.consultar(codigoSolicitacao));
	}

	@PatchMapping("/edicao/{codigoEdicao}")
	public ResponseEntity<EditarCobrancaResponse> editar(
		@PathVariable String codigoEdicao,
		@Valid @RequestBody EditarCobrancaRequest request
	) {
		return ResponseEntity.ok(service.editar(codigoEdicao, request));
	}

	@GetMapping(value = "/{codigoSolicitacao}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> pdf(@PathVariable String codigoSolicitacao) {
		return ResponseEntity.ok(service.pdf(codigoSolicitacao));
	}

	@PostMapping("/{codigoSolicitacao}/cancelar")
	public ResponseEntity<CobrancaDetalhadaResponse> cancelar(
		@PathVariable String codigoSolicitacao,
		@Valid @RequestBody CancelarCobrancaRequest request
	) {
		return ResponseEntity.ok(service.cancelar(codigoSolicitacao, request));
	}

	@GetMapping("/sumario")
	public ResponseEntity<SumarioCobrancasResponse> sumario(
		@RequestParam(required = false) LocalDate dataInicial,
		@RequestParam(required = false) LocalDate dataFinal,
		@RequestParam(required = false) String filtrarDataPor,
		@RequestParam(required = false) String situacao,
		@RequestParam(required = false) String pessoaPagadora,
		@RequestParam(required = false) String cpfCnpjPessoaPagadora,
		@RequestParam(required = false) String seuNumero,
		@RequestParam(required = false) String tipoCobranca
	) {
		CobrancaFilter filter = new CobrancaFilter(dataInicial, dataFinal, filtrarDataPor, situacao, pessoaPagadora,
			cpfCnpjPessoaPagadora, seuNumero, tipoCobranca, null, null, null, null);
		return ResponseEntity.ok(service.sumario(filter));
	}

	@PostMapping("/{codigoSolicitacao}/pagar")
	public ResponseEntity<CobrancaDetalhadaResponse> pagar(
		@PathVariable String codigoSolicitacao,
		@Valid @RequestBody PagarCobrancaRequest request
	) {
		return ResponseEntity.ok(service.pagar(codigoSolicitacao, request));
	}

	@PutMapping("/webhook")
	public ResponseEntity<WebhookRequest> configurarWebhook(@Valid @RequestBody WebhookRequest request) {
		return ResponseEntity.ok(service.configurarWebhook(request));
	}

	@GetMapping("/webhook/callbacks")
	public ResponseEntity<CallbackPage> consultarCallbacks(
		@RequestParam(required = false) java.time.OffsetDateTime dataHoraInicio,
		@RequestParam(required = false) java.time.OffsetDateTime dataHoraFim,
		@RequestParam(required = false) Integer paginaAtual,
		@RequestParam(required = false) Integer itensPorPagina
	) {
		return ResponseEntity.ok(service.consultarCallbacks(new CallbackFilter(dataHoraInicio, dataHoraFim, paginaAtual, itensPorPagina)));
	}

	@PostMapping("/webhook/callbacks/retry")
	public ResponseEntity<RetryCallbackResponse> reenviarCallback(@Valid @RequestBody RetryCallbacksRequest request) {
		return ResponseEntity.ok(service.reenviarCallback(request));
	}
}
