package br.com.unopay.api.order.service;

import br.com.unopay.api.billing.creditcard.model.TransactionStatus;
import br.com.unopay.api.credit.service.ContractorInstrumentCreditService;
import br.com.unopay.api.market.model.AuthorizedMemberCandidate;
import br.com.unopay.api.market.service.AuthorizedMemberCandidateService;
import br.com.unopay.api.market.service.DealCloseService;
import br.com.unopay.api.model.DealClose;
import br.com.unopay.api.notification.model.EventType;
import br.com.unopay.api.notification.service.NotificationService;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.model.PaymentStatus;
import br.com.unopay.api.service.ContractService;
import java.util.Set;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.order.model.OrderType.ADHESION;
import static br.com.unopay.api.order.model.OrderType.CREDIT;
import static br.com.unopay.api.order.model.OrderType.INSTALLMENT_PAYMENT;

@Slf4j
@Service
public class OrderProcessor {

    private OrderService orderService;
    private ContractService contractService;
    private ContractorInstrumentCreditService instrumentCreditService;
    @Setter private NotificationService notificationService;
    private DealCloseService dealCloseService;
    private AuthorizedMemberCandidateService authorizedMemberCandidateService;

    public OrderProcessor(OrderService orderService,
                          ContractService contractService,
                          ContractorInstrumentCreditService instrumentCreditService,
                          NotificationService notificationService,
                          DealCloseService dealCloseService,
                          AuthorizedMemberCandidateService authorizedMemberCandidateService) {
        this.orderService = orderService;
        this.contractService = contractService;
        this.instrumentCreditService = instrumentCreditService;
        this.notificationService = notificationService;
        this.dealCloseService = dealCloseService;
        this.authorizedMemberCandidateService = authorizedMemberCandidateService;
    }

    public void processAsPaid(String orderId){
        Order order = orderService.findById(orderId);
        order.setStatus(PaymentStatus.PAID);
        orderService.save(order);
        process(order);
    }

    public void processWithStatus(String id, TransactionStatus status){
        Order current = orderService.findById(id);
        current.defineStatus(status);
        orderService.save(current);
        process(current);
    }

    public void process(Order order){
        if(order.paid()) {
            if(order.isType(CREDIT)) {
                processCredit(order);
                return;
            }
            if(order.isType(INSTALLMENT_PAYMENT)){
                processInstallment(order);
                return;
            }
            if(order.isType(ADHESION)){
                processAdhesion(order);
                return;
            }
        }
        notificationService.sendPaymentEmail(order,  EventType.PAYMENT_DENIED);
    }

    private void processAdhesion(Order order) {
        Set<AuthorizedMemberCandidate> candidates = authorizedMemberCandidateService.findByOrderId(order.getId());
        DealClose dealClose = new DealClose(order.getPerson(), order.getProductCode(), candidates);
        dealCloseService.dealCloseWithIssuerAsHirer(dealClose);
        log.info("adhesion paid for order={} type={} of value={}",
                order.getId(),order.getType(), order.getValue());
        notificationService.sendPaymentEmail(order,  EventType.PAYMENT_APPROVED);
    }

    private void processInstallment(Order order) {
        contractService.markInstallmentAsPaidFrom(order);
        log.info("contract paid for order={} type={} of value={}",
                order.getId(),order.getType(), order.getValue());
        notificationService.sendPaymentEmail(order,  EventType.PAYMENT_APPROVED);
    }

    private void processCredit(Order order) {
        instrumentCreditService.processOrder(order);
        log.info("credit processed for order={} type={} of value={}",
                order.getId(),order.getType(), order.getValue());
        notificationService.sendPaymentEmail(order,  EventType.PAYMENT_APPROVED);
    }
}
