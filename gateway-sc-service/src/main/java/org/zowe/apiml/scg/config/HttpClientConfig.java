/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.scg.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

@Configuration
@Slf4j
@Setter
@ConfigurationProperties("server.ssl")
public class HttpClientConfig {

    //TODO this should be enhanced
    private String keyStore;
    private String keyStorePassword;
    private String trustStore;
    private String trustStorePassword;

    @Bean
    SslContext secureClientContextWithoutClientCertificate() throws IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException {

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(ResourceUtils.getFile(HttpClientConfig.this.keyStore)), keyStorePassword.toCharArray());

        // Set up key manager factory to use our key store
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

        // truststore
        KeyStore trustStore = KeyStore.getInstance("PKCS12");
        trustStore.load(new FileInputStream((ResourceUtils.getFile(HttpClientConfig.this.trustStore))), trustStorePassword.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        return SslContextBuilder.forClient()
            //.keyManager(keyManagerFactory) // Disable constant appending of client cert
            .trustManager(trustManagerFactory)
            .build();
    }

    @Bean
    SslContext secureClientContextWithClientCertificate() throws IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException {

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(ResourceUtils.getFile(HttpClientConfig.this.keyStore)), keyStorePassword.toCharArray());

        // Set up key manager factory to use our key store
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

        // truststore
        KeyStore trustStore = KeyStore.getInstance("PKCS12");
        trustStore.load(new FileInputStream((ResourceUtils.getFile(HttpClientConfig.this.trustStore))), trustStorePassword.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        return SslContextBuilder.forClient()
            .keyManager(keyManagerFactory)
            .trustManager(trustManagerFactory)
            .build();
    }

    //@Bean
    HttpClientCustomizer clientCustomizer() {
        return new HttpClientCustomizer() {
            @Override
            public HttpClient customize(HttpClient httpClient) {

                return httpClient.secure(spec -> {

                    try {
                        KeyStore keyStore = KeyStore.getInstance("PKCS12");
                        keyStore.load(new FileInputStream(ResourceUtils.getFile(HttpClientConfig.this.keyStore)), keyStorePassword.toCharArray());

                        // Set up key manager factory to use our key store
                        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                        keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

                        // truststore
                        KeyStore trustStore = KeyStore.getInstance("PKCS12");
                        trustStore.load(new FileInputStream((ResourceUtils.getFile(HttpClientConfig.this.trustStore))), trustStorePassword.toCharArray());

                        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                        trustManagerFactory.init(trustStore);

                        spec.sslContext(SslContextBuilder.forClient()
                            //.keyManager(keyManagerFactory) // Disable constant appending of client cert
                            .trustManager(trustManagerFactory)
                            .build());
                    } catch (Exception e) {
                        log.error("Unable to set SSL Context", e);
                    }

                });
            }
        };
    }

}
