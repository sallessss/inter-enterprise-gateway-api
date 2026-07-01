package com.interenterprise.gateway.dto.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record WebhookRequest(
	@NotBlank
	@Pattern(regexp = "^https://[^\\s]*$")
	String webhookUrl
) {
}
