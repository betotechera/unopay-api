package br.com.unopay.api.order.receiver;

import br.com.unopay.api.billing.creditcard.model.Transaction;
import br.com.unopay.api.billing.creditcard.service.TransactionService;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.credit.service.ContractorInstrumentCreditService;
import br.com.unopay.api.order.model.CreditOrder;
import br.com.unopay.api.util.GenericObjectMapper;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderReceiver {

    private TransactionService transactionService;
    private GenericObjectMapper genericObjectMapper;
    private ContractorInstrumentCreditService instrumentCreditService;

    @Autowired
    public OrderReceiver(TransactionService transactionService,
                         GenericObjectMapper genericObjectMapper,
                         ContractorInstrumentCreditService instrumentCreditService){
        this.transactionService = transactionService;
        this.genericObjectMapper = genericObjectMapper;
        this.instrumentCreditService = instrumentCreditService;
    }

    @Transactional
    @RabbitListener(queues = Queues.UNOPAY_ORDER_CREATED)
    public void transactionNotify(String objectAsString) {
        CreditOrder order = genericObjectMapper.getAsObject(objectAsString, CreditOrder.class);
        log.info("creating payment transaction for order={} of value={}", order.getId(),
                order.getPaymentRequest().getValue());
        Transaction transaction = transactionService.create(order.getPaymentRequest());
        order.defineStatus(transaction.getStatus());
        instrumentCreditService.unlockCredit(order);
    }
}
