package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.Subsidiary
import br.com.unopay.bootcommons.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired

class SubsidiaryServiceTest  extends SpockApplicationTests {

    @Autowired
    SubsidiaryService service

    @Autowired
    EstablishmentService establishmentService

    @Autowired
    AccreditedNetworkService accreditedNetworkService

    Establishment matrixUnderTest

    void setup(){
        Establishment establishment = Fixture.from(Establishment.class).gimme("valid")
        accreditedNetworkService.create(establishment.network)
        matrixUnderTest = establishmentService.create(establishment)
    }

    def 'a valid subsidiary should be created'(){
        given:
        Subsidiary subsidiary = Fixture.from(Subsidiary.class).gimme("valid").with { matrix = matrixUnderTest; it }

        when:
        Subsidiary created = service.create(subsidiary)

        then:
        created != null
    }

    def 'a valid subsidiary should be updated'(){
        given:
        Subsidiary subsidiary = Fixture.from(Subsidiary.class).gimme("valid").with { matrix = matrixUnderTest; it }
        Subsidiary created = service.create(subsidiary)
        def newField = "teste"
        subsidiary.technicalContact = newField

        when:
        service.update(created.id, subsidiary)
        Subsidiary result = service.findById(created.id)

        then:
        result.technicalContact == newField
    }

    def 'a known subsidiary should be found'(){
        given:
        Subsidiary subsidiary = Fixture.from(Subsidiary.class).gimme("valid").with { matrix = matrixUnderTest; it }
        Subsidiary created = service.create(subsidiary)

        when:
        Subsidiary result = service.findById(created.id)

        then:
        result != null
    }

    def 'a unknown subsidiary should not be found'(){
        when:
        service.findById('')

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'SUBSIDIARY_NOT_FOUND'
    }

    def 'a known subsidiary should be deleted'(){
        given:
        Subsidiary subsidiary = Fixture.from(Subsidiary.class).gimme("valid").with { matrix = matrixUnderTest; it }
        Subsidiary created = service.create(subsidiary)

        when:
        service.delete(created.id)
        service.findById(created.id)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'SUBSIDIARY_NOT_FOUND'
    }

    def 'a unknown subsidiary should not be deleted'(){
        when:
        service.delete('')

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'SUBSIDIARY_NOT_FOUND'
    }
}
