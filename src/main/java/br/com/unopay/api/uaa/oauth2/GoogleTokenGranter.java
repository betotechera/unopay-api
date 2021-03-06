package br.com.unopay.api.uaa.oauth2;

import br.com.unopay.api.uaa.google.GoogleProfile;
import br.com.unopay.api.uaa.google.GoogleService;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.repository.UserDetailRepository;
import br.com.unopay.api.uaa.service.GroupService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

public class GoogleTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "google";
    private static final String ORGANIZATION = "organization";

    private GoogleService googleService;
    private UserDetailRepository userDetailRepository;
    private GroupService groupService;

    public GoogleTokenGranter(GoogleService googleService,
                                 UserDetailRepository userDetailRepository,
                                 AuthorizationServerTokenServices tokenServices,
                                 ClientDetailsService clientDetailsService,
                                 OAuth2RequestFactory requestFactory) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.googleService = googleService;
        this.userDetailRepository = userDetailRepository;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

        Map<String, String> params = tokenRequest.getRequestParameters();

        GoogleProfile profile = googleService.getProfile(params.get("token"));
        if (profile == null) {
            throw new InvalidRequestException("invalid google token");
        }

        String organization = (String) client.getAdditionalInformation().get(ORGANIZATION);
        if (organization == null) {
            throw new InvalidClientException("missing organization identifier for client");
        }

        Optional<UserDetail> optional = userDetailRepository.findByEmailIgnoreCase(profile.getEmail());
        UserDetail user =  optional.orElseThrow(()->
                new BadCredentialsException(
                        String.format("could not find user with google account %s",
                        profile.toString())));

        List<SimpleGrantedAuthority> authorities = user.toGrantedAuthorities(groupService.findUserGroups(user.getId()));

        AuthUserContextHolder.setAuthUserId(user.getId());

        Authentication userAuth = new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);
        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }
}