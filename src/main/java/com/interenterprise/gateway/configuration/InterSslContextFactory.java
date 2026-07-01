package com.interenterprise.gateway.configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.stereotype.Component;

import com.interenterprise.gateway.exception.InterIntegrationException;

@Component
public class InterSslContextFactory {

	private final InterProperties properties;

	public InterSslContextFactory(InterProperties properties) {
		this.properties = properties;
	}

	public SSLContext create() {
		try {
			char[] password = properties.getPassword() == null ? new char[0] : properties.getPassword().toCharArray();
			KeyStore keyStore = loadKeyStore(password);

			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, password);

			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init((KeyStore) null);

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
			return sslContext;
		} catch (Exception ex) {
			throw new InterIntegrationException("Failed to configure Inter client certificate", ex);
		}
	}

	private KeyStore loadKeyStore(char[] password) throws Exception {
		return loadPemKeyStore(password);
	}

	private KeyStore loadPemKeyStore(char[] password) throws Exception {
		Certificate certificate = loadCertificate(Path.of(properties.getCertificatePath()));
		PrivateKey privateKey = loadPrivateKey(Path.of(properties.getPrivateKeyPath()));

		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(null, password);
		keyStore.setKeyEntry("inter-api", privateKey, password, new Certificate[] { certificate });
		return keyStore;
	}

	private Certificate loadCertificate(Path certificatePath) throws Exception {
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		try (var inputStream = Files.newInputStream(certificatePath)) {
			return certificateFactory.generateCertificate(inputStream);
		}
	}

	private PrivateKey loadPrivateKey(Path privateKeyPath) throws Exception {
		String pem = Files.readString(privateKeyPath);
		if (pem.contains("BEGIN RSA PRIVATE KEY")) {
			throw new InterIntegrationException("Inter private key must be PKCS#8. Convert it with: openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in interapi_chave.key -out interapi_chave_pkcs8.key");
		}

		String normalized = pem
			.replace("-----BEGIN PRIVATE KEY-----", "")
			.replace("-----END PRIVATE KEY-----", "")
			.replaceAll("\\s", "");

		byte[] keyBytes = Base64.getDecoder().decode(normalized);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

		try {
			return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
		} catch (Exception ignored) {
			return KeyFactory.getInstance("EC").generatePrivate(keySpec);
		}
	}
}
