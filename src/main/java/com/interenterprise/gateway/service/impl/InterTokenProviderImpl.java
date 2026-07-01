package com.interenterprise.gateway.service.impl;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.interenterprise.gateway.configuration.InterProperties;
import com.interenterprise.gateway.dto.InterTokenResponse;
import com.interenterprise.gateway.exception.InterAuthenticationException;
import com.interenterprise.gateway.service.InterTokenProvider;

@Service
public class InterTokenProviderImpl implements InterTokenProvider {

	private static final long EXPIRATION_SAFETY_SECONDS = 60;

	private final RestClient restClient;
	private final InterProperties properties;
	private String cachedToken;
	private Instant expiresAt = Instant.EPOCH;

	public InterTokenProviderImpl(@Qualifier("interRestClient") RestClient restClient, InterProperties properties) {
		this.restClient = restClient;
		this.properties = properties;
	}

	@Override
	public synchronized String getToken() {
		if (cachedToken != null && Instant.now().isBefore(expiresAt)) {
			return cachedToken;
		}

		InterTokenResponse response = requestToken();
		if (response == null || response.accessToken() == null || response.accessToken().isBlank()) {
			throw new InterAuthenticationException("Inter token response did not include an access token");
		}

		cachedToken = response.accessToken();
		expiresAt = Instant.now().plusSeconds(Math.max(0, response.expiresIn() - EXPIRATION_SAFETY_SECONDS));
		return cachedToken;
	}

	@Override
	public synchronized void invalidate() {
		cachedToken = null;
		expiresAt = Instant.EPOCH;
	}

	private InterTokenResponse requestToken() {
		validateCredentials();

		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("grant_type", "client_credentials");
		form.add("client_id", properties.getClientId());
		form.add("client_secret", properties.getClientSecret());
		String scope = normalizeScope(properties.getScope());
		if (hasText(scope)) {
			form.add("scope", scope);
		}

		try {
			return restClient.post()
				.uri(properties.getTokenPath())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(form)
				.retrieve()
				.onStatus(status -> status.isError(), (request, response) -> {
					throw new InterAuthenticationException("Inter token request failed with HTTP " + response.getStatusCode().value());
				})
				.body(InterTokenResponse.class);
		} catch (InterAuthenticationException ex) {
			throw ex;
		} catch (RestClientException ex) {
			throw new InterAuthenticationException("Failed to request Inter access token", ex);
		}
	}

	private void validateCredentials() {
		if (!hasText(properties.getClientId()) || !hasText(properties.getClientSecret())) {
			throw new InterAuthenticationException("Inter client id and client secret must be configured");
		}
	}

	private boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

	private String normalizeScope(String scope) {
		if (scope == null) {
			return null;
		}

		String normalizedScope = scope.trim();
		if (normalizedScope.length() >= 2
			&& ((normalizedScope.startsWith("\"") && normalizedScope.endsWith("\""))
				|| (normalizedScope.startsWith("'") && normalizedScope.endsWith("'")))) {
			return normalizedScope.substring(1, normalizedScope.length() - 1).trim();
		}
		return normalizedScope;
	}
}
