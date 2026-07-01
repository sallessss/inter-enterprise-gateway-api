package com.interenterprise.gateway.service;

public interface InterTokenProvider {

	String getToken();

	void invalidate();
}
