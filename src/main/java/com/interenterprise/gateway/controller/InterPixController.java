package com.interenterprise.gateway.controller;

import java.time.OffsetDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.interenterprise.gateway.dto.common.CallbackDtos.CallbackFilter;
import com.interenterprise.gateway.dto.common.CallbackDtos.CallbackPage;
import com.interenterprise.gateway.dto.common.RetryCallbackResponse;
import com.interenterprise.gateway.dto.common.WebhookRequest;
import com.interenterprise.gateway.dto.pix.PixDtos.CobRequest;
import com.interenterprise.gateway.dto.pix.PixDtos.CobResponse;
import com.interenterprise.gateway.dto.pix.PixDtos.DevolucaoRequest;
import com.interenterprise.gateway.dto.pix.PixDtos.DevolucaoResponse;
import com.interenterprise.gateway.dto.pix.PixDtos.PagarCobrancaPixRequest;
import com.interenterprise.gateway.dto.pix.PixDtos.PagarCobrancaPixResponse;
import com.interenterprise.gateway.dto.pix.PixDtos.PixFilter;
import com.interenterprise.gateway.dto.pix.PixDtos.PixListResponse;
import com.interenterprise.gateway.dto.pix.PixDtos.PixRecebido;
import com.interenterprise.gateway.dto.pix.PixDtos.RetryCallbacksRequest;
import com.interenterprise.gateway.service.InterPixService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inter/pix")
public class InterPixController {

	private final InterPixService service;

	public InterPixController(InterPixService service) {
		this.service = service;
	}

	@PutMapping("/cob/{txid}")
	public ResponseEntity<CobResponse> criarCobrancaImediata(@PathVariable String txid, @Valid @RequestBody CobRequest request) {
		return ResponseEntity.ok(service.criarCobrancaImediata(txid, request));
	}

	@PostMapping("/cob")
	public ResponseEntity<CobResponse> criarCobrancaImediata(@Valid @RequestBody CobRequest request) {
		return ResponseEntity.ok(service.criarCobrancaImediata(request));
	}

	@GetMapping("/cob/{txid}")
	public ResponseEntity<CobResponse> consultarCobrancaImediata(@PathVariable String txid) {
		return ResponseEntity.ok(service.consultarCobrancaImediata(txid));
	}

	@PostMapping("/cob/pagar/{txid}")
	public ResponseEntity<PagarCobrancaPixResponse> pagarCobrancaImediata(
		@PathVariable String txid,
		@Valid @RequestBody PagarCobrancaPixRequest request
	) {
		return ResponseEntity.ok(service.pagarCobrancaImediata(txid, request));
	}

	@PutMapping("/cobv/{txid}")
	public ResponseEntity<CobResponse> criarCobrancaVencimento(@PathVariable String txid, @Valid @RequestBody CobRequest request) {
		return ResponseEntity.ok(service.criarCobrancaVencimento(txid, request));
	}

	@PostMapping("/cobv")
	public ResponseEntity<CobResponse> criarCobrancaVencimento(@Valid @RequestBody CobRequest request) {
		return ResponseEntity.ok(service.criarCobrancaVencimento(request));
	}

	@GetMapping("/cobv/{txid}")
	public ResponseEntity<CobResponse> consultarCobrancaVencimento(@PathVariable String txid) {
		return ResponseEntity.ok(service.consultarCobrancaVencimento(txid));
	}

	@PostMapping("/cobv/pagar/{txid}")
	public ResponseEntity<PagarCobrancaPixResponse> pagarCobrancaVencimento(
		@PathVariable String txid,
		@Valid @RequestBody PagarCobrancaPixRequest request
	) {
		return ResponseEntity.ok(service.pagarCobrancaVencimento(txid, request));
	}

	@GetMapping("/{e2eId}")
	public ResponseEntity<PixRecebido> consultarPix(@PathVariable String e2eId) {
		return ResponseEntity.ok(service.consultarPix(e2eId));
	}

	@GetMapping
	public ResponseEntity<PixListResponse> listarPix(
		@RequestParam(required = false) OffsetDateTime inicio,
		@RequestParam(required = false) OffsetDateTime fim,
		@RequestParam(required = false) String txid,
		@RequestParam(required = false) String cpf,
		@RequestParam(required = false) String cnpj,
		@RequestParam(required = false) Integer paginaAtual,
		@RequestParam(required = false) Integer itensPorPagina
	) {
		return ResponseEntity.ok(service.listarPix(new PixFilter(inicio, fim, txid, cpf, cnpj, paginaAtual, itensPorPagina)));
	}

	@PutMapping("/{e2eId}/devolucao/{id}")
	public ResponseEntity<DevolucaoResponse> solicitarDevolucao(
		@PathVariable String e2eId,
		@PathVariable String id,
		@Valid @RequestBody DevolucaoRequest request
	) {
		return ResponseEntity.ok(service.solicitarDevolucao(e2eId, id, request));
	}

	@PutMapping("/webhook/{chave}")
	public ResponseEntity<WebhookRequest> configurarWebhook(@PathVariable String chave, @Valid @RequestBody WebhookRequest request) {
		return ResponseEntity.ok(service.configurarWebhook(chave, request));
	}

	@GetMapping("/webhook/callbacks")
	public ResponseEntity<CallbackPage> consultarCallbacks(
		@RequestParam(required = false) OffsetDateTime dataHoraInicio,
		@RequestParam(required = false) OffsetDateTime dataHoraFim,
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
