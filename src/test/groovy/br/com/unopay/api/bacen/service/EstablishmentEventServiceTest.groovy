package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Establishment
import br.com.unopay.api.bacen.model.EstablishmentEvent
import br.com.unopay.api.bacen.model.Event
import br.com.unopay.api.bacen.model.ServiceType
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
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
        }})
        when:
        EstablishmentEvent created = service.create(establishment.id, establishmentEvent)

        then:
        created != null
    }

    def 'a unknown establishment the event should not be created'(){
        given:
        def event = fixtureCreator.createEvent(ServiceType.FUEL_ALLOWANCE)
        EstablishmentEvent establishmentEvent = Fixture.from(EstablishmentEvent.class)
                .gimme("withoutReferences", new Rule(){{
            add("event", event)
        }})
        when:
        service.create('', establishmentEvent)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ESTABLISHMENT_NOT_FOUND'
    }

    def 'a unknown event the establishment event should not be created'(){
        given:
        def event = new Event()
        def establishment = fixtureCreator.createEstablishment()
        EstablishmentEvent establishmentEvent = Fixture.from(EstablishmentEvent.class)
                .gimme("withoutReferences", new Rule(){{
            add("event", event)
        }})
        when:
        service.create(establishment.id, establishmentEvent)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'EVENT_NOT_FOUND'
    }

    def 'a valid establishment should be updated'(){
        given:
        def created = create()
        def newField = 99999.9
        created.value = newField

        when:
        service.update(created.establishment.id, created)
        EstablishmentEvent result = service.findById(created.id)

        then:
        result.value == newField
    }

    def 'given a establishment event with unknown establishment should not be updated'(){
        given:
        def created = create()
        def newField = 99999.9
        created.value = newField

        when:
        service.update('', created)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ESTABLISHMENT_NOT_FOUND'
    }

    def 'given a establishment event with unknown event should not be updated'(){
        given:
        def created = create()
        created.event = new Event()
        def newField = 99999.9
        created.value = newField

        when:
        service.update(created.establishment.id, created)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'EVENT_NOT_FOUND'
    }

    def 'when try update event of other establishment should not be updated'(){
        given:
        EstablishmentEvent created = create()
        def establishment = fixtureCreator.createEstablishment()
        def newField = 99999.9
        created.value = newField

        when:
        service.update(establishment.id, created)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'ESTABLISHMENT_NOT_QUALIFIED_FOR_THIS_EVENT'
    }

    def 'a known establishment should be found'(){
        given:
        def created = create()

        when:
        EstablishmentEvent result = service.findById(created.id)

        then:
        result != null
    }

    def 'when find by establishment and id should be found'(){
        given:
        def created = create()

        when:
        EstablishmentEvent result = service.findByEstablishmentIdAndId(created.establishment.id,created.id)

        then:
        result != null
    }

    def 'a unknown establishment the event should not be deleted'(){
        given:
        def created = create()

        when:
        service.deleteByEstablishmentIdAndId('', created.id)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ESTABLISHMENT_EVENT_NOT_FOUND'
    }

    def 'a known establishment the event should be deleted'(){
        given:
        def created = create()

        when:
        service.deleteByEstablishmentIdAndId(created.establishment.id, created.id)
        service.findById(created.id)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'ESTABLISHMENT_EVENT_NOT_FOUND'
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

    private EstablishmentEvent create(Establishment establishment = fixtureCreator.createEstablishment()){
        def event = fixtureCreator.createEvent(ServiceType.FUEL_ALLOWANCE)
        return Fixture.from(EstablishmentEvent.class).uses(jpaProcessor)
                .gimme("withoutReferences", new Rule(){{
            add("event", event)
            add("establishment", establishment)
        }})
    }
}
