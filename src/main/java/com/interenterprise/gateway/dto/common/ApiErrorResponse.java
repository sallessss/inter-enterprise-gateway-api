package com.interenterprise.gateway.dto.common;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiErrorResponse(
	OffsetDateTime timestamp,
	int status,
	String error,
	String message,
	String path,
	List<FieldErrorResponse> fields
) {
	public static ApiErrorResponse of(int status, String error, String message, String path) {
		return new ApiErrorResponse(OffsetDateTime.now(), status, error, message, path, List.of());
	}

	public static ApiErrorResponse of(int status, String error, String message, String path, List<FieldErrorResponse> fields) {
		return new ApiErrorResponse(OffsetDateTime.now(), status, error, message, path, fields);
	}
}
