package br.com.unopay.api.market.receiver

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.billing.boleto.service.TicketService
import br.com.unopay.api.market.model.NegotiationBilling
import br.com.unopay.api.util.GenericObjectMapper
import org.springframework.beans.factory.annotation.Autowired

class NegotiationBillingReceiverTest extends SpockApplicationTests {

    private NegotiationBillingReceiver negotiationBillingReceiver
    private TicketService negotiationBillingService = Mock(TicketService)
    @Autowired
    private GenericObjectMapper genericObjectMapper

    @Override
    void setup() {
        negotiationBillingReceiver = new NegotiationBillingReceiver(genericObjectMapper, negotiationBillingService)
    }

    def 'batchReceiptNotify'(){
        given:
        def batch = Fixture.from(NegotiationBilling).gimme("valid")
        when:
        negotiationBillingReceiver.batchReceiptNotify(toJson(batch))
        then:
        1 * negotiationBillingService.createForNegotiationBilling(_)
    }
}
