package com.interenterprise.gateway.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.interenterprise.gateway.dto.common.InterWebhookDtos.WebhookReceivedResponse;
import com.interenterprise.gateway.service.InterWebhookReceiverService;

class InterWebhookReceiverControllerTests {

	private InterWebhookReceiverService service;
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		service = mock(InterWebhookReceiverService.class);
		mockMvc = MockMvcBuilders.standaloneSetup(new InterWebhookReceiverController(service)).build();
	}

	@Test
	void receivesCobrancaWebhook() throws Exception {
		when(service.receive(any())).thenReturn(response("COBRANCA", null));

		mockMvc.perform(post("/api/inter/webhooks/cobranca")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"codigoSolicitacao\":\"codigo-123\"}"))
			.andExpect(status().isAccepted())
			.andExpect(jsonPath("$.product").value("COBRANCA"));
	}

	@Test
	void receivesPixWebhook() throws Exception {
		when(service.receive(any())).thenReturn(response("PIX", null));

		mockMvc.perform(post("/api/inter/webhooks/pix")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"txid\":\"txid-1\"}"))
			.andExpect(status().isAccepted())
			.andExpect(jsonPath("$.product").value("PIX"));
	}

	@Test
	void receivesBankingWebhook() throws Exception {
		when(service.receive(any())).thenReturn(response("BANKING", "pagamento"));

		mockMvc.perform(post("/api/inter/webhooks/banking/pagamento")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"codigoTransacao\":\"tx-1\"}"))
			.andExpect(status().isAccepted())
			.andExpect(jsonPath("$.product").value("BANKING"))
			.andExpect(jsonPath("$.subtype").value("pagamento"));
	}

	@Test
	void receivesPixAutomaticoRecorrenciaWebhook() throws Exception {
		when(service.receive(any())).thenReturn(response("PIX_AUTOMATICO", "RECORRENCIA"));

		mockMvc.perform(post("/api/inter/webhooks/pix-automatico/recorrencia")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"idRec\":\"rec-1\"}"))
			.andExpect(status().isAccepted())
			.andExpect(jsonPath("$.subtype").value("RECORRENCIA"));
	}

	@Test
	void receivesPixAutomaticoCobrancaWebhook() throws Exception {
		when(service.receive(any())).thenReturn(response("PIX_AUTOMATICO", "COBRANCA"));

		mockMvc.perform(post("/api/inter/webhooks/pix-automatico/cobranca")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"txid\":\"txid-1\"}"))
			.andExpect(status().isAccepted())
			.andExpect(jsonPath("$.subtype").value("COBRANCA"));
	}

	private WebhookReceivedResponse response(String product, String subtype) {
		return new WebhookReceivedResponse(product, subtype, OffsetDateTime.parse("2026-07-01T09:00:00-03:00"));
	}
}
