package br.com.unopay.api.wingoo.service;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.service.IssuerService;
import br.com.unopay.api.config.ClientConfig;
import br.com.unopay.api.wingoo.model.Password;
import br.com.unopay.api.wingoo.model.WingooUserMapping;
import br.com.wingoo.userclient.client.UserClient;
import br.com.wingoo.userclient.model.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    public User create(Contractor contractor){
        try {
            return wingooUserClient(contractor.getIssuerDocument()).create(WingooUserMapping.fromContractor(contractor));
        } catch (HttpClientErrorException e){
            log.error(e.getResponseBodyAsString());
            throw e;
        }
    }

    public UserClient wingooUserClient(String issuerDocument){
        return new UserClient(wingooOauth2RestTemplate(issuerDocument), String.format("%s/users", wingooApi));
    }

    private OAuth2RestTemplate wingooOauth2RestTemplate(String issuerDocument) {
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(getResourceDetails(issuerDocument));
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
