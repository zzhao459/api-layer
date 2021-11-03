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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.zowe.apiml.Stores;
import org.zowe.apiml.certificates.ZoweConfiguration;
import org.zowe.apiml.certificates.service.CommandExecutor;

import java.io.IOException;
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

    @GetMapping("/tso-cmd/{cmd}")
    public String executeTSOCmd(@PathVariable("cmd")String cmd) throws IOException {
        return CommandExecutor.execute(cmd);
    }
    
    /**
     * Move one certificate from one store to another store Upload certificate
     * 
     * @return
     */
    @PostMapping("/certificate")
    public ResponseEntity<?> addCertificate() {
        Stores stores = new Stores(zoweConfiguration);
        return null;
    }

    /**
     * 
     * @return
     */
    @PostMapping("/certificate/upload")
    public ResponseEntity<?> addUploadedCertificate(@RequestParam("certificate") MultipartFile certificate) {
        return null;
    }

    /**
     * The expectation is that the current truststore contains the label and that the certificate is removed from the
     * default truststore. 
     * @return
     */
    @DeleteMapping("/certificate")
    public ResponseEntity<String> removeCertificate(String label) {
        try {
            Stores stores = new Stores(zoweConfiguration);
            stores.remove(label);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (KeyStoreException exception) {
            return new ResponseEntity<>("The certificate with provided label isn't part of the default truststore", HttpStatus.BAD_REQUEST);
        }
    }
}
