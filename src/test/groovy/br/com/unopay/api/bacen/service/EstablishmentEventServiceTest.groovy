package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.EstablishmentEvent
import br.com.unopay.api.bacen.model.ServiceType
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.bootcommons.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired

class EstablishmentEventServiceTest extends SpockApplicationTests {

    @Autowired
    EstablishmentEventService service


    @Autowired
    FixtureCreator fixtureCreator


    def 'a valid establishment event should be created'(){
        given:
        def event = fixtureCreator.createEvent(ServiceType.FUEL_ALLOWANCE)
        def establishment = fixtureCreator.createEstablishment()
        EstablishmentEvent establishmentEvent = Fixture.from(EstablishmentEvent.class)
                                                                            .gimme("withoutReferences", new Rule(){{
            add("event", event)
            add("establishment", establishment)
        }})
        when:
        EstablishmentEvent created = service.create(establishmentEvent)

        then:
        created != null
    }

    def 'a valid establishment should be updated'(){
        given:
        def created = create()
        def newField = 99999.9
        created.value = newField

        when:
        service.update(created.id, created)
        EstablishmentEvent result = service.findById(created.id)

        then:
        result.value == newField
    }

    def 'a known establishment should be found'(){
        given:
        def created = create()

        when:
        EstablishmentEvent result = service.findById(created.id)

        then:
        result != null
    }

    def 'a unknown establishment should not be found'(){
        when:
        service.findById('')

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ESTABLISHMENT_EVENT_NOT_FOUND'
    }

    def 'a known establishment should be deleted'(){
        given:
        def created = create()

        when:
        service.delete(created.id)
        service.findById(created.id)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ESTABLISHMENT_EVENT_NOT_FOUND'
    }

    def 'a unknown establishment should not be deleted'(){
        when:
        service.delete('')
        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ESTABLISHMENT_EVENT_NOT_FOUND'
    }

    private EstablishmentEvent create(){
        def event = fixtureCreator.createEvent(ServiceType.FUEL_ALLOWANCE)
        def establishment = fixtureCreator.createEstablishment()
        return Fixture.from(EstablishmentEvent.class).uses(jpaProcessor)
                .gimme("withoutReferences", new Rule(){{
            add("event", event)
            add("establishment", establishment)
        }})
    }
}
