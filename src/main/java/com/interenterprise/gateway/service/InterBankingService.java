package com.interenterprise.gateway.service;

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

public interface InterBankingService {

	ExtratoResponse extrato(ExtratoFilter filtros);

	byte[] exportarExtrato(ExtratoFilter filtros);

	ExtratoResponse extratoCompleto(ExtratoFilter filtros);

	SaldoResponse saldo(SaldoFilter filtros);

	PagamentoResponse incluirPagamento(PagamentoRequest request);

	PagamentoResponse incluirDarf(DarfRequest request);

	LoteResponse incluirLote(PagamentoLoteRequest request);

	LoteResponse consultarLote(String idLote);

	PagamentoResponse consultarPagamento(String codigoTransacao);

	PixPagamentoResponse incluirPix(PixPagamentoRequest request);

	PixPagamentoResponse consultarPix(String codigoSolicitacao);

	WebhookRequest configurarWebhook(String tipoWebhook, WebhookRequest request);

	CallbackPage consultarCallbacks(String tipoWebhook, CallbackFilter filtros);

	RetryCallbackResponse reenviarCallback(String tipoWebhook, RetryCallbacksRequest request);
}
