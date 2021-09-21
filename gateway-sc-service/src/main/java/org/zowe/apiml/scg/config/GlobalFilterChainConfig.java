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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zowe.apiml.scg.filters.pre.ClientCertificateFilter;

@Configuration
public class GlobalFilterChainConfig {

    @Bean
    public ClientCertificateFilter clientCertFilter() {
        return new ClientCertificateFilter();
    }

    // Not needed as the hop by hop is always there?
//    @Bean
//    RemoveHopByHopHeadersFilter RemoveHopByHopHeadersFilter() {
//        return new RemoveHopByHopHeadersFilter();
//    }
}
