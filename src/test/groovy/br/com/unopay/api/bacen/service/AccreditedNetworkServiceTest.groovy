package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.AccreditedNetwork
import br.com.unopay.api.bacen.model.filter.AccreditedNetworkFilter
import br.com.unopay.bootcommons.exception.ConflictException
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page

class AccreditedNetworkServiceTest extends SpockApplicationTests {

    @Autowired
    AccreditedNetworkService service

    void 'should create AccreditedNetwork'(){
        given:
        AccreditedNetwork accreditedNetwork = Fixture.from(AccreditedNetwork.class).gimme("valid")

        when:
        accreditedNetwork =  service.create(accreditedNetwork)
        AccreditedNetwork result  = service.getById(accreditedNetwork.getId())

        then:
        result != null
    }
    void 'should not allow create AccreditedNetwork with same person'(){
        given:
        AccreditedNetwork accreditedNetwork = Fixture.from(AccreditedNetwork.class).gimme("valid")

        when:
        service.create(accreditedNetwork)
        service.create(accreditedNetwork.with { id = null; it })

        then:
        def ex = thrown(ConflictException)
        ex.errors.first().logref == 'PERSON_ACCREDITED_NETWORK_ALREADY_EXISTS'
    }

    void 'known accreditedNetwork should be deleted'(){
        given:
        AccreditedNetwork accreditedNetwork = Fixture.from(AccreditedNetwork.class).gimme("valid")
        service.create(accreditedNetwork)
        AccreditedNetwork found = service.getById(accreditedNetwork.getId())

        when:
        service.delete(found.getId())
        service.getById(found.getId())

        then:
        thrown(NotFoundException)
    }

    void 'unknown accreditedNetwork should not be deleted'(){

        when:
        service.delete('12345')

        then:
        thrown(NotFoundException)
    }

    void 'given known accreditedNetwork should return all'(){
        given:
        List<AccreditedNetwork> accreditedNetworksCreate = Fixture.from(AccreditedNetwork.class).gimme(2, "valid")
        accreditedNetworksCreate.forEach { service.create(it) }

        when:
        UnovationPageRequest page = new UnovationPageRequest() {{ setPage(1); setSize(10)}}
        Page<AccreditedNetwork> AccreditedNetworks = service.findByFilter(new AccreditedNetworkFilter(), page)

        then:
        assert AccreditedNetworks.content.size() > 1
    }


    void 'should update accreditedNetwork '(){
        given:
        AccreditedNetwork accreditedNetwork = Fixture.from(AccreditedNetwork.class).gimme("valid")
        def created = service.create(accreditedNetwork)

        when:
        accreditedNetwork.person.name = 'Updated'
        accreditedNetwork.person.telephone = '1199999999'
        accreditedNetwork.person.legalPersonDetail.responsibleName = 'Test Update'
        accreditedNetwork.merchantDiscountRate = 0.99D

        service.update(created.id,accreditedNetwork)
        def result = service.getById(created.id)
        then:
        result.person.name == 'Updated'
        result.person.telephone== '1199999999'
        result.merchantDiscountRate == 0.99D

    }


}
