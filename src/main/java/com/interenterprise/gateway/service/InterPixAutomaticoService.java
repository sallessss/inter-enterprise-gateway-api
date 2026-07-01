package com.interenterprise.gateway.service;

import java.util.Map;

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

public interface InterPixAutomaticoService {

	RecorrenciaResponse criarRecorrencia(RecorrenciaRequest request);

	RecorrenciaResponse consultarRecorrencia(String idRec);

	RecorrenciaResponse alterarRecorrencia(String idRec, RecorrenciaRequest request);

	SolicRecResponse criarSolicitacaoRecorrencia(SolicRecRequest request);

	SolicRecResponse consultarSolicitacaoRecorrencia(String idSolicRec);

	SolicRecResponse alterarSolicitacaoRecorrencia(String idSolicRec, SolicRecRequest request);

	CobrResponse criarCobrancaRecorrente(String txid, CobrRequest request);

	CobrResponse consultarCobrancaRecorrente(String txid);

	CobrResponse alterarCobrancaRecorrente(String txid, CobrRequest request);

	CobrResponse solicitarRetentativa(String txid, String data, RetentativaRequest request);

	LocRecResponse criarLocationRecorrencia(LocRecRequest request);

	LocRecResponse consultarLocationRecorrencia(String id);

	void desvincularLocationRecorrencia(String id);

	WebhookResponse configurarWebhookRecorrencia(WebhookRequest request);

	WebhookResponse configurarWebhookCobrancaRecorrente(WebhookRequest request);
}
