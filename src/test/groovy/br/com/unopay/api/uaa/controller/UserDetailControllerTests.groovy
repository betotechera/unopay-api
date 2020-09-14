package br.com.unopay.api.uaa.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.billing.creditcard.model.PersonCreditCard
import br.com.unopay.api.billing.creditcard.service.PersonCreditCardService
import br.com.unopay.api.notification.service.NotificationService
import br.com.unopay.api.uaa.AuthServerApplicationTests
import br.com.unopay.api.uaa.model.NewPassword
import br.com.unopay.api.uaa.model.RequestOrigin
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.api.uaa.service.UserDetailService
import com.fasterxml.jackson.databind.ObjectMapper
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.core.Is.is
import static org.hamcrest.core.IsNull.notNullValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserDetailControllerTests extends AuthServerApplicationTests {
    private static final String USER_ENDPOINT = '/users?access_token={access_token}'
    private static final String USER_ME_ENDPOINT = '/users/me?access_token={access_token}'
    private static final String PROFILE_ENDPOINT = '/users/me/profile?access_token={access_token}'
    private static final String GROUP_ENDPOINT = '/users/{id}/groups?access_token={access_token}'

    @Autowired
    private PersonCreditCardService userCreditCardService

    @Autowired
    private FixtureCreator fixtureCreator

    @Autowired
    UserDetailService service

    NotificationService notificationService = Mock(NotificationService)

    def setup(){
        service.notificationService = notificationService
    }

    void should_create_user() throws Exception {
        given:
            String accessToken = getUserAccessToken()
        when:
            UserDetail user = getUserWithGroup()
            def result = performPostToUser(accessToken, user)
        then:
            passwordFlow(user.getEmail(), user.getPassword())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.access_token', is(notNullValue())))
            result.andExpect(status().isCreated())
                .andExpect(header().string("Location", is(notNullValue())))

    }

    void should_update_user_me() throws Exception {
        given:
            String accessToken = getUserAccessToken()
            UserDetail user = getUserWithGroup()
        when:
            def result = this.mvc.perform(
                post(USER_ENDPOINT, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user)))

            MvcResult mvcResult = passwordFlow(user.getEmail(), user.getPassword())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.access_token', is(notNullValue())))
                .andReturn()

            String userAccessToken = getAccessToken(mvcResult)

            user.setPassword("otherpass")
            user.setEmail(randomAlphabetic(7)+"@gmail.com")

            this.mvc.perform(put(USER_ME_ENDPOINT, userAccessToken).contentType(MediaType.APPLICATION_JSON).content(toJson(user))).andExpect(status().isNoContent())

        then:
            passwordFlow(user.getEmail(), user.getPassword())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.access_token', is(notNullValue())))
            result.andExpect(status().isCreated())

    }

    void should_update_user() throws Exception {
        given:
            String getUserAccessToken = getUserAccessToken()

            UserDetail user = getUserWithGroup()
        when:
            MockHttpServletResponse result = this.mvc.perform(
                post(USER_ENDPOINT, getUserAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(user)))
                .andExpect(status().isCreated()).andReturn().getResponse()

            ObjectMapper mapper = new ObjectMapper()
            user.setId(mapper.readValue(result.getContentAsString(), UserDetail.class).getId())
        then:
            passwordFlow(user.getEmail(), user.getPassword())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.access_token', is(notNullValue())))

            user.setPassword("otherpass")
            user.setEmail(randomAlphabetic(7)+"@gmail.com")

            this.mvc.perform(put("/users/{id}?access_token={access_token}", user.getId(), getUserAccessToken).contentType(MediaType.APPLICATION_JSON).content(toJson(user))).andExpect(status().isNoContent())

            passwordFlow(user.getEmail(), user.getPassword())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.access_token', is(notNullValue())))

    }

    void should_not_allow_duplicated_users() {
        given:
            String accessToken = getUserAccessToken()

            UserDetail user = getUserWithoutGroup()
        when:
            expectUserCreated(accessToken, user)
        then:
            performPostToUser(accessToken, user).andExpect(status().isConflict())
    }

    void should_not_allow_client_authentication_on_user_me() throws Exception {
        given:
            UserDetail user = getUserWithoutGroup()
            String accessToken = getClientAccessToken()
        when:
            def result = this.mvc.perform(put(USER_ME_ENDPOINT, accessToken).contentType(MediaType.APPLICATION_JSON).content(toJson(user)))
        then:
            result.andExpect(status().isForbidden())
    }

    void 'should return groups and authorities when get profile'() {
        given:
            UserDetail user = getUserWithGroup()

            String accessToken = getUserAccessToken()

            user.getGroups().find().setId('1')

            MvcResult mvcResult = expectUserCreatedAndPasswordFlowOk(accessToken, user)
            String userAccessToken = getAccessToken(mvcResult)
        when:
            def result = this.mvc.perform(
                get(PROFILE_ENDPOINT, userAccessToken))


        then:
            result.andExpect(status().isOk())
                .andExpect(jsonPath('$.groups',is(notNullValue())))
                .andExpect(jsonPath('$.groups[0].authorities', is(notNullValue())))
    }

    void 'should update my password'() {
        given:
        UserDetail user = getUserWithGroup()

        MvcResult mvcResult = expectUserCreatedAndPasswordFlowOk(getUserAccessToken(), user)
        String userAccessToken = getAccessToken(mvcResult)

        NewPassword newPassword = new NewPassword() {{
            password = "123123"
        }}

        when:
        def result = this.mvc.perform(
                put("/users/me/password?access_token={access_token}", userAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(newPassword)))

        then:
        result.andExpect(status().isNoContent())
    }

    void 'should return groups authorities inline when get profile'() {
        given:
            UserDetail user = getUserWithGroup()

            String accessToken = getUserAccessToken()

            user.getGroups().find().setId('1')

            this.mvc.perform(
                    post(USER_ENDPOINT, accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(user)))
                    .andExpect(status().isCreated())

            MvcResult mvcResult = passwordFlow(user.getEmail(), user.getPassword())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath('$.access_token', is(notNullValue())))
                    .andReturn()

            String userAccessToken = getAccessToken(mvcResult)
        when:
            def result = this.mvc.perform(get(PROFILE_ENDPOINT, userAccessToken))


        then:
            result.andExpect(status().isOk())
                .andExpect(jsonPath('$.groups',is(notNullValue())))
                .andExpect(jsonPath('$.groupsAuthorities', is(notNullValue())))
    }

    void 'when create users with unknown group should not return groups'() {
        given:
            UserDetail user = getUserWithGroup()

            String accessToken = getUserAccessToken()

            MvcResult mvcResult = expectUserCreatedAndPasswordFlowOk(accessToken, user)

            String userAccessToken = getAccessToken(mvcResult)
        when:
            def result = this.mvc.perform(get(PROFILE_ENDPOINT, userAccessToken))
        then:
            result.andExpect(status().isOk()).andExpect(jsonPath('$.groups', hasSize(0)))
    }

    private MvcResult expectUserCreatedAndPasswordFlowOk(String accessToken, UserDetail user) {
        expectUserCreated(accessToken, user)
        MvcResult mvcResult = passwordFlow(user.getEmail(), user.getPassword()).andExpect(status().isOk()).andExpect(jsonPath('$.access_token', is(notNullValue()))).andReturn()
        mvcResult
    }

    void 'given known group and user should be associate user with group'() {
        given:
            String accessToken = getUserAccessToken()
            String groupId = '1'
            this.mvc.perform(put(GROUP_ENDPOINT, groupId, accessToken).contentType(MediaType.APPLICATION_JSON).content("""["1", "2"]"""))

        when:
            def result = this.mvc.perform(get("$GROUP_ENDPOINT&page=1&size=20", groupId, accessToken))

        then:
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', hasSize(2)))
                .andExpect(jsonPath('$.items[0].name', is(notNullValue())))

    }
   
    void 'should return error when associate groups without list'() {
        given:
            String accessToken = getUserAccessToken()
            String userId = '1'
        when:
            def result = this.mvc.perform(
                put("/users/{userId}/groups?access_token={access_token}", userId, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""""""))

        then:
            result.andExpect(status().isBadRequest())
    }

    void 'when search user by params should return'() {
        given:
            String accessToken = getUserAccessToken()

            UserDetail user = getUserWithGroup()

            expectUserCreated(accessToken, user)
        when:
            def name = user.getName()
            def result = this.mvc.perform(
                get("/users?name={name}&access_token={access_token}", name, accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
        then:
            result.andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath('$.items', is(notNullValue())))
                .andExpect(jsonPath('$.total', equalTo(1)))
                .andExpect(jsonPath('$.items[0].name', equalTo(name)))
    }

    void 'should return all me user credit cards'(){

        given:
        UserDetail user = fixtureCreator.createContractorUser()
        PersonCreditCard userCreditCard = Fixture.from(PersonCreditCard).gimme("valid", new Rule(){{
            add("person", user.getContractor().getPerson())
        }})
        userCreditCardService.create(userCreditCard)
        String accessToken = getUserAccessToken(user.email, user.password)

        when:
        def result = this.mvc.perform(get('/users/me/credit-cards?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].id', is(notNullValue())))
    }

    void 'known me user credit card should be found'(){

        given:
        UserDetail user = fixtureCreator.createContractorUser()
        PersonCreditCard userCreditCard = Fixture.from(PersonCreditCard).gimme("valid", new Rule(){{
            add("person", user.getContractor().getPerson())

        }})
        userCreditCardService.create(userCreditCard)
        String accessToken = getUserAccessToken(user.email, user.password)
        def id = userCreditCard.id

        when:
        def result = this.mvc.perform(get('/users/me/credit-cards/{id}?access_token={access_token}', id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.expirationMonth', is(notNullValue())))

    }

    void 'given known user should reset password'(){

        given:
        UserDetail user = fixtureCreator.createUser()
        String accessToken = getClientAccessToken()
        RequestOrigin origin = RequestOrigin.BACKOFFICE
        when:
        def result = this.mvc.perform(get('/users/password/?access_token={access_token}&email={email}&origin={origin}', accessToken, user.email, origin.name())
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isNoContent())

    }

    void 'known me user credit card should be deleted'(){

        given:
        UserDetail user = fixtureCreator.createContractorUser()
        PersonCreditCard userCreditCard = Fixture.from(PersonCreditCard).gimme("valid", new Rule(){{
            add("person", user.getContractor().getPerson())
        }})
        userCreditCardService.create(userCreditCard)
        String accessToken = getUserAccessToken(user.email, user.password)
        def id = userCreditCard.id

        when:
        def result = this.mvc.perform(delete('/users/me/credit-cards/{id}?access_token={access_token}', id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))

        then:
        result.andExpect(status().isNoContent())

    }

    MockHttpServletRequestBuilder postToUserEndpoint(String accessToken, UserDetail user) {
        post(USER_ENDPOINT, accessToken).contentType(MediaType.APPLICATION_JSON).content(toJson(user))
    }

    UserDetail getUserWithGroup() {
        Fixture.from(UserDetail.class).gimme("with-group")
    }

    UserDetail getUserWithoutGroup() {
        Fixture.from(UserDetail.class).gimme("without-group")
    }

    ResultActions performPostToUser(String accessToken, UserDetail user) {
        this.mvc.perform(postToUserEndpoint(accessToken, user))
    }

    ResultActions expectUserCreated(String accessToken, UserDetail user) {
        performPostToUser(accessToken, user).andExpect(status().isCreated())
    }

}