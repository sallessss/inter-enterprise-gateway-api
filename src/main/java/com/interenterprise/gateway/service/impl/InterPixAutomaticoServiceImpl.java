package com.interenterprise.gateway.service.impl;

import static com.interenterprise.gateway.utils.InterApiPaths.PIX;
import static com.interenterprise.gateway.utils.InterApiPaths.path;
import static com.interenterprise.gateway.utils.InterApiPaths.segment;

import org.springframework.stereotype.Service;

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
import com.interenterprise.gateway.gateway.InterGateway;
import com.interenterprise.gateway.service.InterPixAutomaticoService;

@Service
public class InterPixAutomaticoServiceImpl implements InterPixAutomaticoService {

	private final InterGateway gateway;

	public InterPixAutomaticoServiceImpl(InterGateway gateway) {
		this.gateway = gateway;
	}

	@Override
	public RecorrenciaResponse criarRecorrencia(RecorrenciaRequest request) {
		return gateway.post(path(PIX, "/rec"), request, RecorrenciaResponse.class);
	}

	@Override
	public RecorrenciaResponse consultarRecorrencia(String idRec) {
		return gateway.get(path(PIX, "/rec/" + segment(idRec)), RecorrenciaResponse.class);
	}

	@Override
	public RecorrenciaResponse alterarRecorrencia(String idRec, RecorrenciaRequest request) {
		return gateway.patch(path(PIX, "/rec/" + segment(idRec)), request, RecorrenciaResponse.class);
	}

	@Override
	public SolicRecResponse criarSolicitacaoRecorrencia(SolicRecRequest request) {
		return gateway.post(path(PIX, "/solicrec"), request, SolicRecResponse.class);
	}

	@Override
	public SolicRecResponse consultarSolicitacaoRecorrencia(String idSolicRec) {
		return gateway.get(path(PIX, "/solicrec/" + segment(idSolicRec)), SolicRecResponse.class);
	}

	@Override
	public SolicRecResponse alterarSolicitacaoRecorrencia(String idSolicRec, SolicRecRequest request) {
		return gateway.patch(path(PIX, "/solicrec/" + segment(idSolicRec)), request, SolicRecResponse.class);
	}

	@Override
	public CobrResponse criarCobrancaRecorrente(String txid, CobrRequest request) {
		return gateway.put(path(PIX, "/cobr/" + segment(txid)), request, CobrResponse.class);
	}

	@Override
	public CobrResponse consultarCobrancaRecorrente(String txid) {
		return gateway.get(path(PIX, "/cobr/" + segment(txid)), CobrResponse.class);
	}

	@Override
	public CobrResponse alterarCobrancaRecorrente(String txid, CobrRequest request) {
		return gateway.patch(path(PIX, "/cobr/" + segment(txid)), request, CobrResponse.class);
	}

	@Override
	public CobrResponse solicitarRetentativa(String txid, String data, RetentativaRequest request) {
		return gateway.post(path(PIX, "/cobr/" + segment(txid) + "/retentativa/" + segment(data)), request, CobrResponse.class);
	}

	@Override
	public LocRecResponse criarLocationRecorrencia(LocRecRequest request) {
		return gateway.post(path(PIX, "/locrec"), request, LocRecResponse.class);
	}

	@Override
	public LocRecResponse consultarLocationRecorrencia(String id) {
		return gateway.get(path(PIX, "/locrec/" + segment(id)), LocRecResponse.class);
	}

	@Override
	public void desvincularLocationRecorrencia(String id) {
		gateway.delete(path(PIX, "/locrec/" + segment(id) + "/idRec"));
	}

	@Override
	public WebhookResponse configurarWebhookRecorrencia(WebhookRequest request) {
		return gateway.put(path(PIX, "/webhookrec"), request, WebhookResponse.class);
	}

	@Override
	public WebhookResponse configurarWebhookCobrancaRecorrente(WebhookRequest request) {
		return gateway.put(path(PIX, "/webhookcobr"), request, WebhookResponse.class);
	}
}
