package com.interenterprise.gateway.dto.common;

import java.util.List;

public record RetryCallbackResponse(List<String> foundIds) {
}
