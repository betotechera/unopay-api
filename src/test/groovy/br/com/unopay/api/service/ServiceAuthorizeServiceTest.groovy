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
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.springframework.beans.factory.annotation.Autowired

class ServiceAuthorizeServiceTest  extends SpockApplicationTests {

    @Autowired
    ServiceAuthorizeService service

    @Autowired
    SetupCreator setupCreator

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
        userUnderTest = setupCreator.createUser()
        establishmentUnderTest = setupCreator.createEstablishment()
    }

    void 'new service authorize should be created'(){
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        when:
        def created  = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.id != null
    }

    void 'service authorize should be create with current user'(){
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        when:
        def created  = service.create(userUnderTest.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.user.id == userUnderTest.id
    }

    void 'when user is establishment type then the establishment should be the user establishment'(){
        given:
        def userEstablishment = setupCreator.createEstablishmentUser()
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()

        when:
        def created = service.create(userEstablishment.email, serviceAuthorize)
        def result = service.findById(created.id)

        then:
        assert result.establishment.id == userEstablishment.establishment.id
    }

    void 'when user is not establishment type then the establishment document should be required'(){
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            establishment.person.document = null
        }

        when:
        service.create(userUnderTest.email, serviceAuthorize)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'ESTABLISHMENT_DOCUMENT_REQUIRED'
    }

    void 'given a unknown establishment when user is not establishment type should be authorized'(){
        given:
        ServiceAuthorize serviceAuthorize = createServiceAuthorize()
        serviceAuthorize.with {
            establishment.person.document.number = ''
        }

        when:
        service.create(userUnderTest.email, serviceAuthorize)

        then:
        def ex = thrown(NotFoundException)
        assert ex.errors.first().logref == 'ESTABLISHMENT_NOT_FOUND'
    }

    private ServiceAuthorize createServiceAuthorize() {
        return Fixture.from(ServiceAuthorize.class).gimme("valid").with {
            contract = contractUnderTest
            contractor = contractorUnderTest
            event = eventUnderTest
            contractorInstrumentCredit = instrumentCreditUnderTest
            establishment = establishmentUnderTest
            it
        }
    }


}
