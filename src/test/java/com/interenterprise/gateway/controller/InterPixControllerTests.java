package com.interenterprise.gateway.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.interenterprise.gateway.dto.common.CallbackDtos.CallbackPage;
import com.interenterprise.gateway.dto.common.RetryCallbackResponse;
import com.interenterprise.gateway.dto.common.WebhookRequest;
import com.interenterprise.gateway.dto.pix.PixDtos.CobResponse;
import com.interenterprise.gateway.dto.pix.PixDtos.DevolucaoResponse;
import com.interenterprise.gateway.dto.pix.PixDtos.PagarCobrancaPixResponse;
import com.interenterprise.gateway.dto.pix.PixDtos.PixListResponse;
import com.interenterprise.gateway.dto.pix.PixDtos.PixRecebido;
import com.interenterprise.gateway.exception.GlobalExceptionHandler;
import com.interenterprise.gateway.service.InterPixService;

class InterPixControllerTests {

	private InterPixService service;
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		service = mock(InterPixService.class);

		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();

		mockMvc = MockMvcBuilders.standaloneSetup(new InterPixController(service))
			.setControllerAdvice(new GlobalExceptionHandler())
			.setValidator(validator)
			.build();
	}

	@Test
	void createsImmediateChargeWithTxid() throws Exception {
		when(service.criarCobrancaImediata(eq("txid123"), any())).thenReturn(cob("txid123"));

		mockMvc.perform(put("/api/inter/pix/cob/txid123")
				.contentType(MediaType.APPLICATION_JSON)
				.content(validCobRequest()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.txid").value("txid123"))
			.andExpect(jsonPath("$.status").value("ATIVA"));
	}

	@Test
	void createsImmediateChargeWithoutTxid() throws Exception {
		when(service.criarCobrancaImediata(any())).thenReturn(cob("txid-gerado"));

		mockMvc.perform(post("/api/inter/pix/cob")
				.contentType(MediaType.APPLICATION_JSON)
				.content(validCobRequest()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.txid").value("txid-gerado"));
	}

	@Test
	void returnsImmediateCharge() throws Exception {
		when(service.consultarCobrancaImediata("txid123")).thenReturn(cob("txid123"));

		mockMvc.perform(get("/api/inter/pix/cob/txid123"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.txid").value("txid123"));
	}

	@Test
	void paysImmediateCharge() throws Exception {
		when(service.pagarCobrancaImediata(eq("txid123"), any())).thenReturn(new PagarCobrancaPixResponse("E123"));

		mockMvc.perform(post("/api/inter/pix/cob/pagar/txid123")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"valor\":10.00}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.e2e").value("E123"));
	}

	@Test
	void createsDueDateChargeWithTxid() throws Exception {
		when(service.criarCobrancaVencimento(eq("txidv123"), any())).thenReturn(cob("txidv123"));

		mockMvc.perform(put("/api/inter/pix/cobv/txidv123")
				.contentType(MediaType.APPLICATION_JSON)
				.content(validCobRequest()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.txid").value("txidv123"));
	}

	@Test
	void createsDueDateChargeWithoutTxid() throws Exception {
		when(service.criarCobrancaVencimento(any())).thenReturn(cob("txidv-gerado"));

		mockMvc.perform(post("/api/inter/pix/cobv")
				.contentType(MediaType.APPLICATION_JSON)
				.content(validCobRequest()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.txid").value("txidv-gerado"));
	}

	@Test
	void returnsDueDateCharge() throws Exception {
		when(service.consultarCobrancaVencimento("txidv123")).thenReturn(cob("txidv123"));

		mockMvc.perform(get("/api/inter/pix/cobv/txidv123"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.txid").value("txidv123"));
	}

	@Test
	void paysDueDateCharge() throws Exception {
		when(service.pagarCobrancaVencimento(eq("txidv123"), any())).thenReturn(new PagarCobrancaPixResponse("E456"));

		mockMvc.perform(post("/api/inter/pix/cobv/pagar/txidv123")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"valor\":10.00}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.e2e").value("E456"));
	}

	@Test
	void returnsPix() throws Exception {
		when(service.consultarPix("E123")).thenReturn(new PixRecebido("E123", "txid123", "10.00", null, null));

		mockMvc.perform(get("/api/inter/pix/E123"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.endToEndId").value("E123"));
	}

	@Test
	void listsPix() throws Exception {
		when(service.listarPix(any())).thenReturn(new PixListResponse(null, List.of(new PixRecebido("E123", "txid123", "10.00", null, null))));

		mockMvc.perform(get("/api/inter/pix")
				.param("inicio", "2026-07-01T00:00:00-03:00")
				.param("fim", "2026-07-31T23:59:59-03:00"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pix[0].endToEndId").value("E123"));
	}

	@Test
	void requestsRefund() throws Exception {
		when(service.solicitarDevolucao(eq("E123"), eq("dev-1"), any())).thenReturn(new DevolucaoResponse("dev-1", "rtr-1", "10.00", null, "EM_PROCESSAMENTO"));

		mockMvc.perform(put("/api/inter/pix/E123/devolucao/dev-1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"valor\":10.00,\"natureza\":\"ORIGINAL\",\"descricao\":\"teste\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value("dev-1"));
	}

	@Test
	void configuresWebhook() throws Exception {
		when(service.configurarWebhook(eq("chave-pix"), any())).thenReturn(new WebhookRequest("https://example.com/pix"));

		mockMvc.perform(put("/api/inter/pix/webhook/chave-pix")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"webhookUrl\":\"https://example.com/pix\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.webhookUrl").value("https://example.com/pix"));
	}

	@Test
	void returnsWebhookCallbacks() throws Exception {
		when(service.consultarCallbacks(any())).thenReturn(new CallbackPage(0L, 0, true, true, List.of()));

		mockMvc.perform(get("/api/inter/pix/webhook/callbacks"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").isArray());
	}

	@Test
	void retriesWebhookCallbacks() throws Exception {
		when(service.reenviarCallback(any())).thenReturn(new RetryCallbackResponse(List.of("txid123")));

		mockMvc.perform(post("/api/inter/pix/webhook/callbacks/retry")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"txId\":[\"txid123\"],\"chavePix\":\"chave-pix\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.foundIds[0]").value("txid123"));
	}

	@Test
	void rejectsInvalidImmediateChargeRequest() throws Exception {
		mockMvc.perform(post("/api/inter/pix/cob")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error").value("Validation failed"));
	}

	private CobResponse cob(String txid) {
		return new CobResponse(txid, 0, "ATIVA", null, null, null, null, "chave-pix", null, null, "pix-copia-e-cola", null);
	}

	private String validCobRequest() {
		return """
			{
				"calendario": { "expiracao": 3600 },
				"valor": { "original": "10.00" },
				"chave": "chave-pix"
			}
			""";
	}
}
