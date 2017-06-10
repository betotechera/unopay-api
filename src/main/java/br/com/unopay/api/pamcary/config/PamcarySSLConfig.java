package br.com.unopay.api.pamcary.config;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;


@Configuration
public class PamcarySSLConfig {


    @Value("${soap.client.ssl.key-store:}")
    private Resource keyStore;

    @Value("${soap.client.ssl.key-store-password:}")
    private char[] keyStorePassword;

    @Value("${soap.client.ssl.trust-store:}")
    private Resource trustStore;

    @Value("${soap.client.ssl.trust-store-password:}")
    private char[] trustStorePassword;

    @Bean
    @SneakyThrows
    public SSLSocketFactory pamcarySSLSocketFactory (){
        KeyStore clientStore = KeyStore.getInstance("PKCS12");
        clientStore.load(new FileInputStream(keyStore.getFile()), keyStorePassword);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientStore, keyStorePassword);
        KeyManager[] kms = kmf.getKeyManagers();

        KeyStore trustStore2 = KeyStore.getInstance("JKS");
        trustStore2.load(new FileInputStream(trustStore.getFile()), trustStorePassword);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore2);
        TrustManager[] tms = tmf.getTrustManagers();

        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(kms, tms, new SecureRandom());

        return sslContext.getSocketFactory();

    }
}
