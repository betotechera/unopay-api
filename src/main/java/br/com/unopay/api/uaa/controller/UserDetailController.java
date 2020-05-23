package br.com.unopay.api.uaa.controller;

import br.com.unopay.api.billing.creditcard.model.CreditCard;
import br.com.unopay.api.billing.creditcard.model.UserCreditCard;
import br.com.unopay.api.billing.creditcard.model.filter.UserCreditCardFilter;
import br.com.unopay.api.billing.creditcard.service.UserCreditCardService;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.PasswordRequired;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.uaa.model.Group;
import br.com.unopay.api.uaa.model.NewPassword;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.model.filter.UserFilter;
import br.com.unopay.api.uaa.service.GroupService;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.api.util.StringJoiner;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Timed(prefix = "api")
@RestController
public class UserDetailController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailController.class);

    private UserDetailService userDetailService;
    private TokenStore tokenStore;
    private GroupService groupService;
    private UserCreditCardService userCreditCardService;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public UserDetailController(UserDetailService userDetailService,
                                TokenStore tokenStore,
                                GroupService groupService,
                                UserCreditCardService userCreditCardService) {
        this.userDetailService = userDetailService;
        this.tokenStore = tokenStore;
        this.groupService = groupService;
        this.userCreditCardService = userCreditCardService;
    }

    @JsonView(Views.User.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_USER_DETAIL')")
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

    @JsonView(Views.User.Detail.class)
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
        String clientId = authentication.getOAuth2Request().getClientId();
        Collection<OAuth2AccessToken> accessTokens = tokenStore
                .findTokensByClientIdAndUserName(clientId, authentication.getName());
        accessTokens.forEach(tokenStore::removeAccessToken);
    }

    @JsonView(Views.User.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_USER_DETAIL')")
    @RequestMapping(value = "/users/{id}", method = GET)
    public UserDetail get(@PathVariable  String id) {
        LOGGER.info("get uaa user={}", id);
        return userDetailService.getById(id);
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_USER_DETAIL')")
    @RequestMapping(value = "/users/{id}", method = PUT)
    public void update(@PathVariable  String id,
                       @Validated(Update.class) @RequestBody UserDetail user) {
        user.setId(id);
        LOGGER.info("updating uaa user {}", user);
        userDetailService.update(user);
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_USER_DETAIL')")
    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        LOGGER.info("removing uaa userId={}", id);
        userDetailService.delete(id);
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_USER_DETAIL')")
    @RequestMapping(value = "/users/{id}/groups", method = PUT)
    public void groupMembers(@PathVariable("id") String id, @RequestBody Set<String> groupsIds) {
        String groupsAsString = StringJoiner.join(groupsIds);
        LOGGER.info("associate user={} with groups={}", id, groupsAsString);
        groupService.associateUserWithGroups(id, groupsIds);
    }

    @ResponseStatus(HttpStatus.OK)
    @JsonView(Views.User.Detail.class)
    @PreAuthorize("hasRole('ROLE_LIST_USER_DETAIL')")
    @RequestMapping(value = "/users/{id}/groups", method = GET)
    public Results<Group> getGroups(@PathVariable("id") String id) {
        LOGGER.info("get members to group={}", id);
        List<Group> groups =  groupService.findUserGroups(id);
        return new Results<>(groups);
    }

    @ResponseStatus(HttpStatus.OK)
    @JsonView(Views.User.List.class)
    @PreAuthorize("hasRole('ROLE_LIST_USER_DETAIL')")
    @RequestMapping(value = "/users", method = GET)
    public Results<UserDetail> getByParams(UserFilter userFilter,@Validated UnovationPageRequest pageable) {
        LOGGER.info("search users by filter with filter={}", userFilter);
        Page<UserDetail> page =  userDetailService.findByFilter(userFilter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/users", api));
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/users/password", method = PUT)
    public void updatePasswordByToken(@RequestBody @Validated NewPassword passwordChange) {
        LOGGER.info("password token change request. token={}", passwordChange.getToken());
        userDetailService.updatePasswordByToken(passwordChange);
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_USER_DETAIL')")
    @RequestMapping(value = "/users/{id}/password", method = DELETE)
    public void resetPasswordByToken(@PathVariable("id") String id) {
        LOGGER.info("password reset request. to user={}", id);
        userDetailService.resetPasswordById(id);
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("#oauth2.isUser()")
    @RequestMapping(value = "/users/me/password", method = DELETE)
    public void resetPassword(OAuth2Authentication authentication,
                              @RequestParam String requestOrigin) {
        LOGGER.info("password reset request. to user={} on {}", authentication.getName(), requestOrigin);
        userDetailService.resetPasswordByEmail(authentication.getName(), requestOrigin);
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("#oauth2.isUser()")
    @RequestMapping(value = "/users/me/password", method = PUT)
    public void updatePassword(OAuth2Authentication authentication,@RequestBody @Validated NewPassword passwordChange){
        LOGGER.info("password change request. to user={}", authentication.getName());
        userDetailService.updatePasswordByEmail(authentication.getName(), passwordChange);
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/users/password", method = GET, params = "email")
    public void resetPasswordByEmail(HttpServletRequest request, @RequestParam String origin) {
        String email = request.getParameter("email");
        LOGGER.info("password reset request. to user={} on {}", email, origin);
        userDetailService.resetPasswordByEmail(email, origin);
    }

    @JsonView(Views.UserCreditCard.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/me/credit-cards", method = RequestMethod.GET)
    public Results<UserCreditCard> getUserCreditCardByParams(UserDetail userDetail,
                                                             UserCreditCardFilter filter,
                                                             @Validated UnovationPageRequest pageable) {
        LOGGER.info("search user credit card with filter={} for user={}", filter, userDetail.getId());
        filter.setUser(userDetail.getId());
        Page<UserCreditCard> page = userCreditCardService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/users/me/credit-cards", api));
    }

    @JsonView(Views.UserCreditCard.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/me/credit-cards/{id}", method = RequestMethod.GET)
    public UserCreditCard getUserCreditCard(UserDetail userDetail, @PathVariable String id) {
        LOGGER.info("get user credit card={} for user={}", id, userDetail.getId());
        return userCreditCardService.findByIdForUser(id, userDetail);
    }

    @JsonView(Views.UserCreditCard.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/users/me/credit-cards", method = RequestMethod.POST)
    public ResponseEntity<UserCreditCard> createCreditCard(@Validated(Create.class) @RequestBody CreditCard creditCard,
                                                           UserDetail user) {
        LOGGER.info("adding a user credit card to user={}", user);
        UserCreditCard created = userCreditCardService.storeForUser(user, creditCard);
        return ResponseEntity
                .created(URI.create("/users/me/credit-cards"+created.getId()))
                .body(created);

    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/users/me/credit-cards/{id}", method = RequestMethod.DELETE)
    public void removeUserCreditCard(UserDetail userDetail, @PathVariable String id){
        LOGGER.info("removing user credit card id={} for user={}", id, userDetail.getId());
        userCreditCardService.deleteForUser(id, userDetail);
    }

}
