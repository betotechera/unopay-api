package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Event
import br.com.unopay.bootcommons.exception.NotFoundException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.springframework.beans.factory.annotation.Autowired

class EventServiceTest extends SpockApplicationTests {

    @Autowired
    EventService service

    def 'a valid event should be created'(){
        given:
        Event event = Fixture.from(Event.class).gimme("valid")
        when:
        Event created = service.create(event)

        then:
        created != null
    }

    def 'given a event with unknown provider should not be created'(){
        given:
        Event event = Fixture.from(Event.class).gimme("valid")
        event.getProvider().setId('')

        when:
        service.create(event)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'PROVIDER_NOT_FOUND'
    }

    def 'given a event without provider should not be created'(){
        given:
        Event event = Fixture.from(Event.class).gimme("valid")
        event.setProvider(null)

        when:
        service.create(event)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'PROVIDER_REQUIRED'
    }

    def 'a valid event should be updated'(){
        given:
        Event event = Fixture.from(Event.class).gimme("valid")
        Event created = service.create(event)
        def newField = "teste"
        event.setName(newField)

        when:
        service.update(created.id, event)
        Event result = service.findById(created.id)

        then:
        result.name == newField
    }

    def 'a known event should be found'(){
        given:
        Event event = Fixture.from(Event.class).gimme("valid")
        Event created = service.create(event)

        when:
        Event result = service.findById(created.id)

        then:
        result != null
    }

    def 'a unknown event should not be found'(){
        when:
        service.findById('')

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'EVENT_NOT_FOUND'
    }

    def 'a known event should be deleted'(){
        given:
        Event event = Fixture.from(Event.class).gimme("valid")
        Event created = service.create(event)

        when:
        service.delete(created.id)
        service.findById(created.id)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'EVENT_NOT_FOUND'
    }

    def 'given a event with unknown provider should not be updated'(){
        given:
        Event event = Fixture.from(Event.class).gimme("valid")
        def created = service.create(event)
        event.getProvider().setId('')

        when:
        service.update(created.id, event)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'PROVIDER_NOT_FOUND'
    }

    def 'given a event without provider should not be updated'(){
        given:
        Event event = Fixture.from(Event.class).gimme("valid")
        def created = service.create(event)
        event.setProvider(null)

        when:
        service.update(created.id, event)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'PROVIDER_REQUIRED'
    }

    def 'a unknown event should not be deleted'(){
        when:
        service.delete('')

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'EVENT_NOT_FOUND'
    }
}