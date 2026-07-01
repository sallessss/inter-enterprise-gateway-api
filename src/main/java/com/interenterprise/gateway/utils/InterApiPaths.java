package com.interenterprise.gateway.utils;

import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

public final class InterApiPaths {

	public static final String COBRANCA = "/cobranca/v3";
	public static final String BANKING = "/banking/v2";
	public static final String PIX = "/pix/v2";

	private InterApiPaths() {
	}

	public static String path(String prefix, String path) {
		return prefix + path;
	}

	public static String segment(String value) {
		return UriUtils.encodePathSegment(value, StandardCharsets.UTF_8);
	}
}
