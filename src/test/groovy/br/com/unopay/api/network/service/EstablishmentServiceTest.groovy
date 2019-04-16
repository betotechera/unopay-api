package br.com.unopay.api.network.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.geo.service.GeoService
import br.com.unopay.api.network.model.AccreditedNetwork
import br.com.unopay.api.network.model.Branch
import br.com.unopay.api.network.model.Establishment
import br.com.unopay.api.network.model.EstablishmentEvent
import br.com.unopay.api.bacen.model.RecurrencePeriod
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.job.BatchClosingJob
import br.com.unopay.api.job.UnopayScheduler
import br.com.unopay.api.network.service.BranchService
import br.com.unopay.api.network.service.EstablishmentService
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.ForbiddenException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean

class EstablishmentServiceTest  extends SpockApplicationTests {

    @Autowired
    EstablishmentService service

    @Autowired
    BranchService branchService

    GeoService geoService = Mock(GeoService)

    @Autowired
    FixtureCreator fixtureCreator

    AccreditedNetwork networkUnderTest

    UnopayScheduler schedulerMock = Mock(UnopayScheduler)

    void setup(){
        networkUnderTest = fixtureCreator.createNetwork()
        service.scheduler =  schedulerMock
        service.setGeoService(geoService)
    }

    def 'a valid establishment should be schedule closing job'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid", new Rule(){{
            add("network", networkUnderTest)
            add("checkout.period", RecurrencePeriod.BIWEEKLY)
        }})

        when:
        service.create(establishment)

        then:
        1 * schedulerMock.schedule(_,RecurrencePeriod.BIWEEKLY.pattern, BatchClosingJob.class)
    }

    def 'when creating a establishment should be geo resolve it address'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid", new Rule(){{
            add("network", networkUnderTest)
        }})

        when:
        service.create(establishment)

        then:
        1 * geoService.defineAddressLatLong(establishment)
    }

    def 'a valid establishment should be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                        .gimme("valid").with { network = networkUnderTest; it }
        when:
        Establishment created = service.create(establishment)

        then:
        created != null
    }

    def 'a valid establishment when update should schedule closing job'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid", new Rule(){{
            add("network", networkUnderTest)
            add("checkout.period", RecurrencePeriod.DAILY)
        }})
        Establishment created = service.create(establishment)

        def newField = "teste"
        establishment.technicalContact = newField

        when:
        service.update(created.id, establishment)

        then:
        1 * schedulerMock.schedule(_,RecurrencePeriod.DAILY.pattern, BatchClosingJob.class)
    }

    def 'a valid establishment should be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                            .gimme("valid").with { network = networkUnderTest; it }
        Establishment created = service.create(establishment)

        def newField = "teste"
        establishment.technicalContact = newField

        when:
        service.update(created.id, establishment)
        Establishment result = service.findById(created.id)

        then:
        result.technicalContact == newField
    }

    def 'a establishment person should be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                .gimme("valid").with { network = networkUnderTest; it }
        Establishment created = service.create(establishment)

        def newField = "teste"
        establishment.person.name = newField

        when:
        service.update(created.id, establishment)
        Establishment result = service.findById(created.id)

        then:
        result.person.name == newField
    }

    def 'a establishment contact should be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                .gimme("valid").with { network = networkUnderTest; it }
        Establishment created = service.create(establishment)

        def newField = "teste"
        establishment.operationalContact.name = newField

        when:
        service.update(created.id, establishment)
        Establishment result = service.findById(created.id)

        then:
        result.operationalContact.name == newField
    }

    def 'a establishment network should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                .gimme("valid").with { network = networkUnderTest; it }
        Establishment created = service.create(establishment)

        def newField = 10.0
        establishment.network.merchantDiscountRate = newField

        when:
        service.update(created.id, establishment)
        Establishment result = service.findById(created.id)

        then:
        result.network.merchantDiscountRate != newField
    }

    def 'a establishment bank account should be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                .gimme("valid").with { network = networkUnderTest; it }
        Establishment created = service.create(establishment)

        def newField = "teste"
        establishment.bankAccount.agency = newField

        when:
        service.update(created.id, establishment)
        Establishment result = service.findById(created.id)

        then:
        result.bankAccount.agency == newField
    }

    def 'a valid establishment with unknown network should not be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        establishment.network.id = ''

        when:
        service.create(establishment)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ACCREDITED_NETWORK_NOT_FOUND'
    }

    def 'a valid establishment without network id should not be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        establishment.network.id = null
        when:
        service.create(establishment)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'ACCREDITED_NETWORK_ID_REQUIRED'
    }

    def 'a valid establishment without bank account should not be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                        .gimme("valid").with { network = networkUnderTest; it }
        establishment.bankAccount = null
        when:
        service.create(establishment)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'BANK_ACCOUNT_REQUIRED'
    }

    def 'a valid establishment without bank account id should be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                        .gimme("valid").with { network = networkUnderTest; it }
        establishment.bankAccount.id = null
        when:
        def created = service.create(establishment)
        def result = service.findById(created.id)
        then:
        result != null
    }

    def 'a valid establishment should be create to a logged network'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        establishment.network = null
        when:
        def created = service.create(establishment, networkUnderTest)
        def result = service.findById(created.id)
        then:
        result.network.id == networkUnderTest.id
    }

    def 'a valid establishment should not be create to a unknown logged network'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        establishment.network = null
        when:
        service.create(establishment, new AccreditedNetwork())

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ACCREDITED_NETWORK_NOT_FOUND'
    }

    def 'a valid establishment without bank account should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                        .gimme("valid").with { network = networkUnderTest; it }
        def created = service.create(establishment)
        establishment.bankAccount = null
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'BANK_ACCOUNT_REQUIRED'
    }

    def 'a valid establishment should not be updated with a unknown logged network'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")

        when:
        service.update(establishment.id, establishment, new AccreditedNetwork())

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ACCREDITED_NETWORK_NOT_FOUND'
    }

    def 'a valid establishment should not be updated with a different network'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        establishment.network = fixtureCreator.createNetwork()
        when:
        service.update(establishment.id, establishment, networkUnderTest)

        then:
        def ex = thrown(ForbiddenException)
        ex.errors.find().logref == 'ESTABLISHMENT_BELONG_TO_ANOTHER_NETWORK'
    }

    def 'a valid establishment should not be updated the network'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")
        def newNetwork = fixtureCreator.createNetwork()
        establishment.network = newNetwork
        when:
        service.update(establishment.id, establishment, newNetwork)

        then:
        def ex = thrown(ForbiddenException)
        ex.errors.find().logref == 'ESTABLISHMENT_BELONG_TO_ANOTHER_NETWORK'
    }

    def 'given a logged network different than the establishment network should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid")

        when:
        service.update(establishment.id, establishment, fixtureCreator.createNetwork())

        then:
        def ex = thrown(ForbiddenException)
        ex.errors.find().logref == 'ESTABLISHMENT_BELONG_TO_ANOTHER_NETWORK'
    }

    def 'a valid establishment without bank account id should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                            .gimme("valid").with { network = networkUnderTest; it }
        def created = service.create(establishment)
        establishment.bankAccount.id = null

        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'BANK_ACCOUNT_ID_REQUIRED'
    }

    def 'a valid establishment with unknown bank account id should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                .gimme("valid").with { network = networkUnderTest; it }
        def created = service.create(establishment)
        establishment.bankAccount.id = ''

        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'BANK_ACCOUNT_NOT_FOUND'
    }

    def 'a valid establishment with unknown operational contact should not be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                            .gimme("valid").with { network = networkUnderTest; it }
        establishment.operationalContact.id = ''
        when:
        service.create(establishment)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'CONTACT_NOT_FOUND'
    }

    def 'a valid establishment without operational contact id should be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                        .gimme("valid").with { network = networkUnderTest; it }
        establishment.operationalContact.id = null

        when:
        def created = service.create(establishment)
        def result = service.findById(created.id)

        then:
        result != null
    }

    def 'a valid establishment with unknown administrative contact should not be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                            .gimme("valid").with { network = networkUnderTest; it }
        establishment.administrativeContact.id = ''
        when:
        service.create(establishment)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'CONTACT_NOT_FOUND'
    }

    def 'a valid establishment without administrative contact id should be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                        .gimme("valid").with { network = networkUnderTest; it }
        establishment.administrativeContact.id = null

        when:
        def created = service.create(establishment)
        def result = service.findById(created.id)

        then:
        result != null
    }

    def 'a valid establishment with unknown financier contact should not be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                            .gimme("valid").with { network = networkUnderTest; it }
        establishment.financierContact.id = ''
        when:
        service.create(establishment)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'CONTACT_NOT_FOUND'
    }

    def 'a valid establishment without financier contact id should be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                        .gimme("valid").with { network = networkUnderTest; it }
        establishment.financierContact.id = null

        when:
        def created = service.create(establishment)
        def result = service.findById(created.id)

        then:
        result != null
    }

    def 'a valid establishment without administrative contact should be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                .gimme("valid").with { network = networkUnderTest; it }
        establishment.administrativeContact = null
        when:
        def created = service.create(establishment)

        then:
        created
    }

    def 'a valid establishment without operational contact should be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                .gimme("valid").with { network = networkUnderTest; it }
        establishment.operationalContact = null
        when:
        def created = service.create(establishment)

        then:
        created
    }

    def 'a valid establishment without financier contact should not be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                .gimme("valid").with { network = networkUnderTest; it }
        establishment.financierContact = null
        when:
        service.create(establishment)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'CONTACT_REQUIRED'
    }

    def 'a valid establishment without network should not be created'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        establishment.network = null
        when:
        service.create(establishment)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'ACCREDITED_NETWORK_REQUIRED'
    }

    def 'a valid establishment with unknown network should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                        .gimme("valid").with { network = networkUnderTest; it }
        def created = service.create(establishment)
        establishment.network.id = ''
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ACCREDITED_NETWORK_NOT_FOUND'
    }

    def 'a valid establishment without network id should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                        .gimme("valid").with { network = networkUnderTest; it }
        def created = service.create(establishment)
        establishment.network.id = null
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'ACCREDITED_NETWORK_ID_REQUIRED'
    }

    def 'a valid establishment without network should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                        .gimme("valid").with { network = networkUnderTest; it }
        def created = service.create(establishment)
        establishment.network = null
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'ACCREDITED_NETWORK_REQUIRED'
    }

    def 'a valid establishment without administrative contatct should be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                .gimme("valid").with { network = networkUnderTest; it }
        def created = service.create(establishment)
        establishment.administrativeContact = null
        when:
        service.update(created.id, establishment)

        then:
        notThrown(UnprocessableEntityException)
    }

    def 'a valid establishment without financier contact should be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                .gimme("valid").with { network = networkUnderTest; it }
        def created = service.create(establishment)
        establishment.financierContact = null
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'CONTACT_REQUIRED'
    }

    def 'a valid establishment without operational contact should be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                .gimme("valid").with { network = networkUnderTest; it }
        def created = service.create(establishment)
        establishment.operationalContact = null
        when:
        service.update(created.id, establishment)

        then:
        notThrown(UnprocessableEntityException)
    }

    def 'a valid establishment with unknown operational contact should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                        .gimme("valid").with { network = networkUnderTest; it }
        def created = service.create(establishment)
        establishment.operationalContact.id = ''
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'CONTACT_NOT_FOUND'
    }

    def 'a valid establishment without operational contact id should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                            .gimme("valid").with { network = networkUnderTest; it }
        def created = service.create(establishment)
        establishment.operationalContact.id = null
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'CONTACT_NOT_FOUND'
    }

    def 'a valid establishment with unknown administrative contact should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                        .gimme("valid").with { network = networkUnderTest; it }
        def created = service.create(establishment)
        establishment.administrativeContact.id = ''
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'CONTACT_NOT_FOUND'
    }

    def 'a valid establishment without administrative contact id should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                            .gimme("valid").with { network = networkUnderTest; it }
        def created = service.create(establishment)
        establishment.administrativeContact.id = null
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'CONTACT_NOT_FOUND'
    }

    def 'a valid establishment with unknown financier contact should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                        .gimme("valid").with { network = networkUnderTest; it }
        def created = service.create(establishment)
        establishment.financierContact.id = ''
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'CONTACT_NOT_FOUND'
    }

    def 'a valid establishment without financier contact id should not be updated'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                        .gimme("valid").with { network = networkUnderTest; it }
        def created = service.create(establishment)
        establishment.financierContact.id = null
        when:
        service.update(created.id, establishment)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'CONTACT_ID_REQUIRED'
    }

    def 'a known establishment should be found'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                        .gimme("valid").with { network = networkUnderTest; it }
        Establishment created = service.create(establishment)

        when:
        Establishment result = service.findById(created.id)

        then:
        result != null
    }

    def 'a unknown establishment should not be found'(){
        when:
        service.findById('')

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ESTABLISHMENT_NOT_FOUND'
    }

    def 'a known establishment should be deleted'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                        .gimme("valid").with { network = networkUnderTest; it }
        Establishment created = service.create(establishment)

        when:
        service.delete(created.id)
        service.findById(created.id)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ESTABLISHMENT_NOT_FOUND'
    }

    def 'a known establishment with branch should not be deleted'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class)
                                        .gimme("valid").with { network = networkUnderTest; it }
        Branch branch = Fixture.from(Branch.class).gimme("valid")
        Establishment created = service.create(establishment)
        branch.headOffice = created
        branchService.create(branch)

        when:
        service.delete(created.id)

        then:
        def ex = thrown(ConflictException)
        ex.errors.find().logref == 'ESTABLISHMENT_WITH_BRANCH'
    }

    def 'a known establishment with event should not be deleted'(){
        given:
        Establishment establishment = Fixture.from(Establishment.class).uses(jpaProcessor).gimme("valid", new Rule(){{
            add("network", networkUnderTest)
        }})
        Fixture.from(EstablishmentEvent.class).uses(jpaProcessor).gimme("withoutReferences", new Rule(){{
            add("establishment", establishment)
            add("event", fixtureCreator.createEvent())
        }})

        when:
        service.delete(establishment.id)

        then:
        def ex = thrown(ConflictException)
        ex.errors.find().logref == 'ESTABLISHMENT_WITH_EVENT_VALUE'
    }

    def 'a unknown establishment should not be deleted'(){
        when:
        service.delete('')
        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ESTABLISHMENT_NOT_FOUND'
    }
}
