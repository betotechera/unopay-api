package br.com.unopay.api.controller

import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.credit.model.ContractorInstrumentCredit
import br.com.unopay.api.credit.service.ContractorInstrumentCreditService
import br.com.unopay.api.model.validation.group.Views
import br.com.unopay.api.uaa.AuthServerApplicationTests
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ContractorInstrumentCreditControllerTest extends AuthServerApplicationTests {

    @Autowired
    FixtureCreator fixtureCreator

    @Autowired
    ContractorInstrumentCreditService service

    void 'valid payment instrument credit should be created'() {
        given:
        String accessToken = getUserAccessToken()

        ContractorInstrumentCredit credit = fixtureCreator.instrumentCredit()
        String instrumentId = credit?.paymentInstrument?.id

        when:
        def result = this.mvc.perform(
                            post('/payment-instruments/{instrumentId}/credits?access_token={access_token}',
                                                        instrumentId, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonFromView(credit,Views.ContractorInstrumentCredit.Detail.class)))
        then:
        result.andExpect(status().isCreated())
    }


    void 'valid payment instrument credit should be canceled'() {
        given:
        String accessToken = getUserAccessToken()

        ContractorInstrumentCredit credit = fixtureCreator.instrumentCredit()

        String instrumentId = credit?.paymentInstrument?.id
        def inserted = service.insert(instrumentId, credit)
        def id = inserted.id
        when:
        def result = this.mvc.perform(
                delete('/payment-instruments/{instrumentId}/credits/{id}?access_token={access_token}',
                        instrumentId, id, accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }

    void 'valid contract with instrument credits then cancel credits should be canceled'() {
        given:
        String accessToken = getUserAccessToken()

        ContractorInstrumentCredit credit = fixtureCreator.instrumentCredit()

        String instrumentId = credit?.paymentInstrument?.id
        def inserted = service.insert(instrumentId, credit)
        def contractId = inserted.contract.id
        when:
        def result = this.mvc.perform(
                delete('/contracts/{contractId}/payment-instruments/credits?access_token={access_token}',
                         contractId, accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
        then:
        result.andExpect(status().isNoContent())
    }
}
