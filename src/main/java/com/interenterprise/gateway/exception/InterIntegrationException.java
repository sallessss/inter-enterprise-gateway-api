package com.interenterprise.gateway.exception;

public class InterIntegrationException extends RuntimeException {

	public InterIntegrationException(String message) {
		super(message);
	}

	public InterIntegrationException(String message, Throwable cause) {
		super(message, cause);
	}
}
