package br.com.unopay.api.billing.boleto.itau.config;


import br.com.itau.autorizador.CobrancaClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;


@Configuration
public class ItauClientConfig {

    @Value("${itau.api:}")
    private String itauApi;

    @Bean
    @Qualifier("itau")
    @ConfigurationProperties("itau.security.oauth2.client")
    public ClientCredentialsResourceDetails itauOauth2Client() {
        return new ClientCredentialsResourceDetails();
    }

    @Bean
    @Qualifier("itauRestTemplate")
    public OAuth2RestTemplate itauOauth2RestTemplate() {
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(itauOauth2Client());
            configureConnectionPool(restTemplate);
            return restTemplate;
    }

    private void configureConnectionPool(OAuth2RestTemplate template) {
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

    private RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setSocketTimeout(10000)
                .setConnectTimeout(3000)
                .build();
    }

    private PoolingHttpClientConnectionManager connectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(200);
        connectionManager.setValidateAfterInactivity(5000);
        return connectionManager;
    }

    @Bean("cobrancaClient")
    public CobrancaClient cobrancaClient(){
        return new CobrancaClient(itauOauth2RestTemplate(), itauApi);
    }


}
