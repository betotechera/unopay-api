package br.com.unopay.api.market.receiver

import br.com.unopay.api.billing.boleto.service.TicketService
import br.com.unopay.api.config.QueuesScala
import br.com.unopay.api.market.model.BonusBilling
import br.com.unopay.api.util.{GenericObjectMapper, Logging}
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Profile(Array("!test"))
@Autowired
@Component
class BonusBillingReceiver(var genericObjectMapper: GenericObjectMapper,
                           var ticketService: TicketService) extends Logging {

    @Transactional
    @RabbitListener(queues = Array(QueuesScala.BONUS_BILLING_CREATED), containerFactory = QueuesScala.DURABLE_CONTAINER)
    def bonusBillingNotify(objectAsString: String): Unit = {
        val billing = genericObjectMapper.getAsObject(objectAsString, classOf[BonusBilling])
        log.info("processing bonus billing created issuer={}", billing.issuer.documentNumber)
        ticketService.createForBonusBilling(billing)
        log.info("processed bonus billing created issuer={}", billing.issuer.documentNumber)

    }
}

