package br.com.unopay.api.billing.boleto.santander.config;

import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import lombok.SneakyThrows;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;


@Configuration
public class SSLConfig {


    @Value("${soap.client.ssl.key-store:}")
    private Resource keyStore;

    @Value("${soap.client.ssl.key-store-password:}")
    private char[] keyStorePassword;

    @Bean
    @SneakyThrows
    public SSLSocketFactory sSLSocketFactory (){
        KeyStore clientStore = KeyStore.getInstance("PKCS12");
        clientStore.load(keyStore.getInputStream(), keyStorePassword);
        SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(clientStore, keyStorePassword).build();
        return sslContext.getSocketFactory();

    }


}
