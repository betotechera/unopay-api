package br.com.unopay.api.uaa.oauth2;

import java.util.Collection;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

public class UnovationTokenStore implements TokenStore {

    UnopayRedisTokenStore redisTokenStore;
    JdbcTokenStore jdbcTokenStore;

    public UnovationTokenStore(UnopayRedisTokenStore redisTokenStore, JdbcTokenStore jdbcTokenStore) {
        this.redisTokenStore = redisTokenStore;
        this.jdbcTokenStore = jdbcTokenStore;
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return redisTokenStore.readAuthentication(token);
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        return redisTokenStore.readAuthentication(token);
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        redisTokenStore.storeAccessToken(token, authentication);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        return redisTokenStore.readAccessToken(tokenValue);
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        redisTokenStore.removeAccessToken(token);
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        jdbcTokenStore.storeRefreshToken(refreshToken, authentication);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        return jdbcTokenStore.readRefreshToken(tokenValue);
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return jdbcTokenStore.readAuthenticationForRefreshToken(token);
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        jdbcTokenStore.removeRefreshToken(token);
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) { }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        return redisTokenStore.getAccessToken(authentication);
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        return redisTokenStore.findTokensByClientIdAndUserName(clientId, userName);
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        return redisTokenStore.findTokensByClientId(clientId);
    }

}
