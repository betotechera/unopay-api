package br.com.unopay.api.uaa.config;

import br.com.unopay.api.uaa.google.GoogleService;
import br.com.unopay.api.uaa.oauth2.GoogleTokenGranter;
import br.com.unopay.api.uaa.oauth2.UnopayTokenEnhancer;
import br.com.unopay.api.uaa.oauth2.UnovationOAuth2RequestFactory;
import br.com.unopay.api.uaa.repository.UserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GoogleService googleService;

    @Autowired
    private UnovationOAuth2RequestFactory oAuth2RequestFactory;

    @Autowired
    private UnopayTokenEnhancer tokenEnhancer;

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore)
                .authenticationManager(authenticationManager)
                .requestFactory(oAuth2RequestFactory)
                .tokenEnhancer(tokenEnhancer)
                .exceptionTranslator(new WebResponseExceptionTranslator())
                .accessTokenConverter(new DefaultAccessTokenConverter())
                .tokenGranter(tokenGranter(endpoints));
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer server) throws Exception {
        server.allowFormAuthenticationForClients();
        server.checkTokenAccess("hasRole('ROLE_CLIENT')");
        server.passwordEncoder(passwordEncoder);
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setTokenStore(tokenStore);
        return tokenServices;
    }

    private TokenGranter tokenGranter(final AuthorizationServerEndpointsConfigurer endpoints) {
        List<TokenGranter> granters = new ArrayList<>(Arrays.asList(endpoints.getTokenGranter()));

        granters.add(new GoogleTokenGranter(
                googleService,
                userDetailRepository,
                endpoints.getTokenServices(),
                endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory()));

        return new CompositeTokenGranter(granters);
    }

}