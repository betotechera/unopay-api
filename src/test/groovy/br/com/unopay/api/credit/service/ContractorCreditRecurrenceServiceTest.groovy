package br.com.unopay.api.credit.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.credit.model.ContractorCreditRecurrence
import org.springframework.beans.factory.annotation.Autowired

class ContractorCreditRecurrenceServiceTest  extends SpockApplicationTests {

    @Autowired
    private ContractorCreditRecurrenceService service
    @Autowired
    private FixtureCreator fixtureCreator

    def 'given a valid recurrence should be created'(){
        given:
        def contract = fixtureCreator.createPersistedContract()
        ContractorCreditRecurrence recurrence = Fixture.from(ContractorCreditRecurrence)
                                                                            .gimme("valid", new Rule(){{
            add("contract", contract)
            add("hirer", contract.hirer)
        }})

        when:
        ContractorCreditRecurrence created = service.save(recurrence)
        ContractorCreditRecurrence found = service.findById(created.id)

        then:
        found
    }

    def 'given a recurrence  without created date should be created'(){
        given:
        def contract = fixtureCreator.createPersistedContract()
        ContractorCreditRecurrence recurrence = Fixture.from(ContractorCreditRecurrence)
                .gimme("valid", new Rule(){{
            add("contract", contract)
            add("hirer", contract.hirer)
            add("createdDateTime", null)
        }})

        when:
        ContractorCreditRecurrence created = service.create(recurrence)
        ContractorCreditRecurrence found = service.findById(created.id)

        then:
        timeComparator.compare(found.createdDateTime, new Date()) == 0
    }


}
