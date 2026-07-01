package com.interenterprise.gateway.gateway;

import com.interenterprise.gateway.dto.common.QueryParams;

public interface InterGateway {

	<T> T get(String path, Class<T> responseType);

	<T> T get(String path, QueryParams queryParams, Class<T> responseType);

	<T> T post(String path, Object request, Class<T> responseType);

	<T> T put(String path, Object request, Class<T> responseType);

	<T> T patch(String path, Object request, Class<T> responseType);

	void delete(String path);
}
