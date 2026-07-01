package com.interenterprise.gateway.service;

import com.interenterprise.gateway.dto.common.InterWebhookDtos.WebhookNotificationRequest;
import com.interenterprise.gateway.dto.common.InterWebhookDtos.WebhookReceivedResponse;

public interface InterWebhookReceiverService {

	WebhookReceivedResponse receive(WebhookNotificationRequest request);
}
