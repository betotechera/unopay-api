package br.com.unopay.api.billing.remittance.receiver;

import br.com.unopay.api.billing.remittance.model.filter.RemittanceFilter;
import br.com.unopay.api.billing.remittance.service.PaymentRemittanceService;
import br.com.unopay.api.config.Queues;
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
public class PaymentRemittanceReceiver {

    private GenericObjectMapper genericObjectMapper;
    private PaymentRemittanceService paymentRemittanceService;

    @Autowired
    public PaymentRemittanceReceiver(GenericObjectMapper genericObjectMapper,
                                     PaymentRemittanceService paymentRemittanceService) {
        this.genericObjectMapper = genericObjectMapper;
        this.paymentRemittanceService = paymentRemittanceService;
    }

    @Transactional
    @RabbitListener(queues = Queues.PAYMENT_REMITTANCE, containerFactory = Queues.DURABLE_CONTAINER)
    public void remittanceReceiptNotify(String objectAsString) {
        RemittanceFilter filter = genericObjectMapper.getAsObject(objectAsString, RemittanceFilter.class);
        log.info("processing remittance for issuer={}", filter.getId());
        paymentRemittanceService.createForBatch(filter.getId(), filter.getAt());
        paymentRemittanceService.createForCredit(filter.getId());
        log.info("processed remittance for issuer={}", filter.getId());
    }
}
