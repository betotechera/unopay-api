package br.com.unopay.api.uaa.controller;

import br.com.unopay.api.uaa.model.Group;
import br.com.unopay.api.uaa.model.NewPassword;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.model.filter.UserFilter;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.PasswordRequired;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import br.com.unopay.api.uaa.service.GroupService;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.api.util.StringJoiner;
import br.com.unopay.bootcommons.exception.BadRequestException;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
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
import java.util.Set;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Timed(prefix = "api")
@RestController
public class UserDetailController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailController.class);

    private UserDetailService userDetailService;
    private TokenStore tokenStore;
    private GroupService groupService;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public UserDetailController(UserDetailService userDetailService, TokenStore tokenStore, GroupService groupService) {
        this.userDetailService = userDetailService;
        this.tokenStore = tokenStore;
        this.groupService = groupService;
    }

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<UserDetail> create(@Validated(Create.class) @RequestBody UserDetail user) {
        LOGGER.info("creating uaa user {}", user);
        UserDetail created = userDetailService.create(user);
        return ResponseEntity
                .created(URI.create("/users/"+created.getId()))
                .body(created);

    }

    @PreAuthorize("#oauth2.isUser()")
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/users/me", method = PUT)
    public void updateMe(OAuth2Authentication authentication,
                         @Validated({Update.class, PasswordRequired.class}) @RequestBody UserDetail user) {

        UserDetail userDetail = userDetailService.getByEmail(authentication.getName());

        user.setId(userDetail.getId());

        LOGGER.info("updating uaa user me {}", user);
        userDetailService.update(user);
    }

    @JsonView(Views.Public.class)
    @PreAuthorize("#oauth2.isUser()")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "users/me/profile", method = GET)
    public UserDetail getMe(OAuth2Authentication authentication) {
        LOGGER.info("get uaa user={}", authentication.getName());
        return userDetailService.getByEmail(authentication.getName());
    }

    @PreAuthorize("#oauth2.isUser()")
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/users/me/tokens", method = RequestMethod.DELETE)
    public void revoke(OAuth2Authentication authentication) {
        Collection<OAuth2AccessToken> accessTokens = tokenStore.findTokensByClientIdAndUserName(authentication.getOAuth2Request().getClientId(), authentication.getName());
        accessTokens.forEach(tokenStore::removeAccessToken);
    }

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/{id}", method = GET)
    public UserDetail get(@PathVariable  String id) {
        LOGGER.info("get uaa user={}", id);
        return userDetailService.getById(id);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/users/{id}", method = PUT)
    public void update(@PathVariable  String id,
                       @Validated(Update.class) @RequestBody UserDetail user) {
        user.setId(id);
        LOGGER.info("updating uaa user {}", user);
        userDetailService.update(user);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        LOGGER.info("removing uaa userId={}", id);
        userDetailService.delete(id);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/users/{id}/groups", method = PUT)
    public void groupMembers(@PathVariable("id") String id, @RequestBody Set<String> groupsIds) {
        String groupsAsString = StringJoiner.join(groupsIds);
        LOGGER.info("associate user={} with groups={}", id, groupsAsString);
        groupService.associateUserWithGroups(id, groupsIds);
    }

    @ResponseStatus(HttpStatus.OK)
    @JsonView(Views.Public.class)
    @RequestMapping(value = "/users/{id}/groups", method = GET)
    public Results<Group> getGroups(@PathVariable("id") String id) {
        LOGGER.info("get members to group={}", id);
        List<Group> groups =  groupService.findUserGroups(id);
        return new Results<>(groups);
    }

    @ResponseStatus(HttpStatus.OK)
    @JsonView(Views.List.class)
    @RequestMapping(value = "/users", method = GET)
    public Results<UserDetail> getByParams(UserFilter userFilter,@Validated UnovationPageRequest pageable) {
        LOGGER.info("search users by filter with filter={}", userFilter);
        Page<UserDetail> page =  userDetailService.findByFilter(userFilter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/users", api));
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/users/password", method = PUT)
    public void updatePasswordByToken(@RequestBody @Validated NewPassword passwordChange) {
        LOGGER.info("password token change request. change={}", passwordChange);
        userDetailService.updatePasswordByToken(passwordChange);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/users/{id}/password", method = DELETE)
    public void resetPasswordByToken(@PathVariable("id") String id) {
        LOGGER.info("password reset request. to user={}", id);
        userDetailService.resetPasswordById(id);
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("#oauth2.isUser()")
    @RequestMapping(value = "/users/me/password", method = DELETE)
    public void resetPassword(OAuth2Authentication authentication) {
        LOGGER.info("password reset request. to user={}", authentication.getName());
        userDetailService.resetPasswordByEmail(authentication.getName());
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("#oauth2.isUser()")
    @RequestMapping(value = "/users/me/password", method = PUT)
    public void updatePassword(OAuth2Authentication authentication, @RequestBody @Validated NewPassword passwordChange) {
        LOGGER.info("password change request. to user={}", authentication.getName());
        userDetailService.updatePasswordByEmail(authentication.getName(), passwordChange);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/users/password", method = GET, params = "email")
    public void resetPasswordByEmail(HttpServletRequest request) {
        String email = request.getParameter("email");
        LOGGER.info("password reset request. to user={}", email);
        userDetailService.resetPasswordByEmail(email);
    }
}
