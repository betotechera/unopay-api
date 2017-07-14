package br.com.unopay.api.bacen.controller

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.bacen.model.Branch
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.uaa.AuthServerApplicationTests
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MvcResult
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class BranchControllerTest extends AuthServerApplicationTests {

    @Autowired
    FixtureCreator fixtureCreator

    Establishment headOfficeUnderTest

    void setup(){
        flyway.clean()
        flyway.migrate()
        headOfficeUnderTest = fixtureCreator.createHeadOffice()
    }

    void 'valid branch should be created'() {
        given:
        String accessToken = getClientAccessToken()
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }

        when:
        def result = this.mvc.perform(post('/branches?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(branch)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known branch should be updated'() {
        given:
        String accessToken = getClientAccessToken()
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        def mvcResult = this.mvc.perform(post('/branches?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(branch))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(put('/branches/{id}?access_token={access_token}',id, accessToken)
                .content(toJson(branch.with { id= extractId(location);  fee = 0.3d ; person.id = '1'; person.document.number = '11114444555786'; it }))
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known branch should be deleted'() {
        given:
        String accessToken = getClientAccessToken()
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        def mvcResult = this.mvc.perform(post('/branches?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(branch))).andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(delete('/branches/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known branches should be found'() {
        given:
        String accessToken = getClientAccessToken()
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        def mvcResult = this.mvc.perform(post('/branches?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(branch)))
                .andReturn()

        def location = getLocationHeader(mvcResult)
        def id = extractId(location)
        when:
        def result = this.mvc.perform(get('/branches/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.fee', is(notNullValue())))
    }

    void 'all branches should be found'() {
        given:
        String accessToken = getClientAccessToken()
        Branch branch = Fixture.from(Branch.class).gimme("valid").with { headOffice = headOfficeUnderTest; it }
        this.mvc.perform(post('/branches?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(branch)))

        when:
        def result = this.mvc.perform(get('/branches?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.total', is(equalTo(1))))
                .andExpect(MockMvcResultMatchers.jsonPath('$.items[0].fee', is(notNullValue())))
    }


    private String extractId(String location) {
        location.replaceAll('/branches/', "")
    }

}
