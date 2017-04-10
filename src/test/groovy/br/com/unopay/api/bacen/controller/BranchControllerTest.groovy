package br.com.unopay.api.bacen.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.model.Branch
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.uaa.AuthServerApplicationTests
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class BranchControllerTest extends AuthServerApplicationTests {

    @Autowired
    SetupCreator setupCreator

    Establishment headOfficeUnderTest

    void setup(){
        headOfficeUnderTest = setupCreator.createHeadOffice()
    }

    void 'valid branch should be created'() {
        given:
        String accessToken = getClientAccessToken()
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }

        when:
        def result = this.mvc.perform(post('/branchs?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(branch)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known branch should be updated'() {
        given:
        String accessToken = getClientAccessToken()
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        def mvcResult = this.mvc.perform(post('/branchs?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(branch))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(put('/branchs/{id}?access_token={access_token}',id, accessToken)
                .content(toJson(branch.with { id= extractId(location);  tax = 0.3d ; person.id = '1'; person.document.number = '11114444555786'; it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known branch should be deleted'() {
        given:
        String accessToken = getClientAccessToken()
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        def mvcResult = this.mvc.perform(post('/branchs?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(branch))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(delete('/branchs/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known branchs should be found'() {
        given:
        String accessToken = getClientAccessToken()
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        def mvcResult = this.mvc.perform(post('/branchs?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(branch)))
                .andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(get('/branchs/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.tax', is(notNullValue())))
    }

    void 'all branchs should be found'() {
        given:
        String accessToken = getClientAccessToken()
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        this.mvc.perform(post('/branchs?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(branch)))

        when:
        def result = this.mvc.perform(get('/branchs?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].tax', is(notNullValue())))
    }


    private String extractId(String location) {
        location.replaceAll('/branchs/', "")
    }

    private String getLocationHeader(MvcResult mvcResult) {
        mvcResult.getResponse().getHeader("Location")
    }
}
