package com.interenterprise.gateway.service.impl;

import static com.interenterprise.gateway.utils.InterApiPaths.COBRANCA;
import static com.interenterprise.gateway.utils.InterApiPaths.path;
import static com.interenterprise.gateway.utils.InterApiPaths.segment;

import org.springframework.stereotype.Service;

import com.interenterprise.gateway.dto.common.CallbackDtos.CallbackFilter;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.CancelarCobrancaRequest;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.CobrancaDetalhadaResponse;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.CobrancaFilter;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.CobrancasResponse;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.EditarCobrancaRequest;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.EditarCobrancaResponse;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.EmitirCobrancaRequest;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.EmitirCobrancaResponse;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.PagarCobrancaRequest;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.RetryCallbacksRequest;
import com.interenterprise.gateway.dto.cobranca.CobrancaDtos.SumarioCobrancasResponse;
import com.interenterprise.gateway.dto.common.CallbackDtos.CallbackPage;
import com.interenterprise.gateway.dto.common.RetryCallbackResponse;
import com.interenterprise.gateway.dto.common.WebhookRequest;
import com.interenterprise.gateway.gateway.InterGateway;
import com.interenterprise.gateway.service.InterCobrancaService;

@Service
public class InterCobrancaServiceImpl implements InterCobrancaService {

	private final InterGateway gateway;

	public InterCobrancaServiceImpl(InterGateway gateway) {
		this.gateway = gateway;
	}

	@Override
	public CobrancasResponse listar(CobrancaFilter filtros) {
		return gateway.get(path(COBRANCA, "/cobrancas"), filtros, CobrancasResponse.class);
	}

	@Override
	public EmitirCobrancaResponse emitir(EmitirCobrancaRequest request) {
		return gateway.post(path(COBRANCA, "/cobrancas"), request, EmitirCobrancaResponse.class);
	}

	@Override
	public CobrancaDetalhadaResponse consultar(String codigoSolicitacao) {
		return gateway.get(path(COBRANCA, "/cobrancas/" + segment(codigoSolicitacao)), CobrancaDetalhadaResponse.class);
	}

	@Override
	public EditarCobrancaResponse editar(String codigoEdicao, EditarCobrancaRequest request) {
		return gateway.patch(path(COBRANCA, "/cobrancas/edicao/" + segment(codigoEdicao)), request, EditarCobrancaResponse.class);
	}

	@Override
	public byte[] pdf(String codigoSolicitacao) {
		return gateway.get(path(COBRANCA, "/cobrancas/" + segment(codigoSolicitacao) + "/pdf"), byte[].class);
	}

	@Override
	public CobrancaDetalhadaResponse cancelar(String codigoSolicitacao, CancelarCobrancaRequest request) {
		return gateway.post(path(COBRANCA, "/cobrancas/" + segment(codigoSolicitacao) + "/cancelar"), request, CobrancaDetalhadaResponse.class);
	}

	@Override
	public SumarioCobrancasResponse sumario(CobrancaFilter filtros) {
		return gateway.get(path(COBRANCA, "/cobrancas/sumario"), filtros, SumarioCobrancasResponse.class);
	}

	@Override
	public CobrancaDetalhadaResponse pagar(String codigoSolicitacao, PagarCobrancaRequest request) {
		return gateway.post(path(COBRANCA, "/cobrancas/" + segment(codigoSolicitacao) + "/pagar"), request, CobrancaDetalhadaResponse.class);
	}

	@Override
	public WebhookRequest configurarWebhook(WebhookRequest request) {
		return gateway.put(path(COBRANCA, "/cobrancas/webhook"), request, WebhookRequest.class);
	}

	@Override
	public CallbackPage consultarCallbacks(CallbackFilter filtros) {
		return gateway.get(path(COBRANCA, "/cobrancas/webhook/callbacks"), filtros, CallbackPage.class);
	}

	@Override
	public RetryCallbackResponse reenviarCallback(RetryCallbacksRequest request) {
		return gateway.post(path(COBRANCA, "/webhook/callbacks/retry"), request, RetryCallbackResponse.class);
	}
}
