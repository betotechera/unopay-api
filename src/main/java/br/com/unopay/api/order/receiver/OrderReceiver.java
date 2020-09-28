package br.com.unopay.api.order.receiver;

import br.com.unopay.api.billing.boleto.service.TicketService;
import br.com.unopay.api.billing.creditcard.model.PaymentMethod;
import br.com.unopay.api.billing.creditcard.model.Transaction;
import br.com.unopay.api.billing.creditcard.service.TransactionService;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.service.OrderProcessor;
import br.com.unopay.api.util.GenericObjectMapper;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!test")
@Slf4j
@Component
public class OrderReceiver {

    private TransactionService transactionService;
    private GenericObjectMapper genericObjectMapper;
    private OrderProcessor orderProcessor;
    private TicketService ticketService;

    @Autowired
    public OrderReceiver(TransactionService transactionService,
                         GenericObjectMapper genericObjectMapper,
                         OrderProcessor orderProcessor,
                         TicketService ticketService){
        this.transactionService = transactionService;
        this.genericObjectMapper = genericObjectMapper;
        this.orderProcessor = orderProcessor;
        this.ticketService = ticketService;
    }

    @RabbitListener(queues = Queues.ORDER_CREATED, containerFactory = Queues.DURABLE_CONTAINER)
    public void orderCreated(String objectAsString) {
        Order order = genericObjectMapper.getAsObject(objectAsString, Order.class);
        processPayment(order);
    }

    @Transactional
    @RabbitListener(queues = Queues.ORDER_CREATE, containerFactory = Queues.DURABLE_CONTAINER)
    public void orderCreating(String objectAsString){
        Order order = genericObjectMapper.getAsObject(objectAsString, Order.class);
        orderProcessor.createOrder(order);
    }

    @Transactional
    @RabbitListener(queues = Queues.ORDER_UPDATED, containerFactory = Queues.DURABLE_CONTAINER)
    public void orderUpdated(String objectAsString) {
        Order order = genericObjectMapper.getAsObject(objectAsString, Order.class);
        log.info("update order payment status for order={} type={} of value={} and number={}",
                order.getId(),order.getType(), order.getValue(), order.getNumber());
            orderProcessor.process(order);
    }

    private void processPayment(Order order) {
        log.info("creating payment for order={} method={} type={} of value={} and number={}",
                order.getId(), order.getRecurrencePaymentMethod(), order.getType(), order.getValue(), order.getNumber());
        if(order.is(PaymentMethod.CARD)) {
            order.definePaymentValue(order.paymentValue());
            order.defineCardToken(order.creditCardToken());
            order.definePaymentIssuer(order.issuerDocumentNumber());
            Transaction transaction = transactionService.create(order.getPaymentRequest());
            orderProcessor.processWithStatus(order.getId(), transaction.getStatus());
        }
        if(order.is(PaymentMethod.BOLETO)){
            ticketService.createForOrder(order.getId());
        }
    }
}
