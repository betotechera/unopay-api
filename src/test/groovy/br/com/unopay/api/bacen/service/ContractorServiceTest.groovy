package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Contractor
import br.com.unopay.api.bacen.model.filter.ContractorFilter
import br.com.unopay.api.bacen.repository.PaymentRuleGroupRepository
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.model.Contract
import br.com.unopay.api.network.model.AccreditedNetwork
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page

class ContractorServiceTest extends SpockApplicationTests {

    @Autowired
    ContractorService service

    @Autowired
    FixtureCreator fixtureCreator

    @Autowired
    PaymentRuleGroupRepository repository

    void 'should create Contractor'(){
        given:
        Contractor contractor = Fixture.from(Contractor.class).gimme("valid")

        when:
        contractor =  service.create(contractor)
        Contractor result  = service.getById(contractor.getId())

        then:
        result != null
    }

    void 'should create Contractor without bank account'(){
        given:
        Contractor contractor = Fixture.from(Contractor.class).gimme("valid", new Rule(){{
            add("bankAccount", null)
        }})

        when:
        contractor =  service.create(contractor)
        Contractor result  = service.getById(contractor.getId())

        then:
        result != null
    }


    void 'should not allow create contractor with same person'(){
        given:
        Contractor contractor = Fixture.from(Contractor.class).gimme("valid")

        when:
        service.create(contractor)
        service.create(contractor.with { id = null; it })

        then:
        def ex = thrown(ConflictException)
        ex.errors.first().logref == 'PERSON_CONTRACTOR_ALREADY_EXISTS'
    }

    void 'known contractor should be deleted'(){
        given:
        Contractor contractor = Fixture.from(Contractor.class).gimme("valid")
        service.create(contractor)
        Contractor found = service.getById(contractor.getId())

        when:
        service.delete(found.getId())
        service.getById(found.getId())

        then:
        thrown(NotFoundException)
    }

    void 'unknown contractor should not be deleted'(){

        when:
        service.delete('12345')

        then:
        thrown(NotFoundException)
    }

    void 'given known contractor should return all'(){
        given:
        List<Contractor> contractorsCreate = Fixture.from(Contractor.class).gimme(2, "valid")
        contractorsCreate.forEach { service.create(it) }

        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<Contractor> Contractors = service.findByFilter(new ContractorFilter(), page)

        then:
            assert Contractors.content.size() > 1
    }


    void 'should update contractor '(){
        given:
        Contractor contractor = Fixture.from(Contractor.class).gimme("valid")
        def created = service.create(contractor)

        when:
        contractor.person.name = 'Updated'
        contractor.person.legalPersonDetail.fantasyName = 'Test Update'
        service.update(created.id,contractor)
        def result = service.getById(created.id)
        then:
        result.person.name == 'Updated'
        result.person.legalPersonDetail.fantasyName == 'Test Update'

    }

    void 'given a contractor without bank account should updated'(){
        given:
        Contractor contractor = Fixture.from(Contractor.class).gimme("valid")

        def created = service.create(contractor)
        contractor.bankAccount = null

        when:
        contractor.person.name = 'Updated'
        contractor.person.legalPersonDetail.fantasyName = 'Test Update'
        service.update(created.id,contractor)
        def result = service.getById(created.id)
        then:
        result.person.name == 'Updated'
        result.person.legalPersonDetail.fantasyName == 'Test Update'
        result.bankAccount != null
    }

    void 'should return contractor for a logged network'() {
        given:
        Contract contract = fixtureCreator.createPersistedContract()
        def network = contract.getProduct().getAccreditedNetwork()
        def contractorId = contract.getContractor().getId()

        when:
        def result = service.getByIdForNetwork(contractorId, network)

        then:
        result != null
    }

    void 'should not return contractor with unknown id for a logged network'() {
        given:
        Contract contract = fixtureCreator.createPersistedContract()
        def network = contract.getProduct().getAccreditedNetwork()

        when:
        service.getByIdForNetwork("0110", network)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'CONTRACTOR_NOT_FOUND'
    }

    void 'should not return contractor if he dont belongs to the logged network'() {
        given:
        Contract contract = fixtureCreator.createPersistedContract()
        def contractorId = contract.getContractor().getId()
        def network = fixtureCreator.createNetwork()

        when:
        service.getByIdForNetwork(contractorId, network)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.first().logref == 'CONTRACTOR_NOT_FOUND'
    }


}
