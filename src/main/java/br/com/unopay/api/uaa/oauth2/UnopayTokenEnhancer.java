package br.com.unopay.api.uaa.oauth2;

import java.util.HashMap;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

@Component
public class UnopayTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        if (accessToken instanceof DefaultOAuth2AccessToken) {
            HashMap<String, Object> extensions = new HashMap<>();
            extensions.putAll(authentication.getOAuth2Request().getExtensions());
            addUserExtension(extensions);
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(extensions);
        }

        return accessToken;
    }

    private void addUserExtension(HashMap<String, Object> extensions) {

        String userId = AuthUserContextHolder.getAuthUserId();
        if (userId != null) {
            extensions.put("user_id", userId);
        }

    }
}
