package br.com.unopay.api.credit.receiver;

import br.com.unopay.api.billing.boleto.service.BoletoService;
import br.com.unopay.api.billing.creditcard.model.PaymentMethod;
import br.com.unopay.api.billing.creditcard.model.Transaction;
import br.com.unopay.api.billing.creditcard.service.TransactionService;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.credit.model.Credit;
import br.com.unopay.api.credit.model.CreditProcessed;
import br.com.unopay.api.credit.service.CreditService;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.util.GenericObjectMapper;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static br.com.unopay.api.credit.model.CreditInsertionType.CREDIT_CARD;
import static br.com.unopay.api.credit.model.CreditInsertionType.DIRECT_DEBIT;
import static br.com.unopay.api.credit.model.CreditTarget.HIRER;

@Slf4j
@Profile("!test")
@Component
public class CreditReceiver {


    private CreditService creditService;
    private GenericObjectMapper genericObjectMapper;
    private BoletoService boletoService;
    private TransactionService transactionService;

    @Autowired
    public CreditReceiver(CreditService creditService,
                          GenericObjectMapper genericObjectMapper,
                          BoletoService boletoService,
                          TransactionService transactionService){
        this.creditService = creditService;
        this.genericObjectMapper = genericObjectMapper;
        this.boletoService = boletoService;
        this.transactionService = transactionService;
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

    @RabbitListener(queues = Queues.HIRER_CREDIT_CREATED, containerFactory = Queues.DURABLE_CONTAINER)
    public void creditCreated(String objectAsString) {
        Credit credit = genericObjectMapper.getAsObject(objectAsString, Credit.class);
        log.info("creating payment for hirer credit issuer={} hirer={} value={}",
                credit.issuerId(), credit.hirerId(), credit.getValue());
        if(credit.isCreditCard()) {
            definePaymentRequest(credit);
            Transaction transaction = transactionService.create(credit.getPaymentRequest());
            creditService.process(credit, transaction);
        }
        if(credit.isBoleto()) {
            boletoService.createForCredit(credit);
        }
    }



    private void definePaymentRequest(Credit credit) {
        credit.getPaymentRequest().setValue(credit.getValue());
        credit.getPaymentRequest().setOrderId(credit.getId());
        credit.getPaymentRequest().setMethod(PaymentMethod.CARD);
    }

}
