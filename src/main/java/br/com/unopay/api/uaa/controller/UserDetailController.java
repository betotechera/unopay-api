package br.com.unopay.api.uaa.controller;

import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.model.valistionsgroups.Create;
import br.com.unopay.api.uaa.model.valistionsgroups.Update;
import br.com.unopay.api.uaa.model.valistionsgroups.Views;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.BadRequestException;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Collection;
import java.util.List;

@Timed(prefix = "api")
@RestController
public class UserDetailController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailController.class);

    private UserDetailService userDetailService;
    private TokenStore tokenStore;
    @Autowired
    public UserDetailController(UserDetailService userDetailService, TokenStore tokenStore) {
        this.userDetailService = userDetailService;
        this.tokenStore = tokenStore;
    }

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<UserDetail> create(@Validated(Create.class) @RequestBody UserDetail user) {
        LOGGER.info("creating uaa user {}", user);
        UserDetail created = userDetailService.create(user);
        return ResponseEntity
                .created(URI.create("/users"+created.getId()))
                .body(created);

    }

    @PreAuthorize("#oauth2.isUser()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/users/me", method = RequestMethod.PUT)
    public void updateMe(OAuth2Authentication authentication,
                         @Validated(Update.class) @RequestBody UserDetail user) {

        UserDetail userDetail = userDetailService.getByEmail(authentication.getName());

        user.setId(userDetail.getId());

        LOGGER.info("updating uaa user me {}", user);
        userDetailService.update(user);
    }

    @PreAuthorize("#oauth2.isUser()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/users/me", method = RequestMethod.GET)
    public void getMe(OAuth2Authentication authentication,
                         @Validated(Update.class) @RequestBody UserDetail user) {

        UserDetail userDetail = userDetailService.getByEmail(authentication.getName());

        user.setId(userDetail.getId());

        LOGGER.info("updating uaa user me {}", user);
        userDetailService.update(user);
    }

    @JsonView(Views.Public.class)
    @PreAuthorize("#oauth2.isUser()")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public UserDetail getMe(OAuth2Authentication authentication) {
        LOGGER.info("get uaa user={}", authentication.getName());
        return userDetailService.getByEmail(authentication.getName());
    }

    @PreAuthorize("#oauth2.isUser()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/users/me/revokeTokens", method = RequestMethod.POST)
    public void revoke(OAuth2Authentication authentication) {
        Collection<OAuth2AccessToken> accessTokens = tokenStore.findTokensByClientIdAndUserName(authentication.getOAuth2Request().getClientId(), authentication.getName());
        accessTokens.forEach(accessToken -> {
            tokenStore.removeAccessToken(accessToken);
        });
    }

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public UserDetail get(@PathVariable  String id) {
        LOGGER.info("get uaa user={}", id);
        return userDetailService.getById(id);
    }

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/search", method = RequestMethod.GET)
    public List<UserDetail> getByAuthority(OAuth2Authentication authentication, HttpServletRequest request) {
        String authority = request.getParameter("authority");

        if (authority == null || authority.isEmpty()) {
            throw new BadRequestException("Authority required");
        }

        LOGGER.info("get uaa user by authority={}", authority);
        return userDetailService.getByAuthority(authority);
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id,
                       @Validated(Update.class) @RequestBody UserDetail user) {
        user.setId(id);
        LOGGER.info("updating uaa user {}", user);
        userDetailService.update(user);
    }
}
