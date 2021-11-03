/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */
package org.zowe.apiml.certificates.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zowe.apiml.Stores;
import org.zowe.apiml.certificates.ZoweConfiguration;

import java.security.KeyStoreException;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class StoresController {

    private final ZoweConfiguration zoweConfiguration;

    @GetMapping("/trusted-certs")
    public Map<Object, Object> getListOfTrustedCerts() throws KeyStoreException {
        Stores stores = new Stores(zoweConfiguration);
        return stores.getListOfCertificates().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toString()));
    }
}
