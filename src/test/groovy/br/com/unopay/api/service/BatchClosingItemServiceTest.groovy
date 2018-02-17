package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.FixtureCreator
import br.com.unopay.api.model.BatchClosingItem
import org.springframework.beans.factory.annotation.Autowired

class BatchClosingItemServiceTest extends SpockApplicationTests {

    @Autowired
    BatchClosingItemService service

    @Autowired
    ServiceAuthorizeService serviceAuthorizeService


    @Autowired
    FixtureCreator fixtureCreator

    def 'should create batch closing item'(){
        given:
        def batchClosing = fixtureCreator.createBatchClosing()
        def serviceAuthorize = fixtureCreator.createServiceAuthorize().with { authorizeEvents.find().id = null; it }
        serviceAuthorizeService.create(fixtureCreator.createUser(), serviceAuthorize.with {
            authorizeEvents.find().id = null; it
        })
        BatchClosingItem batchClosingItem = Fixture.from(BatchClosingItem.class).gimme("valid", new Rule(){{
            add("batchClosing", batchClosing)
            add("serviceAuthorize", serviceAuthorize)
        }})

        when:
        def created = service.save(batchClosingItem)
        def result = service.findById(created.id)

        then:
        result.id != null
    }
}
