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

import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@RestController
@RequiredArgsConstructor
public class StoresController {

    private final ZoweConfiguration zoweConfiguration;

    @GetMapping("/trusted-certs")
    public Map<Object, Object> getListOfTrustedCerts() throws KeyStoreException {
        Stores stores = new Stores(zoweConfiguration);
        return stores.getListOfCertificates().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toString()));
    }

    @GetMapping("/tso-cmd/{cmd}")
    public String executeTSOCmd(@PathVariable("cmd") String cmd) throws IOException {
        return CommandExecutor.execute(cmd);
    }

    /**
     * Add to the truststore certificate of specific service. 
     * 
     * @return
     */
    @PostMapping("/certificate")
    public ResponseEntity<?> addCertificate(
        @RequestParam("label") String label,
        @RequestParam("url") String url
    ) {
        Stores stores = new Stores(zoweConfiguration);
        // TODO: Load the certificate

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Add uploaded certificate under selected label to the given keyring. 
     * 
     * @return 200 when suceeded, 500 otherwise
     */
    @PostMapping("/certificate/upload")
    public ResponseEntity<?> addUploadedCertificate(@RequestParam("certificate") MultipartFile certificateFile,
            @RequestParam("label") String label) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) cf.generateCertificate(certificateFile.getInputStream());
            
            Stores stores = new Stores(zoweConfiguration);
            stores.add(label, certificate);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (KeyStoreException | IOException | CertificateException e) {
            return new ResponseEntity<>("There was an issue with adding of the certificate to the truststore", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * The expectation is that the current truststore contains the label and that
     * the certificate is removed from the default truststore. If that's not the case
     * return 400 otherwise 200
     * 
     * @return 200 when succeeds, 400 otherwise 
     */
    @DeleteMapping("/certificate")
    public ResponseEntity<String> removeCertificate(String label) {
        try {
            Stores stores = new Stores(zoweConfiguration);
            stores.remove(label);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (KeyStoreException exception) {
            return new ResponseEntity<>("The certificate with provided label isn't part of the default truststore",
                    HttpStatus.BAD_REQUEST);
        }
    }
}
