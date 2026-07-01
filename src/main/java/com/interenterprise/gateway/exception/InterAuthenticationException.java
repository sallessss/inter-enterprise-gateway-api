package com.interenterprise.gateway.exception;

public class InterAuthenticationException extends RuntimeException {

	public InterAuthenticationException(String message) {
		super(message);
	}

	public InterAuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}
}
