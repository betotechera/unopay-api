package br.com.unopay.api.controller

import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.model.BatchClosing
import br.com.unopay.api.uaa.AuthServerApplicationTests
import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.core.Is.is
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class BatchClosingControllerTest extends AuthServerApplicationTests {

    @Autowired
    FixtureCreator fixtureCreator


    void 'valid batchClosing should be created'() {
        given:
        String accessToken = getUserAccessToken()
        BatchClosing batchClosing = fixtureCreator.createBatchToPersist()

        when:
        def result = this.mvc.perform(post('/batch-closings?access_token={access_token}', accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(batchClosing)))
        then:
        result.andExpect(status().isCreated())
    }

    void 'known batchClosing should be deleted'() {
        given:
        String accessToken = getUserAccessToken()
        BatchClosing batchClosing = fixtureCreator.createBatchClosing()

        def id = batchClosing.id
        when:
        def result = this.mvc.perform(delete('/batch-closings/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'known batchClosings should be found'() {
        given:
        String accessToken = getUserAccessToken()
        BatchClosing batchClosing = fixtureCreator.createBatchClosing()
        def id = batchClosing.id
        when:
        def result = this.mvc.perform(get('/batch-closings/{id}?access_token={access_token}',id, accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.closingDateTime', is(notNullValue())))
    }

}
