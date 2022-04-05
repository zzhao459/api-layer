/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.security.common.config;

import com.ibm.security.common.auth.config.SafSecurityConfigurationProperties;
import com.ibm.security.common.auth.saf.SafMethodSecurityExpressionHandler;
import com.ibm.security.common.auth.saf.SafResourceAccessVerifying;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
@EnableConfigurationProperties(SafSecurityConfigurationProperties.class)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    private final SafSecurityConfigurationProperties safSecurityConfigurationProperties;
    private final SafResourceAccessVerifying safResourceAccessVerifying;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return new SafMethodSecurityExpressionHandler(safSecurityConfigurationProperties, safResourceAccessVerifying);
    }

}
