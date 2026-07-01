package com.interenterprise.gateway.configuration;

import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import javax.net.ssl.SSLContext;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import com.interenterprise.gateway.exception.InterIntegrationException;

@Configuration
@EnableConfigurationProperties(InterProperties.class)
public class InterConfiguration {

	@Bean
	@Qualifier("interRestClient")
	RestClient interRestClient(InterProperties properties, InterSslContextFactory sslContextFactory) {
		RestClient.Builder builder = RestClient.builder().baseUrl(properties.getBaseUrl());

		if (hasClientCertificate(properties)) {
			SSLContext sslContext = sslContextFactory.create();
			HttpClient httpClient = HttpClient.newBuilder()
				.sslContext(sslContext)
				.connectTimeout(Duration.ofSeconds(properties.getConnectTimeoutSeconds()))
				.build();
			builder.requestFactory(new JdkClientHttpRequestFactory(httpClient));
		} else if (properties.isCertificateRequired()) {
			throw new InterIntegrationException("Inter client certificate file must be configured and available");
		}

		return builder.build();
	}

	private boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

	private boolean hasClientCertificate(InterProperties properties) {
		return hasText(properties.getCertificatePath())
			&& hasText(properties.getPrivateKeyPath())
			&& Files.exists(Path.of(properties.getCertificatePath()))
			&& Files.exists(Path.of(properties.getPrivateKeyPath()));
	}
}
