package org.zowe.apiml.certificates.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import lombok.extern.slf4j.Slf4j;

/**
 * Manage Certificate but actually truststore. 
 * Truststore (KeyRing) is a place to store the certificates in. 
 * It's going to be Truststore.
 */
@Slf4j
public class CertificateManager {
    private final String pathToStore;
    private final char[] password;

    public CertificateManager(String pathToTheStore, char[] password) {
        this.pathToTheStore = pathToTheStore;
        this.password = password;
    }

    /**
     * Get the names of the certificates available 
     */
    public void listCertificates() {
        
    }

    /**
     * Add certificate to the data.
     */
    public void addCertificate() {

    }


    public void removeCertificate() {
        
    }
}
