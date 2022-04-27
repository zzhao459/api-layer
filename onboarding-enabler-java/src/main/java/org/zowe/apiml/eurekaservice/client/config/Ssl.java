/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.eurekaservice.client.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ssl {
    private Boolean enabled = true;

    private Boolean verifySslCertificatesOfServices = true;

    private Boolean nonStrictVerifySslCertificatesOfServices = false;

    private String protocol;

    private String ciphers;

    private String keyAlias;

    private char[] keyPassword;

    private String keyStore;

    private char[] keyStorePassword;

    private String keyStoreType;

    private String trustStore;

    private char[] trustStorePassword;

    private String trustStoreType;
}
