import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.SpockApplicationTests
import br.com.unopay.api.bacen.model.Issuer
import br.com.unopay.api.billing.remittance.model.filter.RemittanceFilter
import br.com.unopay.api.billing.remittance.receiver.PaymentRemittanceReceiver
import br.com.unopay.api.billing.remittance.service.PaymentRemittanceService
import br.com.unopay.api.util.GenericObjectMapper
import org.springframework.beans.factory.annotation.Autowired

class PaymentRemittanceReceiverTest extends SpockApplicationTests {

    private PaymentRemittanceService paymentRemittanceService = Mock(PaymentRemittanceService)
    @Autowired
    private GenericObjectMapper genericObjectMapper
    private PaymentRemittanceReceiver paymentRemittanceReceiver

    @Override
    void setup() {
        paymentRemittanceReceiver = new PaymentRemittanceReceiver(genericObjectMapper, paymentRemittanceService)
    }

    def 'remittanceReceiptNotify' (){
        given:
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        RemittanceFilter filter = new RemittanceFilter(id: issuer.id,at: new Date())
        when:
        paymentRemittanceReceiver.remittanceReceiptNotify(toJson(filter))
        then:
        1 * paymentRemittanceService.createForBatch(_,_)
        1 * paymentRemittanceService.createForCredit(_)
    }
}
