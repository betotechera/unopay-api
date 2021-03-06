package br.com.unopay.api.wingoo.service;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.service.IssuerService;
import br.com.unopay.api.config.ClientConfig;
import br.com.unopay.api.market.model.ContractorProduct;
import br.com.unopay.api.wingoo.model.Password;
import br.com.unopay.api.wingoo.model.WingooPaymentInfo;
import br.com.unopay.api.wingoo.model.WingooUserMapping;
import br.com.wingoo.userclient.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Data
@Slf4j
@Service
public class WingooService {

    @Autowired
    private IssuerService issuerService;
    @Value("${wingoo.api}")
    private String wingooApi;
    @Value("${wingoo.security.oauth2.client.accessTokenUri}")
    private String tokenInfo;

    public void create(ContractorProduct contractor){
        try {
            wingooOauth2RestTemplate(contractor.getIssuerDocument())
                    .postForObject(String.format("%s/subscription/enrollments/payment-returning", wingooApi),
                            WingooUserMapping.fromContractor(contractor), Void.class);
        } catch (HttpClientErrorException e){
            log.error(e.getResponseBodyAsString());
            throw e;
        }
    }

    private OAuth2RestTemplate wingooOauth2RestTemplate(String issuerDocument) {
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(getResourceDetails(issuerDocument));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        restTemplate.getMessageConverters().add(0, converter);
        ClientConfig.configureConnectionPool(restTemplate);
        return restTemplate;
    }

    private ClientCredentialsResourceDetails getResourceDetails(String issuerDocument) {
        Issuer issuer = issuerService.findByDocument(issuerDocument);
        ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
        resourceDetails.setAccessTokenUri(tokenInfo);
        resourceDetails.setClientId(issuer.wingooClientId());
        resourceDetails.setClientSecret(issuer.wingooClientSecret());
        return resourceDetails;
    }

    public void update(Password password){

    }
}
