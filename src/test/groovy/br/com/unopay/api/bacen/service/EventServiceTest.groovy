package br.com.unopay.api.bacen.service

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Event
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
}
