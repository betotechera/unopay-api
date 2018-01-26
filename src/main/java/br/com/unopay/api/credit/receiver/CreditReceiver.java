package br.com.unopay.api.credit.receiver;

import br.com.unopay.api.billing.boleto.service.BoletoService;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.credit.model.Credit;
import br.com.unopay.api.credit.model.CreditProcessed;
import br.com.unopay.api.credit.service.CreditService;
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
public class CreditReceiver {


    private CreditService creditService;
    private GenericObjectMapper genericObjectMapper;
    private BoletoService boletoService;

    @Autowired
    public CreditReceiver(CreditService creditService,
                          GenericObjectMapper genericObjectMapper){
        this.creditService = creditService;
        this.genericObjectMapper = genericObjectMapper;
    }

    @Transactional
    @RabbitListener(queues = Queues.CREDIT_PROCESSED, containerFactory = Queues.DURABLE_CONTAINER)
    public void creditReceiptNotify(String objectAsString) {
        CreditProcessed credit = genericObjectMapper.getAsObject(objectAsString, CreditProcessed.class);
        log.info("credit for {}={} of value={} received",credit.getTarget(),credit.getIssuerId(),credit.getValue());
        if(credit.forHirer()) {
            creditService.unblockCredit(credit);
            return;
        }
    }

    @Transactional
    @RabbitListener(queues = Queues.HIRER_CREDIT_CREATED, containerFactory = Queues.DURABLE_CONTAINER)
    public void creditCreated(String objectAsString) {
        Credit credit = genericObjectMapper.getAsObject(objectAsString, Credit.class);
        log.info("creating payment for hirer credit issuer={} hirer={} value={}",
                credit.issuerId(), credit.hirerId(), credit.getValue());
        boletoService.createForCredit(credit);
    }
}
