package com.interenterprise.gateway.dto.common;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class InterWebhookDtos {

	private InterWebhookDtos() {
	}

	public record WebhookNotificationRequest(
		@NotBlank
		String product,
		String subtype,
		@NotNull
		Object payload
	) {
	}

	public record WebhookReceivedResponse(
		String product,
		String subtype,
		OffsetDateTime receivedAt
	) {
	}
}
