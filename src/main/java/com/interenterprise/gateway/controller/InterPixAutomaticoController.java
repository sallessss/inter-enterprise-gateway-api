package com.interenterprise.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interenterprise.gateway.dto.common.WebhookRequest;
import com.interenterprise.gateway.dto.pixautomatico.PixAutomaticoDtos.CobrRequest;
import com.interenterprise.gateway.dto.pixautomatico.PixAutomaticoDtos.CobrResponse;
import com.interenterprise.gateway.dto.pixautomatico.PixAutomaticoDtos.LocRecRequest;
import com.interenterprise.gateway.dto.pixautomatico.PixAutomaticoDtos.LocRecResponse;
import com.interenterprise.gateway.dto.pixautomatico.PixAutomaticoDtos.RecorrenciaRequest;
import com.interenterprise.gateway.dto.pixautomatico.PixAutomaticoDtos.RecorrenciaResponse;
import com.interenterprise.gateway.dto.pixautomatico.PixAutomaticoDtos.RetentativaRequest;
import com.interenterprise.gateway.dto.pixautomatico.PixAutomaticoDtos.SolicRecRequest;
import com.interenterprise.gateway.dto.pixautomatico.PixAutomaticoDtos.SolicRecResponse;
import com.interenterprise.gateway.dto.pixautomatico.PixAutomaticoDtos.WebhookResponse;
import com.interenterprise.gateway.service.InterPixAutomaticoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inter/pix-automatico")
public class InterPixAutomaticoController {

	private final InterPixAutomaticoService service;

	public InterPixAutomaticoController(InterPixAutomaticoService service) {
		this.service = service;
	}

	@PostMapping("/rec")
	public ResponseEntity<RecorrenciaResponse> criarRecorrencia(@Valid @RequestBody RecorrenciaRequest request) {
		return ResponseEntity.ok(service.criarRecorrencia(request));
	}

	@GetMapping("/rec/{idRec}")
	public ResponseEntity<RecorrenciaResponse> consultarRecorrencia(@PathVariable String idRec) {
		return ResponseEntity.ok(service.consultarRecorrencia(idRec));
	}

	@PatchMapping("/rec/{idRec}")
	public ResponseEntity<RecorrenciaResponse> alterarRecorrencia(
		@PathVariable String idRec,
		@Valid @RequestBody RecorrenciaRequest request
	) {
		return ResponseEntity.ok(service.alterarRecorrencia(idRec, request));
	}

	@PostMapping("/solicrec")
	public ResponseEntity<SolicRecResponse> criarSolicitacaoRecorrencia(@Valid @RequestBody SolicRecRequest request) {
		return ResponseEntity.ok(service.criarSolicitacaoRecorrencia(request));
	}

	@GetMapping("/solicrec/{idSolicRec}")
	public ResponseEntity<SolicRecResponse> consultarSolicitacaoRecorrencia(@PathVariable String idSolicRec) {
		return ResponseEntity.ok(service.consultarSolicitacaoRecorrencia(idSolicRec));
	}

	@PatchMapping("/solicrec/{idSolicRec}")
	public ResponseEntity<SolicRecResponse> alterarSolicitacaoRecorrencia(
		@PathVariable String idSolicRec,
		@Valid @RequestBody SolicRecRequest request
	) {
		return ResponseEntity.ok(service.alterarSolicitacaoRecorrencia(idSolicRec, request));
	}

	@PutMapping("/cobr/{txid}")
	public ResponseEntity<CobrResponse> criarCobrancaRecorrente(@PathVariable String txid, @Valid @RequestBody CobrRequest request) {
		return ResponseEntity.ok(service.criarCobrancaRecorrente(txid, request));
	}

	@GetMapping("/cobr/{txid}")
	public ResponseEntity<CobrResponse> consultarCobrancaRecorrente(@PathVariable String txid) {
		return ResponseEntity.ok(service.consultarCobrancaRecorrente(txid));
	}

	@PatchMapping("/cobr/{txid}")
	public ResponseEntity<CobrResponse> alterarCobrancaRecorrente(@PathVariable String txid, @Valid @RequestBody CobrRequest request) {
		return ResponseEntity.ok(service.alterarCobrancaRecorrente(txid, request));
	}

	@PostMapping("/cobr/{txid}/retentativa/{data}")
	public ResponseEntity<CobrResponse> solicitarRetentativa(
		@PathVariable String txid,
		@PathVariable String data,
		@Valid @RequestBody RetentativaRequest request
	) {
		return ResponseEntity.ok(service.solicitarRetentativa(txid, data, request));
	}

	@PostMapping("/locrec")
	public ResponseEntity<LocRecResponse> criarLocationRecorrencia(@Valid @RequestBody LocRecRequest request) {
		return ResponseEntity.ok(service.criarLocationRecorrencia(request));
	}

	@GetMapping("/locrec/{id}")
	public ResponseEntity<LocRecResponse> consultarLocationRecorrencia(@PathVariable String id) {
		return ResponseEntity.ok(service.consultarLocationRecorrencia(id));
	}

	@DeleteMapping("/locrec/{id}/idRec")
	public ResponseEntity<Void> desvincularLocationRecorrencia(@PathVariable String id) {
		service.desvincularLocationRecorrencia(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/webhookrec")
	public ResponseEntity<WebhookResponse> configurarWebhookRecorrencia(@Valid @RequestBody WebhookRequest request) {
		return ResponseEntity.ok(service.configurarWebhookRecorrencia(request));
	}

	@PutMapping("/webhookcobr")
	public ResponseEntity<WebhookResponse> configurarWebhookCobrancaRecorrente(@Valid @RequestBody WebhookRequest request) {
		return ResponseEntity.ok(service.configurarWebhookCobrancaRecorrente(request));
	}
}
