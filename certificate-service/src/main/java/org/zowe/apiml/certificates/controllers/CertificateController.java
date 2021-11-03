package org.zowe.apiml.certificates.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Provide the API necessary to manage the certificates living within specific Keyring, KeyStore, Truststore
 */
public class CertificateController {
    /**
     * 
     * @return
     */
    @GetMapping(value = "/certificates", produces = "application/json")
    public ResponseEntity<?> listCertificates() {
        return null;
    }
    
    /**
     * 
     * @return
     */
    public ResponseEntity<?> addCertificate() {
        return null;
    }

    /**
     * 
     * @return
     */
    public ResponseEntity<?> removeCertificate() {
        return null;
    }
}
