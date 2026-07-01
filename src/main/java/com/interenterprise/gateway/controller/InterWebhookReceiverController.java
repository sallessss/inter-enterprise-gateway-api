package com.interenterprise.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interenterprise.gateway.dto.common.InterWebhookDtos.WebhookNotificationRequest;
import com.interenterprise.gateway.dto.common.InterWebhookDtos.WebhookReceivedResponse;
import com.interenterprise.gateway.service.InterWebhookReceiverService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inter/webhooks")
public class InterWebhookReceiverController {

	private final InterWebhookReceiverService service;

	public InterWebhookReceiverController(InterWebhookReceiverService service) {
		this.service = service;
	}

	@PostMapping("/cobranca")
	public ResponseEntity<WebhookReceivedResponse> cobranca(@RequestBody Object payload) {
		return accepted("COBRANCA", null, payload);
	}

	@PostMapping("/pix")
	public ResponseEntity<WebhookReceivedResponse> pix(@RequestBody Object payload) {
		return accepted("PIX", null, payload);
	}

	@PostMapping("/banking/{tipoWebhook}")
	public ResponseEntity<WebhookReceivedResponse> banking(@PathVariable String tipoWebhook, @RequestBody Object payload) {
		return accepted("BANKING", tipoWebhook, payload);
	}

	@PostMapping("/pix-automatico/recorrencia")
	public ResponseEntity<WebhookReceivedResponse> pixAutomaticoRecorrencia(@RequestBody Object payload) {
		return accepted("PIX_AUTOMATICO", "RECORRENCIA", payload);
	}

	@PostMapping("/pix-automatico/cobranca")
	public ResponseEntity<WebhookReceivedResponse> pixAutomaticoCobranca(@RequestBody Object payload) {
		return accepted("PIX_AUTOMATICO", "COBRANCA", payload);
	}

	private ResponseEntity<WebhookReceivedResponse> accepted(String product, String subtype, @Valid Object payload) {
		WebhookNotificationRequest request = new WebhookNotificationRequest(product, subtype, payload);
		return ResponseEntity.accepted().body(service.receive(request));
	}
}
