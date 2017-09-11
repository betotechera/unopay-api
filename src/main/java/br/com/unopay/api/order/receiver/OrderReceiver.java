package br.com.unopay.api.order.receiver;

import br.com.unopay.api.billing.creditcard.model.PaymentMethod;
import br.com.unopay.api.billing.creditcard.model.Transaction;
import br.com.unopay.api.billing.creditcard.service.TransactionService;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.credit.service.ContractorInstrumentCreditService;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.service.OrderService;
import br.com.unopay.api.service.ContractService;
import br.com.unopay.api.util.GenericObjectMapper;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static br.com.unopay.api.order.model.OrderType.*;

@Slf4j
@Component
public class OrderReceiver {

    private TransactionService transactionService;
    private GenericObjectMapper genericObjectMapper;
    private ContractorInstrumentCreditService instrumentCreditService;
    private ContractService contractService;
    private OrderService orderService;

    @Autowired
    public OrderReceiver(TransactionService transactionService,
                         GenericObjectMapper genericObjectMapper,
                         ContractorInstrumentCreditService instrumentCreditService,
                         ContractService contractService, OrderService orderService){
        this.transactionService = transactionService;
        this.genericObjectMapper = genericObjectMapper;
        this.instrumentCreditService = instrumentCreditService;
        this.contractService = contractService;
        this.orderService = orderService;
    }

    @Transactional
    @RabbitListener(queues = Queues.UNOPAY_ORDER_CREATED)
    public void transactionNotify(String objectAsString) {
        Order order = genericObjectMapper.getAsObject(objectAsString, Order.class);
        log.info("creating payment transaction for order={} of value={}", order.getId(), order.getValue());
        if(order.is(PaymentMethod.CARD)) {
            Transaction transaction = transactionService.create(order.getPaymentRequest());
            order.defineStatus(transaction.getStatus());
            orderService.save(order);
        }
        if(order.paid()) {
            if(order.isType(CREDIT)) {
                instrumentCreditService.processOrder(order);
                return;
            }
            if(order.isType(INSTALLMENT_PAYMENT) || order.isType(ADHESION)){
                contractService.markInstallmentAsPaidFrom(order);
                return;
            }
        }
    }
}
