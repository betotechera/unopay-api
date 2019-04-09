package br.com.unopay.api.network.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.network.model.Event
import br.com.unopay.api.network.service.EventService
import br.com.unopay.bootcommons.exception.ConflictException
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

    def 'given an event with the same name, it should not be created'(){
        given:
        Event event = Fixture.from(Event.class).gimme("valid")
        when:
        service.create(event)
        service.create(event.with{it.ncmCode = 'update';it})
        then:
        def ex = thrown(ConflictException)
        ex.errors.find().logref == 'EVENT_NAME_ALREADY_EXISTS'
    }
    def 'given an event with the same code, it should not be created'(){
        given:
        Event event = Fixture.from(Event.class).gimme("valid")
        when:
        service.create(event)
        service.create(event.with{it.name = 'update';it})

        then:
        def ex = thrown(ConflictException)
        ex.errors.find().logref == 'EVENT_CODE_ALREADY_EXISTS'
    }

    def 'given a event with unknown service should not be created'(){
        given:
        Event event = Fixture.from(Event.class).gimme("valid")
        event.getService().setId('')

        when:
        service.create(event)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'SERVICE_NOT_FOUND'
    }

    def 'given a event without service should not be created'(){
        given:
        Event event = Fixture.from(Event.class).gimme("valid")
        event.setService(null)

        when:
        service.create(event)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'SERVICE_REQUIRED'
    }

    def 'given a event with requestQuantity and without quantityUnity it should not be created'(){
        given:
        Event event = Fixture.from(Event.class).gimme("valid")
        event.setRequestQuantity(true)
        event.setQuantityUnity(null)

        when:
        service.create(event)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'QUANTITY_UNITY_REQUIRED'
    }

    def 'a valid event should be updated'(){
        given:
        Event event = Fixture.from(Event.class).gimme("valid")
        Event created = service.create(event)
        def newField = "test"
        event.setName(newField)

        when:
        service.update(created.id, event)
        Event result = service.findById(created.id)

        then:
        result.name == newField
    }

    def 'given an event with the same name, it should not be updated'(){
        given:
        Event event = Fixture.from(Event.class).gimme("valid")
        event.name = 'same'
        service.create(event)
        def updated = event.with { id = null; name = 'other'; ncmCode = 'other' ;it}
        service.create(updated)
        when:
            updated.name = 'same'
            service.update(updated.id,updated)
        then:
        def ex = thrown(ConflictException)
        ex.errors.find().logref == 'EVENT_NAME_ALREADY_EXISTS'
    }

    def 'given an event with the same code, it should not be updated'(){
        given:
        Event event = Fixture.from(Event.class).gimme("valid")
        event.ncmCode = 'same'
        service.create(event)
        def updated = event.with { id = null; name = 'other'; ncmCode = 'other' ;it}
        service.create(updated)
        when:
        updated.ncmCode = 'same'
        service.update(updated.id,updated)
        then:
        def ex = thrown(ConflictException)
        ex.errors.find().logref == 'EVENT_CODE_ALREADY_EXISTS'
    }


    def 'given a event with requestQuantity and without quantityUnity it should not be updated'(){
        given:
        Event event = Fixture.from(Event.class).gimme("valid")
        service.create(event)
        event.setRequestQuantity(true)
        event.setQuantityUnity(null)

        when:
        service.update(event.id,event)
        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'QUANTITY_UNITY_REQUIRED'
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

    def 'given a event with unknown service should not be updated'(){
        given:
        Event event = Fixture.from(Event.class).gimme("valid")
        def created = service.create(event)
        event.getService().setId('')

        when:
        service.update(created.id, event)

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'SERVICE_NOT_FOUND'
    }

    def 'given a event without service should not be updated'(){
        given:
        Event event = Fixture.from(Event.class).gimme("valid")
        def created = service.create(event)
        event.setService(null)

        when:
        service.update(created.id, event)

        then:
        def ex = thrown(UnprocessableEntityException)
        ex.errors.find().logref == 'SERVICE_REQUIRED'
    }

    def 'a unknown event should not be deleted'(){
        when:
        service.delete('')

        then:
        def ex = thrown(NotFoundException)
        ex.errors.find().logref == 'EVENT_NOT_FOUND'
    }
}
