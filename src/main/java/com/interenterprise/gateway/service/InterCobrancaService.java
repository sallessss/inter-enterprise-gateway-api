package com.interenterprise.gateway.service;

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

public interface InterCobrancaService {

	CobrancasResponse listar(CobrancaFilter filtros);

	EmitirCobrancaResponse emitir(EmitirCobrancaRequest request);

	CobrancaDetalhadaResponse consultar(String codigoSolicitacao);

	EditarCobrancaResponse editar(String codigoEdicao, EditarCobrancaRequest request);

	byte[] pdf(String codigoSolicitacao);

	CobrancaDetalhadaResponse cancelar(String codigoSolicitacao, CancelarCobrancaRequest request);

	SumarioCobrancasResponse sumario(CobrancaFilter filtros);

	CobrancaDetalhadaResponse pagar(String codigoSolicitacao, PagarCobrancaRequest request);

	WebhookRequest configurarWebhook(WebhookRequest request);

	CallbackPage consultarCallbacks(CallbackFilter filtros);

	RetryCallbackResponse reenviarCallback(RetryCallbacksRequest request);
}
