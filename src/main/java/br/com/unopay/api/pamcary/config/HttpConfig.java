package br.com.unopay.api.pamcary.config;

import lombok.SneakyThrows;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.net.ssl.SSLContext;


@Configuration
public class HttpConfig {


    // e.g. Add http.client.ssl.trust-store=classpath:ssl/truststore.jks to application.properties
    @Value("${http.client.ssl.trust-store:}")
    private Resource trustStore;

    @Value("${http.client.ssl.trust-store-password:}")
    private char[] trustStorePassword;

    @Bean
    @SneakyThrows
    public HttpClient httpClient() {

            SSLContext sslContext = SSLContexts
                    .custom()
                    .loadTrustMaterial(trustStore.getFile(),
                            trustStorePassword)
                    .build();

            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext,
                    (hostname, session) -> true);

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslSocketFactory)
                    .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        HttpClient httpClient = HttpClientBuilder.create()
                .disableAutomaticRetries()
                .setConnectionManager(connectionManager)
                .build();
        factory.setHttpClient(httpClient);
    return httpClient;

    }
}
