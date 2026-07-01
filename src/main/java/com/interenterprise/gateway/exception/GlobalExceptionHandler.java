package com.interenterprise.gateway.exception;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.interenterprise.gateway.dto.common.ApiErrorResponse;
import com.interenterprise.gateway.dto.common.FieldErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(InterApiException.class)
	public ResponseEntity<ApiErrorResponse> handleInterApiException(InterApiException ex, HttpServletRequest request) {
		HttpStatus status = HttpStatus.resolve(ex.getStatusCode());
		if (status == null) {
			status = HttpStatus.BAD_GATEWAY;
		}

		LOGGER.warn("Inter API error status={} path={}", ex.getStatusCode(), request.getRequestURI());
		return ResponseEntity.status(status)
			.body(ApiErrorResponse.of(status.value(), status.getReasonPhrase(), sanitize(ex.getMessage()), request.getRequestURI()));
	}

	@ExceptionHandler(InterAuthenticationException.class)
	public ResponseEntity<ApiErrorResponse> handleInterAuthenticationException(InterAuthenticationException ex, HttpServletRequest request) {
		LOGGER.warn("Inter authentication error path={}", request.getRequestURI());
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
			.body(ApiErrorResponse.of(HttpStatus.BAD_GATEWAY.value(), "Inter authentication failed", sanitize(ex.getMessage()), request.getRequestURI()));
	}

	@ExceptionHandler(InterIntegrationException.class)
	public ResponseEntity<ApiErrorResponse> handleInterIntegrationException(InterIntegrationException ex, HttpServletRequest request) {
		LOGGER.warn("Inter integration error path={} message={}", request.getRequestURI(), sanitize(ex.getMessage()));
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
			.body(ApiErrorResponse.of(HttpStatus.BAD_GATEWAY.value(), "Inter integration failed", sanitize(ex.getMessage()), request.getRequestURI()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
		List<FieldErrorResponse> fields = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(this::toFieldError)
			.toList();

		return ResponseEntity.badRequest()
			.body(ApiErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Validation failed", "Invalid request body", request.getRequestURI(), fields));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
		List<FieldErrorResponse> fields = ex.getConstraintViolations()
			.stream()
			.map(violation -> new FieldErrorResponse(violation.getPropertyPath().toString(), violation.getMessage()))
			.toList();

		return ResponseEntity.badRequest()
			.body(ApiErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Validation failed", "Invalid request parameters", request.getRequestURI(), fields));
	}

	@ExceptionHandler({
		HttpMessageNotReadableException.class,
		MethodArgumentTypeMismatchException.class,
		MissingServletRequestParameterException.class
	})
	public ResponseEntity<ApiErrorResponse> handleBadRequest(Exception ex, HttpServletRequest request) {
		return ResponseEntity.badRequest()
			.body(ApiErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Invalid request format", request.getRequestURI()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
		LOGGER.error("Unexpected error path={}", request.getRequestURI(), ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ApiErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "Unexpected server error", request.getRequestURI()));
	}

	private FieldErrorResponse toFieldError(FieldError error) {
		return new FieldErrorResponse(error.getField(), error.getDefaultMessage());
	}

	private String sanitize(String value) {
		if (value == null || value.isBlank()) {
			return "Integration request failed";
		}

		return value
			.replaceAll("(?i)(access_token|client_secret|clientSecret|authorization|cert|certificate|password)=?\\s*[^,\\s]+", "$1=***")
			.replaceAll("(?i)(Bearer)\\s+[A-Za-z0-9._\\-]+", "$1 ***");
	}
}
