package br.com.unopay.api.order.receiver;

import br.com.unopay.api.billing.boleto.service.TicketService;
import br.com.unopay.api.billing.creditcard.model.PaymentMethod;
import br.com.unopay.api.billing.creditcard.model.Transaction;
import br.com.unopay.api.billing.creditcard.service.TransactionService;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.service.OrderService;
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
    private OrderService orderService;
    private TicketService ticketService;

    @Autowired
    public OrderReceiver(TransactionService transactionService,
                         GenericObjectMapper genericObjectMapper,
                         OrderService orderService,
                         TicketService ticketService){
        this.transactionService = transactionService;
        this.genericObjectMapper = genericObjectMapper;
        this.orderService = orderService;
        this.ticketService = ticketService;
    }

    @Transactional
    @RabbitListener(queues = Queues.ORDER_CREATED, containerFactory = Queues.DURABLE_CONTAINER)
    public void transactionNotify(String objectAsString) {
        Order order = genericObjectMapper.getAsObject(objectAsString, Order.class);
        log.info("creating payment for order={} type={} of value={}",
                order.getId(),order.getType(), order.getValue());
        if(order.is(PaymentMethod.CARD)) {
            order.getPaymentRequest().setValue(order.getValue());
            Transaction transaction = transactionService.create(order.getPaymentRequest());
            orderService.processWithStatus(order.getId(), transaction.getStatus());
        }
        if(order.is(PaymentMethod.BOLETO)){
            ticketService.createForOrder(order.getId());
        }
    }
}
