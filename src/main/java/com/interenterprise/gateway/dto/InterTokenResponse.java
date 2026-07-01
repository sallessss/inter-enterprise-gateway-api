package com.interenterprise.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InterTokenResponse(
	@JsonProperty("access_token") String accessToken,
	@JsonProperty("token_type") String tokenType,
	@JsonProperty("expires_in") long expiresIn,
	String scope
) {
}
