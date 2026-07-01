package com.interenterprise.gateway.service.impl;

import static com.interenterprise.gateway.utils.InterApiPaths.PIX;
import static com.interenterprise.gateway.utils.InterApiPaths.path;
import static com.interenterprise.gateway.utils.InterApiPaths.segment;

import org.springframework.stereotype.Service;

import com.interenterprise.gateway.dto.common.CallbackDtos.CallbackFilter;
import com.interenterprise.gateway.dto.common.CallbackDtos.CallbackPage;
import com.interenterprise.gateway.dto.common.RetryCallbackResponse;
import com.interenterprise.gateway.dto.common.WebhookRequest;
import com.interenterprise.gateway.dto.pix.PixDtos.CobRequest;
import com.interenterprise.gateway.dto.pix.PixDtos.CobResponse;
import com.interenterprise.gateway.dto.pix.PixDtos.DevolucaoRequest;
import com.interenterprise.gateway.dto.pix.PixDtos.DevolucaoResponse;
import com.interenterprise.gateway.dto.pix.PixDtos.PagarCobrancaPixRequest;
import com.interenterprise.gateway.dto.pix.PixDtos.PagarCobrancaPixResponse;
import com.interenterprise.gateway.dto.pix.PixDtos.PixListResponse;
import com.interenterprise.gateway.dto.pix.PixDtos.PixRecebido;
import com.interenterprise.gateway.dto.pix.PixDtos.PixFilter;
import com.interenterprise.gateway.dto.pix.PixDtos.RetryCallbacksRequest;
import com.interenterprise.gateway.gateway.InterGateway;
import com.interenterprise.gateway.service.InterPixService;

@Service
public class InterPixServiceImpl implements InterPixService {

	private final InterGateway gateway;

	public InterPixServiceImpl(InterGateway gateway) {
		this.gateway = gateway;
	}

	@Override
	public CobResponse criarCobrancaImediata(String txid, CobRequest request) {
		return gateway.put(path(PIX, "/cob/" + segment(txid)), request, CobResponse.class);
	}

	@Override
	public CobResponse criarCobrancaImediata(CobRequest request) {
		return gateway.post(path(PIX, "/cob"), request, CobResponse.class);
	}

	@Override
	public CobResponse consultarCobrancaImediata(String txid) {
		return gateway.get(path(PIX, "/cob/" + segment(txid)), CobResponse.class);
	}

	@Override
	public PagarCobrancaPixResponse pagarCobrancaImediata(String txid, PagarCobrancaPixRequest request) {
		return gateway.post(path(PIX, "/cob/pagar/" + segment(txid)), request, PagarCobrancaPixResponse.class);
	}

	@Override
	public CobResponse criarCobrancaVencimento(String txid, CobRequest request) {
		return gateway.put(path(PIX, "/cobv/" + segment(txid)), request, CobResponse.class);
	}

	@Override
	public CobResponse criarCobrancaVencimento(CobRequest request) {
		return gateway.post(path(PIX, "/cobv"), request, CobResponse.class);
	}

	@Override
	public CobResponse consultarCobrancaVencimento(String txid) {
		return gateway.get(path(PIX, "/cobv/" + segment(txid)), CobResponse.class);
	}

	@Override
	public PagarCobrancaPixResponse pagarCobrancaVencimento(String txid, PagarCobrancaPixRequest request) {
		return gateway.post(path(PIX, "/cobv/pagar/" + segment(txid)), request, PagarCobrancaPixResponse.class);
	}

	@Override
	public PixRecebido consultarPix(String e2eId) {
		return gateway.get(path(PIX, "/pix/" + segment(e2eId)), PixRecebido.class);
	}

	@Override
	public PixListResponse listarPix(PixFilter filtros) {
		return gateway.get(path(PIX, "/pix"), filtros, PixListResponse.class);
	}

	@Override
	public DevolucaoResponse solicitarDevolucao(String e2eId, String id, DevolucaoRequest request) {
		return gateway.put(path(PIX, "/pix/" + segment(e2eId) + "/devolucao/" + segment(id)), request, DevolucaoResponse.class);
	}

	@Override
	public WebhookRequest configurarWebhook(String chave, WebhookRequest request) {
		return gateway.put(path(PIX, "/webhook/" + segment(chave)), request, WebhookRequest.class);
	}

	@Override
	public CallbackPage consultarCallbacks(CallbackFilter filtros) {
		return gateway.get(path(PIX, "/webhook/callbacks"), filtros, CallbackPage.class);
	}

	@Override
	public RetryCallbackResponse reenviarCallback(RetryCallbacksRequest request) {
		return gateway.post(path(PIX, "/webhook/callbacks/retry"), request, RetryCallbackResponse.class);
	}
}
