package br.com.unopay.api.uaa.oauth2;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;

public class UnovationTokenRequest extends TokenRequest implements Serializable {

    public UnovationTokenRequest(Map<String, String> requestParameters, String clientId, Collection<String> scope,
                                 String grantType) {
        super(requestParameters, clientId, scope, grantType);
    }

    @Override
    public OAuth2Request createOAuth2Request(ClientDetails client) {
        Map<String, String> requestParameters = getRequestParameters();
        HashMap<String, String> modifiable = new HashMap<String, String>(requestParameters);
        preventPasswordLeaks(modifiable);
        addGrantTypeFromOauth2Request(modifiable);
        HashMap<String, Serializable> extensions = new HashMap<>();
        client.getAdditionalInformation().forEach( (k,v) -> extensions.put(k, (Serializable) v));
        return new OAuth2Request(modifiable, client.getClientId(), client.getAuthorities(), true, this.getScope(),
                client.getResourceIds(), null, null, extensions);
    }

    private void addGrantTypeFromOauth2Request(HashMap<String, String> modifiable) {
        modifiable.put("grant_type", getGrantType());
    }

    private void preventPasswordLeaks(HashMap<String, String> modifiable) {
        modifiable.remove("password");
        modifiable.remove("client_secret");
    }
}