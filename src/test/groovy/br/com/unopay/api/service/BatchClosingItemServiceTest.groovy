package br.com.unopay.api.service

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.util.SetupCreator
import br.com.unopay.api.model.BatchClosingItem
import org.springframework.beans.factory.annotation.Autowired

class BatchClosingItemServiceTest extends SpockApplicationTests {

    @Autowired
    BatchClosingItemService service

    @Autowired
    ServiceAuthorizeService serviceAuthorizeService


    @Autowired
    SetupCreator setupCreator

    def 'should create batch closing item'(){
        given:
        def batchClosing = setupCreator.createBatchClosing()
        def serviceAuthorize = setupCreator.createServiceAuthorize()
        serviceAuthorizeService.create(setupCreator.createUser().email, serviceAuthorize)
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
