package br.com.unopay.api.uaa.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class UnovationOAuth2RequestFactory extends DefaultOAuth2RequestFactory {

    private ClientDetailsService clientDetailsService;

    @Autowired
    public UnovationOAuth2RequestFactory(ClientDetailsService clientDetailsService) {
        super(clientDetailsService);
        this.clientDetailsService = clientDetailsService;
    }

    @Override
    public TokenRequest createTokenRequest(Map<String, String> requestParameters, ClientDetails authenticatedClient) {

        String clientId = requestParameters.get(OAuth2Utils.CLIENT_ID);
        if (clientId == null) {
            // if the clientId wasn't passed in in the map, we add pull it from the authenticated client object
            clientId = authenticatedClient.getClientId();
        }
        else {
            // otherwise, make sure that they match
            if (!clientId.equals(authenticatedClient.getClientId())) {
                throw new InvalidClientException("Given client ID does not match authenticated client");
            }
        }
        String grantType = requestParameters.get(OAuth2Utils.GRANT_TYPE);

        Set<String> scopes = extractUnovationScopes(requestParameters, clientId);
        return new UnovationTokenRequest(requestParameters, clientId, scopes, grantType);
    }

    private Set<String> extractUnovationScopes(Map<String, String> requestParameters, String clientId) {
        Set<String> scopes = OAuth2Utils.parseParameterList(requestParameters.get(OAuth2Utils.SCOPE));
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);

        if ((scopes == null || scopes.isEmpty())) {
            // If no scopes are specified in the incoming data, use the default values registered with the client
            // (the spec allows us to choose between this option and rejecting the request completely, so we'll take the
            // least obnoxious choice as a default).
            scopes = clientDetails.getScope();
        }
        return scopes;
    }

}
