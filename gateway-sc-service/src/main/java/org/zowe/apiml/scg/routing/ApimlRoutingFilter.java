/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.apiml.scg.routing;

import io.netty.handler.ssl.SslContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.config.HttpClientProperties;
import org.springframework.cloud.gateway.filter.NettyRoutingFilter;
import org.springframework.cloud.gateway.filter.headers.HttpHeadersFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.netty.http.client.HttpClient;

import java.util.List;

@Component
@Slf4j
//TODO works without primary
public class ApimlRoutingFilter extends NettyRoutingFilter {

    @Autowired
    SslContext secureClientContextWithoutClientCertificate;

    @Autowired
    SslContext secureClientContextWithClientCertificate;

    public ApimlRoutingFilter(HttpClient httpClient, ObjectProvider<List<HttpHeadersFilter>> headersFiltersProvider, HttpClientProperties properties) {
        super(httpClient, headersFiltersProvider, properties);
    }

    @Override
    protected HttpClient getHttpClient(Route route, ServerWebExchange exchange) {
        //TODO this is the mechanism through which customizations of client are possible.
        // The route object can carry metadata and we can load it up when we create the routes in route locator.
        HttpClient httpClient = super.getHttpClient(route, exchange);
        return httpClient.secure(sslContextSpec -> sslContextSpec.sslContext(secureClientContextWithoutClientCertificate));
    }
}
