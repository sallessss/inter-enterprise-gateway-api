package com.interenterprise.gateway.service.impl;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;

import com.interenterprise.gateway.dto.common.InterWebhookDtos.WebhookNotificationRequest;
import com.interenterprise.gateway.dto.common.InterWebhookDtos.WebhookReceivedResponse;
import com.interenterprise.gateway.service.InterWebhookReceiverService;

@Service
public class InterWebhookReceiverServiceImpl implements InterWebhookReceiverService {

	@Override
	public WebhookReceivedResponse receive(WebhookNotificationRequest request) {
		return new WebhookReceivedResponse(request.product(), request.subtype(), OffsetDateTime.now());
	}
}
