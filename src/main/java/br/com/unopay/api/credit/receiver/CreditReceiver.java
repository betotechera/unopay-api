package br.com.unopay.api.credit.receiver;

import br.com.unopay.api.config.Queues;
import br.com.unopay.api.credit.model.CreditProcessed;
import br.com.unopay.api.credit.service.CreditService;
import br.com.unopay.api.util.GenericObjectMapper;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreditReceiver {


    private CreditService creditService;
    private GenericObjectMapper genericObjectMapper;

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
        log.info("credit for {}={} of value={} received",credit.getTarget(),credit.getDocument(),credit.getValue());
        if(credit.forHirer()) {
            creditService.unblockCredit(credit);
            return;
        }
    }
}
