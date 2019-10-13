package br.com.unopay.api.market.receiver

import br.com.six2six.fixturefactory.Fixture.from
import br.com.unopay.api.UnopayApiScalaApplicationTest
import br.com.unopay.api.billing.boleto.service.TicketService
import br.com.unopay.api.market.model.BonusBilling
import br.com.unopay.api.util.GenericObjectMapper
import org.mockito.Mockito.verify
import org.scalatest.mockito.MockitoSugar
import org.springframework.beans.factory.annotation.Autowired

class BonusBillingReceiverTest extends UnopayApiScalaApplicationTest with MockitoSugar {
    var receiver: BonusBillingReceiver = _

    @Autowired
    var genericObjectMapper: GenericObjectMapper = _
    var mockTicketService: TicketService = _

    override def beforeEach(): Unit = {
        super.beforeEach()
        mockTicketService =  mock[TicketService]
        receiver = new BonusBillingReceiver(genericObjectMapper, mockTicketService)
    }

    it should "notify BonusBilling creation" in {
        val bonusBilling: BonusBilling = from(classOf[BonusBilling]).gimme("valid")

        receiver.bonusBillingNotify(toJson(bonusBilling))

        verify(mockTicketService).createForBonusBilling(_)
    }

}
