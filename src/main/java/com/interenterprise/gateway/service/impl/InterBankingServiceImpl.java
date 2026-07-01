package com.interenterprise.gateway.service.impl;

import static com.interenterprise.gateway.utils.InterApiPaths.BANKING;
import static com.interenterprise.gateway.utils.InterApiPaths.path;
import static com.interenterprise.gateway.utils.InterApiPaths.segment;

import org.springframework.stereotype.Service;

import com.interenterprise.gateway.dto.banking.BankingDtos.DarfRequest;
import com.interenterprise.gateway.dto.banking.BankingDtos.ExtratoFilter;
import com.interenterprise.gateway.dto.banking.BankingDtos.ExtratoResponse;
import com.interenterprise.gateway.dto.banking.BankingDtos.LoteResponse;
import com.interenterprise.gateway.dto.banking.BankingDtos.PagamentoLoteRequest;
import com.interenterprise.gateway.dto.banking.BankingDtos.PagamentoRequest;
import com.interenterprise.gateway.dto.banking.BankingDtos.PagamentoResponse;
import com.interenterprise.gateway.dto.banking.BankingDtos.PixPagamentoRequest;
import com.interenterprise.gateway.dto.banking.BankingDtos.PixPagamentoResponse;
import com.interenterprise.gateway.dto.banking.BankingDtos.RetryCallbacksRequest;
import com.interenterprise.gateway.dto.banking.BankingDtos.SaldoFilter;
import com.interenterprise.gateway.dto.banking.BankingDtos.SaldoResponse;
import com.interenterprise.gateway.dto.common.CallbackDtos.CallbackFilter;
import com.interenterprise.gateway.dto.common.CallbackDtos.CallbackPage;
import com.interenterprise.gateway.dto.common.RetryCallbackResponse;
import com.interenterprise.gateway.dto.common.WebhookRequest;
import com.interenterprise.gateway.gateway.InterGateway;
import com.interenterprise.gateway.service.InterBankingService;

@Service
public class InterBankingServiceImpl implements InterBankingService {

	private final InterGateway gateway;

	public InterBankingServiceImpl(InterGateway gateway) {
		this.gateway = gateway;
	}

	@Override
	public ExtratoResponse extrato(ExtratoFilter filtros) {
		return gateway.get(path(BANKING, "/extrato"), filtros, ExtratoResponse.class);
	}

	@Override
	public byte[] exportarExtrato(ExtratoFilter filtros) {
		return gateway.get(path(BANKING, "/extrato/exportar"), filtros, byte[].class);
	}

	@Override
	public ExtratoResponse extratoCompleto(ExtratoFilter filtros) {
		return gateway.get(path(BANKING, "/extrato/completo"), filtros, ExtratoResponse.class);
	}

	@Override
	public SaldoResponse saldo(SaldoFilter filtros) {
		return gateway.get(path(BANKING, "/saldo"), filtros, SaldoResponse.class);
	}

	@Override
	public PagamentoResponse incluirPagamento(PagamentoRequest request) {
		return gateway.post(path(BANKING, "/pagamento"), request, PagamentoResponse.class);
	}

	@Override
	public PagamentoResponse incluirDarf(DarfRequest request) {
		return gateway.post(path(BANKING, "/pagamento/darf"), request, PagamentoResponse.class);
	}

	@Override
	public LoteResponse incluirLote(PagamentoLoteRequest request) {
		return gateway.post(path(BANKING, "/pagamento/lote"), request, LoteResponse.class);
	}

	@Override
	public LoteResponse consultarLote(String idLote) {
		return gateway.get(path(BANKING, "/pagamento/lote/" + segment(idLote)), LoteResponse.class);
	}

	@Override
	public PagamentoResponse consultarPagamento(String codigoTransacao) {
		return gateway.get(path(BANKING, "/pagamento/" + segment(codigoTransacao)), PagamentoResponse.class);
	}

	@Override
	public PixPagamentoResponse incluirPix(PixPagamentoRequest request) {
		return gateway.post(path(BANKING, "/pix"), request, PixPagamentoResponse.class);
	}

	@Override
	public PixPagamentoResponse consultarPix(String codigoSolicitacao) {
		return gateway.get(path(BANKING, "/pix/" + segment(codigoSolicitacao)), PixPagamentoResponse.class);
	}

	@Override
	public WebhookRequest configurarWebhook(String tipoWebhook, WebhookRequest request) {
		return gateway.put(path(BANKING, "/webhooks/" + segment(tipoWebhook)), request, WebhookRequest.class);
	}

	@Override
	public CallbackPage consultarCallbacks(String tipoWebhook, CallbackFilter filtros) {
		return gateway.get(path(BANKING, "/webhooks/" + segment(tipoWebhook) + "/callbacks"), filtros, CallbackPage.class);
	}

	@Override
	public RetryCallbackResponse reenviarCallback(String tipoWebhook, RetryCallbacksRequest request) {
		return gateway.post(path(BANKING, "/webhooks/" + segment(tipoWebhook) + "/callbacks/retry"), request, RetryCallbackResponse.class);
	}
}
