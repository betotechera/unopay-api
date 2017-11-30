package br.com.unopay.api.uaa.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.uaa.AuthServerApplicationTests
import br.com.unopay.api.uaa.model.Group
import br.com.unopay.api.uaa.repository.GroupRepository
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasSize
import static org.hamcrest.core.Is.is
import static org.hamcrest.core.IsNull.notNullValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class GroupControllerTest extends AuthServerApplicationTests {
    private static final String AUTHORITIES_ENDPOINT = '/groups/{groupId}/authorities?access_token={access_token}'
    private static final String GROUP_ENDPOINT = '/groups?access_token={access_token}'
    private static final String GROUP_ID_ENDPOINT = '/groups/{id}?access_token={access_token}'
    private static final String MEMBERS_ENDPOINT = '/groups/{groupId}/members?access_token={access_token}'
    private static final String DEFAULT_GROUP_ID = '1'

    @Autowired
    private GroupRepository groupRepository

    
    void 'should create group'() {
        given:
            String accessToken = getUserAccessToken()
        when:
            def result = this.mvc.perform(postGroups(accessToken, getGroup()))
        then:
            result.andExpect(status().isCreated()).andExpect(header().string("Location", is(notNullValue())))
    }

    MockHttpServletRequestBuilder postGroups(String accessToken, Group group) {
        post(GROUP_ENDPOINT, accessToken).contentType(MediaType.APPLICATION_JSON).content(toJson(group))
    }

    void 'known group should be deleted'() {
        given:
            String accessToken = getUserAccessToken()
            def mvcResult = this.mvc.perform(postGroups(accessToken, getGroup())).andReturn()
            def location = getLocationHeader(mvcResult)
            def id = extractId(location)
        when:
            def result = this.mvc.perform(delete(GROUP_ID_ENDPOINT,id, accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
            result.andExpect(status().isNoContent())
    }

    String extractId(String location) {
        location.replaceAll('/groups/', "")
    }

    String getLocationHeader(MvcResult mvcResult) {
        mvcResult.getResponse().getHeader("Location")
    }

    void 'known group should be found'() {
        given:
            String accessToken = getUserAccessToken()
            Group group = getGroup()
            def mvcResult = this.mvc.perform(postGroups(accessToken, group)).andReturn()
            def location = getLocationHeader(mvcResult)
            def id = extractId(location)
        when:
            def result = this.mvc.perform(get(GROUP_ID_ENDPOINT,id, accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
            result.andExpect(status().isOk())
                .andExpect(jsonPath('$.name', is(equalTo(group.name))))
                .andExpect(jsonPath('$.description', is(equalTo(group.description))))
    }

    void 'known groups should be found when find all'() {
        given:
            groupRepository.deleteAll()
            String accessToken = getUserAccessToken()
            this.mvc.perform(postGroups(accessToken, getGroup()))
            this.mvc.perform(post(GROUP_ENDPOINT, accessToken).contentType(MediaType.APPLICATION_JSON).content(toJson(group.with { name = 'temp'; it })))
        when:
            def result = this.mvc.perform(get("$GROUP_ENDPOINT&page=1&size=2",accessToken).contentType(MediaType.APPLICATION_JSON))
        then:
            result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', hasSize(2)))
                .andExpect(jsonPath('$.total', is(equalTo(2))))
                .andExpect(jsonPath('$.items[0].name', is(notNullValue())))
    }
   
    void 'given known group and user should be add member to group'() {
        given:
            String accessToken = getUserAccessToken()
            this.mvc.perform(put(MEMBERS_ENDPOINT, DEFAULT_GROUP_ID, accessToken).contentType(MediaType.APPLICATION_JSON).content("""["1", "2"]"""))
        when:
            def result = this.mvc.perform(get("$MEMBERS_ENDPOINT&page=1&size=20", DEFAULT_GROUP_ID, accessToken))
        then:
            result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', hasSize(2)))
                .andExpect(jsonPath('$.total', is(equalTo(2))))
                .andExpect(jsonPath('$.items[0].id', is(notNullValue())))
    }
   
    void 'given known group and authority should be add authority to group'() {
        given:
            String accessToken = getUserAccessToken()
            this.mvc.perform(put(AUTHORITIES_ENDPOINT, DEFAULT_GROUP_ID, accessToken).contentType(MediaType.APPLICATION_JSON).content("""["ROLE_ADMIN", "ROLE_USER"]"""))
        when:
            def result = this.mvc.perform(get("$AUTHORITIES_ENDPOINT&page=1&size=20", DEFAULT_GROUP_ID, accessToken))
        then:
            result.andExpect(status().isOk())
                .andExpect(jsonPath('$.items', is(notNullValue())))
                .andExpect(jsonPath('$.items[0].name', is(notNullValue())))
    }
   
    void 'should return error when add authorities without list'() {
        given:
            String accessToken = getUserAccessToken()
        when:
            def result = this.mvc.perform(put(AUTHORITIES_ENDPOINT, DEFAULT_GROUP_ID, accessToken).contentType(MediaType.APPLICATION_JSON).content(""""""))
        then:
            result.andExpect(status().isBadRequest())
    }
   
    void 'should return error when add members without list'() {
        given:
            String accessToken = getUserAccessToken()
        when:
            def result = this.mvc.perform(put(MEMBERS_ENDPOINT, DEFAULT_GROUP_ID, accessToken).contentType(MediaType.APPLICATION_JSON).content(""""""))
        then:
            result.andExpect(status().isBadRequest())
    }

    Group getGroup() {
        Fixture.from(Group.class).gimme("valid")
    }
}
