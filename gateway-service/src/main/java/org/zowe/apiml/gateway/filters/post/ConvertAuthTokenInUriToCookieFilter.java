/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.gateway.filters.post;

import com.netflix.zuul.context.RequestContext;
import org.zowe.apiml.security.common.config.AuthConfigurationProperties;
import org.zowe.apiml.util.CookieUtil;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SEND_RESPONSE_FILTER_ORDER;

/**
 * Uses the authentication token provided as a query parameter and puts it to
 * the expected place (cookie).
 */
public class ConvertAuthTokenInUriToCookieFilter extends PostZuulFilter {
    private final AuthConfigurationProperties authConfigurationProperties;

    public ConvertAuthTokenInUriToCookieFilter(AuthConfigurationProperties authConfigurationProperties) {
        this.authConfigurationProperties = authConfigurationProperties;
    }

    public int filterOrder() {
        return SEND_RESPONSE_FILTER_ORDER - 1;
    }

    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        AuthConfigurationProperties.CookieProperties cp = authConfigurationProperties.getCookieProperties();
        return context.getRequestQueryParams() != null && context.getRequestQueryParams().containsKey(cp.getCookieName());
    }

    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletResponse servletResponse = context.getResponse();
        AuthConfigurationProperties.CookieProperties cp =  authConfigurationProperties.getCookieProperties();

        // SameSite attribute is not supported in Cookie used in HttpServletResponse.addCookie,
        // so specify Set-Cookie header directly
        String cookieHeader = CookieUtil.setCookieHeader(
            cp.getCookieName(),
            context.getRequestQueryParams().get(cp.getCookieName()).get(0),
            cp.getCookieComment(),
            cp.getCookiePath(),
            cp.getCookieSameSite().getValue(),
            cp.getCookieMaxAge(),
            true,
            true
        );
        servletResponse.addHeader("Set-Cookie", cookieHeader);

        String url = context.getRequest().getRequestURL().toString();
        String newUrl;
        if (url.endsWith("/apicatalog/")) {
            newUrl = url + "#/dashboard";
        } else {
            newUrl = url;
        }
        context.addZuulResponseHeader("Location", newUrl);
        context.setResponseStatusCode(HttpServletResponse.SC_MOVED_TEMPORARILY);
        return null;
    }
}
