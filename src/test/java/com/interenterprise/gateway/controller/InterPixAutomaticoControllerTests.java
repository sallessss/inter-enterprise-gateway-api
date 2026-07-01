package com.interenterprise.gateway.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.interenterprise.gateway.dto.pixautomatico.PixAutomaticoDtos.CobrResponse;
import com.interenterprise.gateway.dto.pixautomatico.PixAutomaticoDtos.LocRecResponse;
import com.interenterprise.gateway.dto.pixautomatico.PixAutomaticoDtos.RecorrenciaResponse;
import com.interenterprise.gateway.dto.pixautomatico.PixAutomaticoDtos.SolicRecResponse;
import com.interenterprise.gateway.dto.pixautomatico.PixAutomaticoDtos.WebhookResponse;
import com.interenterprise.gateway.exception.GlobalExceptionHandler;
import com.interenterprise.gateway.service.InterPixAutomaticoService;

class InterPixAutomaticoControllerTests {

	private InterPixAutomaticoService service;
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		service = mock(InterPixAutomaticoService.class);

		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.afterPropertiesSet();

		mockMvc = MockMvcBuilders.standaloneSetup(new InterPixAutomaticoController(service))
			.setControllerAdvice(new GlobalExceptionHandler())
			.setValidator(validator)
			.build();
	}

	@Test
	void createsRecurrence() throws Exception {
		when(service.criarRecorrencia(any())).thenReturn(recorrencia());

		mockMvc.perform(post("/api/inter/pix-automatico/rec")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"idRec\":\"rec-1\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.idRec").value("rec-1"));
	}

	@Test
	void returnsRecurrence() throws Exception {
		when(service.consultarRecorrencia("rec-1")).thenReturn(recorrencia());

		mockMvc.perform(get("/api/inter/pix-automatico/rec/rec-1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.idRec").value("rec-1"))
			.andExpect(jsonPath("$.status").value("CRIADA"));
	}

	@Test
	void updatesRecurrence() throws Exception {
		when(service.alterarRecorrencia(eq("rec-1"), any())).thenReturn(recorrencia());

		mockMvc.perform(patch("/api/inter/pix-automatico/rec/rec-1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"idRec\":\"rec-1\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.idRec").value("rec-1"));
	}

	@Test
	void createsSolicRecWithValidRequest() throws Exception {
		when(service.criarSolicitacaoRecorrencia(any())).thenReturn(new SolicRecResponse("solic-1", "rec-1", "CRIADA"));

		mockMvc.perform(post("/api/inter/pix-automatico/solicrec")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"idRec\":\"rec-1\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.idSolicRec").value("solic-1"));
	}

	@Test
	void returnsSolicRec() throws Exception {
		when(service.consultarSolicitacaoRecorrencia("solic-1")).thenReturn(new SolicRecResponse("solic-1", "rec-1", "CRIADA"));

		mockMvc.perform(get("/api/inter/pix-automatico/solicrec/solic-1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.idSolicRec").value("solic-1"));
	}

	@Test
	void updatesSolicRec() throws Exception {
		when(service.alterarSolicitacaoRecorrencia(eq("solic-1"), any())).thenReturn(new SolicRecResponse("solic-1", "rec-1", "CRIADA"));

		mockMvc.perform(patch("/api/inter/pix-automatico/solicrec/solic-1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"idRec\":\"rec-1\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.idSolicRec").value("solic-1"));
	}

	@Test
	void createsRecurringCharge() throws Exception {
		when(service.criarCobrancaRecorrente(eq("txid-1"), any())).thenReturn(cobranca());

		mockMvc.perform(put("/api/inter/pix-automatico/cobr/txid-1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(validCobrRequest()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.txid").value("txid-1"));
	}

	@Test
	void returnsRecurringCharge() throws Exception {
		when(service.consultarCobrancaRecorrente("txid-1")).thenReturn(cobranca());

		mockMvc.perform(get("/api/inter/pix-automatico/cobr/txid-1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.txid").value("txid-1"));
	}

	@Test
	void updatesRecurringCharge() throws Exception {
		when(service.alterarCobrancaRecorrente(eq("txid-1"), any())).thenReturn(cobranca());

		mockMvc.perform(patch("/api/inter/pix-automatico/cobr/txid-1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(validCobrRequest()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.txid").value("txid-1"));
	}

	@Test
	void requestsRetry() throws Exception {
		when(service.solicitarRetentativa(eq("txid-1"), eq("2026-07-02"), any())).thenReturn(cobranca());

		mockMvc.perform(post("/api/inter/pix-automatico/cobr/txid-1/retentativa/2026-07-02")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"motivo\":\"falha\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.txid").value("txid-1"));
	}

	@Test
	void createsLocation() throws Exception {
		when(service.criarLocationRecorrencia(any())).thenReturn(new LocRecResponse(1L, "https://pix/location", "rec-1"));

		mockMvc.perform(post("/api/inter/pix-automatico/locrec")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"idRec\":\"rec-1\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	void returnsLocation() throws Exception {
		when(service.consultarLocationRecorrencia("1")).thenReturn(new LocRecResponse(1L, "https://pix/location", "rec-1"));

		mockMvc.perform(get("/api/inter/pix-automatico/locrec/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	void unlinksLocation() throws Exception {
		mockMvc.perform(delete("/api/inter/pix-automatico/locrec/1/idRec"))
			.andExpect(status().isNoContent());
	}

	@Test
	void configuresRecurrenceWebhook() throws Exception {
		when(service.configurarWebhookRecorrencia(any())).thenReturn(new WebhookResponse("https://example.com/rec", "ATIVO"));

		mockMvc.perform(put("/api/inter/pix-automatico/webhookrec")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"webhookUrl\":\"https://example.com/rec\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.webhookUrl").value("https://example.com/rec"));
	}

	@Test
	void configuresRecurringChargeWebhook() throws Exception {
		when(service.configurarWebhookCobrancaRecorrente(any())).thenReturn(new WebhookResponse("https://example.com/cobr", "ATIVO"));

		mockMvc.perform(put("/api/inter/pix-automatico/webhookcobr")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"webhookUrl\":\"https://example.com/cobr\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.webhookUrl").value("https://example.com/cobr"));
	}

	@Test
	void rejectsInvalidSolicRecRequest() throws Exception {
		mockMvc.perform(post("/api/inter/pix-automatico/solicrec")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error").value("Validation failed"));
	}

	private RecorrenciaResponse recorrencia() {
		return new RecorrenciaResponse("rec-1", "CRIADA", null, null);
	}

	private CobrResponse cobranca() {
		return new CobrResponse("txid-1", "rec-1", "CRIADA", BigDecimal.TEN);
	}

	private String validCobrRequest() {
		return "{\"idRec\":\"rec-1\",\"dataDeVencimento\":\"2026-07-30\",\"valor\":10.00}";
	}
}
