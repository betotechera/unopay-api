package br.com.unopay.api.receiver

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.model.BatchClosing
import br.com.unopay.api.service.BatchClosingService
import br.com.unopay.api.util.GenericObjectMapper
import org.springframework.beans.factory.annotation.Autowired

class BatchClosingReceiverTest extends SpockApplicationTests {

    private BatchClosingReceiver batchClosingReceiver
    private BatchClosingService batchClosingService = Mock(BatchClosingService)
    @Autowired
    private GenericObjectMapper genericObjectMapper

    @Override
    void setup() {
        batchClosingReceiver = new BatchClosingReceiver(genericObjectMapper, batchClosingService)
    }

    def 'batchReceiptNotify'(){
        given:
        def batch = Fixture.from(BatchClosing).gimme("valid")
        when:
        batchClosingReceiver.batchReceiptNotify(toJson(batch))
        then:
        1 * batchClosingService.process(_,_)
    }
}
