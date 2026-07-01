package com.interenterprise.gateway.gateway.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.interenterprise.gateway.dto.common.QueryParams;
import com.interenterprise.gateway.exception.InterApiException;
import com.interenterprise.gateway.exception.InterIntegrationException;
import com.interenterprise.gateway.gateway.InterGateway;
import com.interenterprise.gateway.service.InterTokenProvider;

@Component
public class InterGatewayImpl implements InterGateway {

	private static final Logger LOGGER = LoggerFactory.getLogger(InterGatewayImpl.class);

	private final RestClient restClient;
	private final InterTokenProvider tokenProvider;

	public InterGatewayImpl(@Qualifier("interRestClient") RestClient restClient, InterTokenProvider tokenProvider) {
		this.restClient = restClient;
		this.tokenProvider = tokenProvider;
	}

	@Override
	public <T> T get(String path, Class<T> responseType) {
		return executeWithRetry(path, () -> restClient.get()
			.uri(path)
			.headers(headers -> headers.setBearerAuth(tokenProvider.getToken()))
			.retrieve()
			.onStatus(status -> status.isError(), InterResponseErrorHandler::handle)
			.body(responseType));
	}

	@Override
	public <T> T get(String path, QueryParams queryParams, Class<T> responseType) {
		String uri = buildUri(path, queryParams.toQueryParams());
		return executeWithRetry(path, () -> restClient.get()
			.uri(uri)
			.headers(headers -> headers.setBearerAuth(tokenProvider.getToken()))
			.retrieve()
			.onStatus(status -> status.isError(), InterResponseErrorHandler::handle)
			.body(responseType));
	}

	@Override
	public <T> T post(String path, Object request, Class<T> responseType) {
		return executeWithRetry(path, () -> restClient.post()
			.uri(path)
			.headers(headers -> headers.setBearerAuth(tokenProvider.getToken()))
			.body(request)
			.retrieve()
			.onStatus(status -> status.isError(), InterResponseErrorHandler::handle)
			.body(responseType));
	}

	@Override
	public <T> T put(String path, Object request, Class<T> responseType) {
		return executeWithRetry(path, () -> restClient.put()
			.uri(path)
			.headers(headers -> headers.setBearerAuth(tokenProvider.getToken()))
			.body(request)
			.retrieve()
			.onStatus(status -> status.isError(), InterResponseErrorHandler::handle)
			.body(responseType));
	}

	@Override
	public <T> T patch(String path, Object request, Class<T> responseType) {
		return executeWithRetry(path, () -> restClient.method(HttpMethod.PATCH)
			.uri(path)
			.headers(headers -> headers.setBearerAuth(tokenProvider.getToken()))
			.body(request)
			.retrieve()
			.onStatus(status -> status.isError(), InterResponseErrorHandler::handle)
			.body(responseType));
	}

	@Override
	public void delete(String path) {
		executeWithRetry(path, () -> {
			restClient.delete()
				.uri(path)
				.headers(headers -> headers.setBearerAuth(tokenProvider.getToken()))
				.retrieve()
				.onStatus(status -> status.isError(), InterResponseErrorHandler::handle)
				.toBodilessEntity();
			return null;
		});
	}

	private <T> T executeWithRetry(String path, Supplier<T> call) {
		Instant startedAt = Instant.now();
		try {
			T response = call.get();
			logRequest(path, startedAt, 200);
			return response;
		} catch (InterApiException ex) {
			if (ex.getStatusCode() == 401) {
				tokenProvider.invalidate();
				T response = call.get();
				logRequest(path, startedAt, 200);
				return response;
			}
			logRequest(path, startedAt, ex.getStatusCode());
			throw ex;
		} catch (RestClientException ex) {
			throw new InterIntegrationException("Failed to call Inter API", ex);
		}
	}

	private void logRequest(String path, Instant startedAt, int statusCode) {
		long elapsedMillis = Duration.between(startedAt, Instant.now()).toMillis();
		LOGGER.info("Inter API request path={} status={} elapsedMs={}", path, statusCode, elapsedMillis);
	}

	private String buildUri(String path, Map<String, ?> queryParams) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromPath(path);
		queryParams.forEach((name, value) -> {
			if (value != null) {
				builder.queryParam(name, value);
			}
		});
		return builder.build().toUriString();
	}
}
