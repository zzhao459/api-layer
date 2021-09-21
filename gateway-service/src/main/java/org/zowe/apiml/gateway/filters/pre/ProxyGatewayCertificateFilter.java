/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.gateway.filters.pre;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * This filter will add X509 certificate from Proxy Gateway
 */
@Component
public class ProxyGatewayCertificateFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        X509Certificate[] certificates = new X509Certificate[1];
        String clientCert = request.getHeader("X-TLS-CLIENT-CERT");
        if (clientCert != null) {
            try {
                InputStream targetStream = new ByteArrayInputStream(Base64.getDecoder().decode(clientCert));
                certificates[0] = (X509Certificate) CertificateFactory
                    .getInstance("X509")
                    .generateCertificate(targetStream);
            } catch (Exception e) {
                filterChain.doFilter(request, response);
            }
            request.setAttribute("javax.servlet.request.X509Certificate", certificates);
        }


        filterChain.doFilter(request, response);
    }

}
