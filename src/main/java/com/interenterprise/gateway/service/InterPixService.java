package com.interenterprise.gateway.service;

import com.interenterprise.gateway.dto.common.CallbackDtos.CallbackPage;
import com.interenterprise.gateway.dto.common.CallbackDtos.CallbackFilter;
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

public interface InterPixService {

	CobResponse criarCobrancaImediata(String txid, CobRequest request);

	CobResponse criarCobrancaImediata(CobRequest request);

	CobResponse consultarCobrancaImediata(String txid);

	PagarCobrancaPixResponse pagarCobrancaImediata(String txid, PagarCobrancaPixRequest request);

	CobResponse criarCobrancaVencimento(String txid, CobRequest request);

	CobResponse criarCobrancaVencimento(CobRequest request);

	CobResponse consultarCobrancaVencimento(String txid);

	PagarCobrancaPixResponse pagarCobrancaVencimento(String txid, PagarCobrancaPixRequest request);

	PixRecebido consultarPix(String e2eId);

	PixListResponse listarPix(PixFilter filtros);

	DevolucaoResponse solicitarDevolucao(String e2eId, String id, DevolucaoRequest request);

	WebhookRequest configurarWebhook(String chave, WebhookRequest request);

	CallbackPage consultarCallbacks(CallbackFilter filtros);

	RetryCallbackResponse reenviarCallback(RetryCallbacksRequest request);
}
