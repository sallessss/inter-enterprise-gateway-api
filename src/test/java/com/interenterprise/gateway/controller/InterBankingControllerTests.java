package com.interenterprise.gateway.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import com.interenterprise.gateway.dto.banking.BankingDtos.ExtratoResponse;
import com.interenterprise.gateway.dto.banking.BankingDtos.LoteResponse;
import com.interenterprise.gateway.dto.banking.BankingDtos.PagamentoResponse;
import com.interenterprise.gateway.dto.banking.BankingDtos.PixPagamentoResponse;
import com.interenterprise.gateway.dto.banking.BankingDtos.SaldoResponse;
import com.interenterprise.gateway.dto.common.CallbackDtos.CallbackPage;
import com.interenterprise.gateway.dto.common.RetryCallbackResponse;
import com.interenterprise.gateway.dto.common.WebhookRequest;
import com.interenterprise.gateway.exception.GlobalExceptionHandler;
import com.interenterprise.gateway.service.InterBankingService;

class InterBankingControllerTests {

	private InterBankingService service;
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		service = mock(InterBankingService.class);

		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();

		mockMvc = MockMvcBuilders.standaloneSetup(new InterBankingController(service))
			.setControllerAdvice(new GlobalExceptionHandler())
			.setValidator(validator)
			.build();
	}

	@Test
	void returnsExtrato() throws Exception {
		when(service.extrato(any())).thenReturn(new ExtratoResponse(List.of()));

		mockMvc.perform(get("/api/inter/banking/extrato")
				.param("dataInicio", "2026-07-01")
				.param("dataFim", "2026-07-31"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.transacoes").isArray());
	}

	@Test
	void exportsExtrato() throws Exception {
		when(service.exportarExtrato(any())).thenReturn("csv".getBytes());

		mockMvc.perform(get("/api/inter/banking/extrato/exportar"))
			.andExpect(status().isOk())
			.andExpect(content().bytes("csv".getBytes()));
	}

	@Test
	void returnsExtratoCompleto() throws Exception {
		when(service.extratoCompleto(any())).thenReturn(new ExtratoResponse(List.of()));

		mockMvc.perform(get("/api/inter/banking/extrato/completo"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.transacoes").isArray());
	}

	@Test
	void returnsSaldo() throws Exception {
		when(service.saldo(any())).thenReturn(new SaldoResponse(new BigDecimal("100.00"), BigDecimal.ZERO, BigDecimal.ZERO));

		mockMvc.perform(get("/api/inter/banking/saldo"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.disponivel").value(100.00));
	}

	@Test
	void includesPaymentWithValidRequest() throws Exception {
		when(service.incluirPagamento(any())).thenReturn(new PagamentoResponse("tx-1", "PROCESSANDO", "Pagamento recebido"));

		mockMvc.perform(post("/api/inter/banking/pagamento")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"codigoBarras\":\"123456789\",\"valor\":10.00,\"dataPagamento\":\"2026-07-01\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.codigoTransacao").value("tx-1"));
	}

	@Test
	void includesDarf() throws Exception {
		when(service.incluirDarf(any())).thenReturn(new PagamentoResponse("darf-1", "PROCESSANDO", null));

		mockMvc.perform(post("/api/inter/banking/pagamento/darf")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"codigoReceita\":\"0561\",\"valor\":10.00,\"dataPagamento\":\"2026-07-01\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.codigoTransacao").value("darf-1"));
	}

	@Test
	void includesPaymentBatch() throws Exception {
		when(service.incluirLote(any())).thenReturn(new LoteResponse("lote-1", "CRIADO", List.of()));

		mockMvc.perform(post("/api/inter/banking/pagamento/lote")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"pagamentos\":[{\"codigoBarras\":\"123\",\"valor\":10.00}]}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.idLote").value("lote-1"));
	}

	@Test
	void returnsPaymentBatch() throws Exception {
		when(service.consultarLote("lote-1")).thenReturn(new LoteResponse("lote-1", "CRIADO", List.of()));

		mockMvc.perform(get("/api/inter/banking/pagamento/lote/lote-1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.idLote").value("lote-1"));
	}

	@Test
	void returnsPayment() throws Exception {
		when(service.consultarPagamento("tx-1")).thenReturn(new PagamentoResponse("tx-1", "PAGO", null));

		mockMvc.perform(get("/api/inter/banking/pagamento/tx-1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.codigoTransacao").value("tx-1"));
	}

	@Test
	void includesPixPayment() throws Exception {
		when(service.incluirPix(any())).thenReturn(new PixPagamentoResponse("pix-1", "PROCESSANDO", null));

		mockMvc.perform(post("/api/inter/banking/pix")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"valor\":10.00,\"dataPagamento\":\"2026-07-01\",\"descricao\":\"teste\",\"destinatario\":{\"chave\":\"chave-pix\",\"tipo\":\"CHAVE\"}}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.codigoSolicitacao").value("pix-1"));
	}

	@Test
	void returnsPixPayment() throws Exception {
		when(service.consultarPix("pix-1")).thenReturn(new PixPagamentoResponse("pix-1", "PROCESSANDO", null));

		mockMvc.perform(get("/api/inter/banking/pix/pix-1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.codigoSolicitacao").value("pix-1"));
	}

	@Test
	void configuresWebhook() throws Exception {
		when(service.configurarWebhook(eq("pagamento"), any())).thenReturn(new WebhookRequest("https://example.com/webhook"));

		mockMvc.perform(put("/api/inter/banking/webhooks/pagamento")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"webhookUrl\":\"https://example.com/webhook\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.webhookUrl").value("https://example.com/webhook"));
	}

	@Test
	void returnsWebhookCallbacks() throws Exception {
		when(service.consultarCallbacks(eq("pagamento"), any())).thenReturn(new CallbackPage(0L, 0, true, true, List.of()));

		mockMvc.perform(get("/api/inter/banking/webhooks/pagamento/callbacks"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").isArray());
	}

	@Test
	void retriesWebhookCallbacks() throws Exception {
		when(service.reenviarCallback(eq("pagamento"), any())).thenReturn(new RetryCallbackResponse(List.of("tx-1")));

		mockMvc.perform(post("/api/inter/banking/webhooks/pagamento/callbacks/retry")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"codigosSolicitacao\":[\"tx-1\"]}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.foundIds[0]").value("tx-1"));
	}

	@Test
	void rejectsInvalidPaymentRequest() throws Exception {
		mockMvc.perform(post("/api/inter/banking/pagamento")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error").value("Validation failed"));
	}
}
