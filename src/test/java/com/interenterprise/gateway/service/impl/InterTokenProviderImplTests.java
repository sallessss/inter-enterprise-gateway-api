package com.interenterprise.gateway.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.interenterprise.gateway.configuration.InterProperties;

class InterTokenProviderImplTests {

	@Test
	void reusesCachedTokenUntilExpiration() {
		RestClient.Builder builder = RestClient.builder().baseUrl("https://inter.test");
		MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
		InterTokenProviderImpl provider = new InterTokenProviderImpl(builder.build(), properties());

		server.expect(once(), requestTo("https://inter.test/oauth/v2/token"))
			.andExpect(method(HttpMethod.POST))
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
			.andRespond(withSuccess(tokenResponse("token-1", 300), MediaType.APPLICATION_JSON));

		assertEquals("token-1", provider.getToken());
		assertEquals("token-1", provider.getToken());
		server.verify();
	}

	@Test
	void requestsNewTokenAfterInvalidation() {
		RestClient.Builder builder = RestClient.builder().baseUrl("https://inter.test");
		MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
		InterTokenProviderImpl provider = new InterTokenProviderImpl(builder.build(), properties());

		server.expect(once(), requestTo("https://inter.test/oauth/v2/token"))
			.andRespond(withSuccess(tokenResponse("token-1", 300), MediaType.APPLICATION_JSON));
		server.expect(once(), requestTo("https://inter.test/oauth/v2/token"))
			.andRespond(withSuccess(tokenResponse("token-2", 300), MediaType.APPLICATION_JSON));

		assertEquals("token-1", provider.getToken());
		provider.invalidate();
		assertEquals("token-2", provider.getToken());
		server.verify();
	}

	@Test
	void removesOuterQuotesFromScopeBeforeRequestingToken() {
		RestClient.Builder builder = RestClient.builder().baseUrl("https://inter.test");
		MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
		InterProperties properties = properties();
		properties.setScope("\"extrato.read pix.read\"");
		InterTokenProviderImpl provider = new InterTokenProviderImpl(builder.build(), properties);

		server.expect(once(), requestTo("https://inter.test/oauth/v2/token"))
			.andExpect(content().string(containsString("scope=extrato.read+pix.read")))
			.andRespond(withSuccess(tokenResponse("token-1", 300), MediaType.APPLICATION_JSON));

		assertEquals("token-1", provider.getToken());
		server.verify();
	}

	private InterProperties properties() {
		InterProperties properties = new InterProperties();
		properties.setClientId("client-id");
		properties.setClientSecret("client-secret");
		properties.setTokenPath("/oauth/v2/token");
		properties.setScope("extrato.read");
		return properties;
	}

	private String tokenResponse(String token, int expiresIn) {
		return """
			{
				"access_token": "%s",
				"token_type": "Bearer",
				"expires_in": %d,
				"scope": "extrato.read"
			}
			""".formatted(token, expiresIn);
	}
}
