/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.certificates;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.zowe.apiml.Config;

@Configuration
public class ZoweConfiguration implements Config {

    @Value("${server.ssl.keyStore}")
    private String keyStore;
    @Value("${server.ssl.trustStore}")
    private String trustStore;
    @Value("${server.ssl.trustStorePassword}")
    private String trustPasswd;
    @Value("${server.ssl.keyStorePassword}")
    private String keyPasswd;
    @Value("${server.ssl.trustStoreType}")
    private String trustStoreType;
    @Value("${server.ssl.trustStoreType}")
    private String keyStoreType;
    @Value("${server.ssl.keyAlias}")
    private String keyAlias;

    @Override
    public String getKeyStore() {
        return keyStore;
    }

    @Override
    public String getTrustStore() {
        return trustStore;
    }

    @Override
    public String getTrustPasswd() {
        return trustPasswd;
    }

    @Override
    public String getKeyPasswd() {
        return keyPasswd;
    }

    @Override
    public String getTrustStoreType() {
        return trustStoreType;
    }

    @Override
    public String getKeyStoreType() {
        return keyStoreType;
    }

    @Override
    public String getKeyAlias() {
        return keyAlias;
    }

}
