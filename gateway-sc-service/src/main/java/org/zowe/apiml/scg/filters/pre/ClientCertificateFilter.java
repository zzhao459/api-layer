/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.scg.filters.pre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.*;

@Slf4j
public class ClientCertificateFilter implements GlobalFilter {


    //TODO this filter could mutate the headers and filter the header if it is supplied by client.
    // The RemoveHopByHopHeaderFilter is acting after this one, so it is not effective
    // because it's part of netty routing filter. Thats why.

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Optional<X509Certificate> first = Optional.empty();

        SslInfo sslInfo = exchange.getRequest().getSslInfo();
        if (sslInfo != null && sslInfo.getPeerCertificates() != null) {
            first = Arrays.stream(sslInfo.getPeerCertificates()).findFirst();
        }

        ServerHttpRequest request = exchange.getRequest();

        first.ifPresent(x509Certificate -> {
            request.mutate().headers(httpHeaders -> {
                try {
                    httpHeaders.add("X-TLS-CLIENT-CERT", encodeCertificate(x509Certificate));
                } catch (CertificateEncodingException e) {
                    e.printStackTrace();
                }
            }).build();

        });

        return chain.filter(exchange.mutate().request(request).build());
    }

    private String encodeCertificate(X509Certificate x509Certificate) throws CertificateEncodingException {
        return new String(Base64.getEncoder().encode(x509Certificate.getEncoded()));
    }
}
