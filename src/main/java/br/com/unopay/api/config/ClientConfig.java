package br.com.unopay.api.config;


import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;


public class ClientConfig {

    public static void configureConnectionPool(OAuth2RestTemplate template) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(HttpClientBuilder.create()
                .setConnectionManager(connectionManager())
                .setMaxConnPerRoute(200)
                .setMaxConnTotal(200)
                .setDefaultRequestConfig(requestConfig())
                .disableAutomaticRetries()
                .build());
        template.setRequestFactory(factory);
    }

    private static RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setSocketTimeout(10000)
                .setConnectTimeout(3000)
                .build();
    }

    private static PoolingHttpClientConnectionManager connectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(200);
        connectionManager.setValidateAfterInactivity(5000);
        return connectionManager;
    }


}
