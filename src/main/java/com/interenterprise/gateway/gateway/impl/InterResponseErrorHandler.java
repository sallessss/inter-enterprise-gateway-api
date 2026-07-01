package com.interenterprise.gateway.gateway.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;

import com.interenterprise.gateway.exception.InterApiException;

final class InterResponseErrorHandler {

	private InterResponseErrorHandler() {
	}

	static void handle(org.springframework.http.HttpRequest request, ClientHttpResponse response) throws IOException {
		int statusCode = response.getStatusCode().value();
		String body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
		throw new InterApiException("Inter API returned HTTP " + statusCode + sanitizeBody(body), statusCode);
	}

	private static String sanitizeBody(String body) {
		if (body == null || body.isBlank()) {
			return "";
		}
		return ": " + body.replaceAll("(?i)(access_token|client_secret)\"?\\s*[:=]\\s*\"?[^\"]+", "$1=***");
	}
}
