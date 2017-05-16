package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.Event
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.Contract
import br.com.unopay.api.model.ContractorInstrumentCredit
import br.com.unopay.api.model.ServiceAuthorize
import br.com.unopay.api.uaa.model.UserDetail
import br.com.unopay.api.uaa.repository.UserDetailRepository
import org.springframework.beans.factory.annotation.Autowired

class ServiceAuthorizeServiceTest  extends SpockApplicationTests {

    @Autowired
    ServiceAuthorizeService service

    @Autowired
    SetupCreator setupCreator

    @Autowired
    UserDetailRepository userDetailRepository

    @Autowired
    ContractorInstrumentCreditService contractorInstrumentCreditService

    Contractor contractorUnderTest
    Contract contractUnderTest
    UserDetail userUnderTest
    Event eventUnderTest
    ContractorInstrumentCredit instrumentCreditUnderTest
    Establishment establishmentUnderTest

    def setup(){
        instrumentCreditUnderTest = setupCreator.createContractorInstrumentCredit()
        contractorInstrumentCreditService.insert(instrumentCreditUnderTest.paymentInstrumentId, instrumentCreditUnderTest)
        contractorUnderTest = instrumentCreditUnderTest.contract.contractor
        contractUnderTest = instrumentCreditUnderTest.contract
        eventUnderTest = setupCreator.createEvent(contractUnderTest.serviceType.find())
        userUnderTest = userDetailRepository.findById('1')
        establishmentUnderTest = setupCreator.createEstablishment()
    }

    void 'new service authorize should be created'(){
        given:
        ServiceAuthorize serviceAuthorize = Fixture.from(ServiceAuthorize.class).gimme("valid")
        serviceAuthorize.with {
            contract = contractUnderTest
            contractor = contractorUnderTest
            event = eventUnderTest
            user = userUnderTest
            contractorInstrumentCredit = instrumentCreditUnderTest
            establishment = establishmentUnderTest
        }

        when:
        def created  = service.save(serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.id != null
    }
}
