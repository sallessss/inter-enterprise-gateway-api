package com.interenterprise.gateway.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.Cobranca;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.CobrancaDetalhadaResponse;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.CobrancasResponse;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.EditarCobrancaResponse;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.EmitirCobrancaResponse;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.ItemSumarioCobrancas;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.SumarioCobrancasResponse;
import com.interenterprise.gateway.dto.common.CallbackDtos.CallbackPage;
import com.interenterprise.gateway.dto.common.RetryCallbackResponse;
import com.interenterprise.gateway.dto.common.WebhookRequest;
import com.interenterprise.gateway.exception.GlobalExceptionHandler;
import com.interenterprise.gateway.exception.InterApiException;
import com.interenterprise.gateway.service.InterCobrancaService;

class InterCobrancaControllerTests {

	private InterCobrancaService service;
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		service = mock(InterCobrancaService.class);

		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();

		mockMvc = MockMvcBuilders.standaloneSetup(new InterCobrancaController(service))
			.setControllerAdvice(new GlobalExceptionHandler())
			.setValidator(validator)
			.build();
	}

	@Test
	void listsCobrancas() throws Exception {
		when(service.listar(any())).thenReturn(new CobrancasResponse(1, 1, 10, true, true, 1, List.of(cobrancaDetalhada())));

		mockMvc.perform(get("/api/inter/cobrancas")
				.param("dataInicial", "2026-07-01")
				.param("dataFinal", "2026-07-31"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.cobrancas[0].cobranca.codigoSolicitacao").value("codigo-123"));
	}

	@Test
	void returnsValidationErrorForInvalidEmitirCobrancaRequest() throws Exception {
		mockMvc.perform(post("/api/inter/cobrancas")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.error").value("Validation failed"))
			.andExpect(jsonPath("$.fields").isArray());
	}

	@Test
	void emitsCobrancaWithValidRequest() throws Exception {
		when(service.emitir(any())).thenReturn(new EmitirCobrancaResponse("codigo-123"));

		mockMvc.perform(post("/api/inter/cobrancas")
				.contentType(MediaType.APPLICATION_JSON)
				.content(validEmitirRequest()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.codigoSolicitacao").value("codigo-123"));
	}

	@Test
	void returnsCobranca() throws Exception {
		when(service.consultar("codigo-123")).thenReturn(cobrancaDetalhada());

		mockMvc.perform(get("/api/inter/cobrancas/codigo-123"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.cobranca.codigoSolicitacao").value("codigo-123"));
	}

	@Test
	void editsCobranca() throws Exception {
		when(service.editar(any(), any())).thenReturn(new EditarCobrancaResponse("CRIADA", "ok", "edicao-1"));

		mockMvc.perform(patch("/api/inter/cobrancas/edicao/edicao-1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"dataVencimento\":\"2026-08-01\",\"valorNominal\":30.00}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.codigoEdicao").value("edicao-1"));
	}

	@Test
	void returnsPdf() throws Exception {
		when(service.pdf("codigo-123")).thenReturn("pdf".getBytes());

		mockMvc.perform(get("/api/inter/cobrancas/codigo-123/pdf"))
			.andExpect(status().isOk())
			.andExpect(content().bytes("pdf".getBytes()));
	}

	@Test
	void cancelsCobranca() throws Exception {
		when(service.cancelar(any(), any())).thenReturn(cobrancaDetalhada());

		mockMvc.perform(post("/api/inter/cobrancas/codigo-123/cancelar")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"motivoCancelamento\":\"solicitado\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.cobranca.codigoSolicitacao").value("codigo-123"));
	}

	@Test
	void returnsSummary() throws Exception {
		when(service.sumario(any())).thenReturn(new SumarioCobrancasResponse(List.of(new ItemSumarioCobrancas("RECEBIDO", BigDecimal.TEN, 1L))));

		mockMvc.perform(get("/api/inter/cobrancas/sumario"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.itens[0].situacao").value("RECEBIDO"));
	}

	@Test
	void paysCobranca() throws Exception {
		when(service.pagar(any(), any())).thenReturn(cobrancaDetalhada());

		mockMvc.perform(post("/api/inter/cobrancas/codigo-123/pagar")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"pagarCom\":\"SALDO\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.cobranca.codigoSolicitacao").value("codigo-123"));
	}

	@Test
	void configuresWebhook() throws Exception {
		when(service.configurarWebhook(any())).thenReturn(new WebhookRequest("https://example.com/cobranca"));

		mockMvc.perform(put("/api/inter/cobrancas/webhook")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"webhookUrl\":\"https://example.com/cobranca\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.webhookUrl").value("https://example.com/cobranca"));
	}

	@Test
	void returnsWebhookCallbacks() throws Exception {
		when(service.consultarCallbacks(any())).thenReturn(new CallbackPage(0L, 0, true, true, List.of()));

		mockMvc.perform(get("/api/inter/cobrancas/webhook/callbacks"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").isArray());
	}

	@Test
	void retriesWebhookCallbacks() throws Exception {
		when(service.reenviarCallback(any())).thenReturn(new RetryCallbackResponse(List.of("codigo-123")));

		mockMvc.perform(post("/api/inter/cobrancas/webhook/callbacks/retry")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"codigoSolicitacao\":[\"codigo-123\"]}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.foundIds[0]").value("codigo-123"));
	}

	@Test
	void sanitizesInterApiErrorResponse() throws Exception {
		when(service.consultar("abc")).thenThrow(new InterApiException("Inter API returned HTTP 404: access_token=secret-token", 404));

		mockMvc.perform(get("/api/inter/cobrancas/abc"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value(404))
			.andExpect(jsonPath("$.error").value("Not Found"))
			.andExpect(content().string(not(containsString("secret-token"))));
	}

	private CobrancaDetalhadaResponse cobrancaDetalhada() {
		return new CobrancaDetalhadaResponse(
			new Cobranca("codigo-123", "123456", null, null, BigDecimal.TEN, "SIMPLES", "CRIADA", null, null, null, null, false, null),
			null,
			null
		);
	}

	private String validEmitirRequest() {
		return """
			{
				"seuNumero": "123456",
				"valorNominal": 25.90,
				"dataVencimento": "2026-07-30",
				"numDiasAgenda": 10,
				"pagador": {
					"cpfCnpj": "12345678901",
					"nome": "Cliente Teste"
				}
			}
			""";
	}
}
