package br.com.unopay.api.market.receiver;

import br.com.unopay.api.billing.boleto.service.TicketService;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.market.model.NegotiationBilling;
import br.com.unopay.api.util.GenericObjectMapper;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("!test")
@Component
public class NegotiationBillingReceiver {

    private GenericObjectMapper genericObjectMapper;
    private TicketService ticketService;

    @Autowired
    public NegotiationBillingReceiver(GenericObjectMapper genericObjectMapper,
                                      TicketService ticketService) {
        this.genericObjectMapper = genericObjectMapper;
        this.ticketService = ticketService;
    }

    @Transactional
    @RabbitListener(queues = Queues.HIRER_BILLING_CREATED, containerFactory = Queues.DURABLE_CONTAINER)
    public void batchReceiptNotify(String objectAsString) {
        NegotiationBilling billing = genericObjectMapper.getAsObject(objectAsString, NegotiationBilling.class);
        log.info("processing negotiation billing created hirer={}", billing.hirer().getDocumentNumber());
        ticketService.createForNegotiationBilling(billing);
        log.info("processed negotiation billing created hirer={}", billing.hirer().getDocumentNumber());
    }
}
