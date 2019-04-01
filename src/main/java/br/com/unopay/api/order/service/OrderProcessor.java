package br.com.unopay.api.order.service;

import br.com.unopay.api.billing.creditcard.model.TransactionStatus;
import br.com.unopay.api.credit.service.ContractorInstrumentCreditService;
import br.com.unopay.api.market.model.AuthorizedMemberCandidate;
import br.com.unopay.api.market.service.AuthorizedMemberCandidateService;
import br.com.unopay.api.market.service.DealService;
import br.com.unopay.api.model.Deal;
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
    private DealService dealService;
    private AuthorizedMemberCandidateService authorizedMemberCandidateService;

    public OrderProcessor(OrderService orderService,
                          ContractService contractService,
                          ContractorInstrumentCreditService instrumentCreditService,
                          NotificationService notificationService,
                          DealService dealService,
                          AuthorizedMemberCandidateService authorizedMemberCandidateService) {
        this.orderService = orderService;
        this.contractService = contractService;
        this.instrumentCreditService = instrumentCreditService;
        this.notificationService = notificationService;
        this.dealService = dealService;
        this.authorizedMemberCandidateService = authorizedMemberCandidateService;
    }

    public void processAsPaid(String number){
        Order order = orderService.findByNumber(number);
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
        Deal deal = new Deal(order.getPerson(), order.getProductCode(), order.getCreateUser(), candidates, order.getUserPassword());
        dealService.closeWithIssuerAsHirer(deal);
        log.info("adhesion paid for order={} type={} of value={}",
                order.getId(),order.getType(), order.getValue());
        sentPaymentMail(order);
    }

    private void processInstallment(Order order) {
        contractService.markInstallmentAsPaidFrom(order);
        log.info("contract paid for order={} type={} of value={}",
                order.getId(),order.getType(), order.getValue());
        sentPaymentMail(order);
    }

    private void processCredit(Order order) {
        instrumentCreditService.processOrder(order);
        log.info("credit processed for order={} type={} of value={}",
                order.getId(),order.getType(), order.getValue());
        sentPaymentMail(order);
    }

    private void sentPaymentMail(Order order) {
        if(order.hasBillingMail()) {
            notificationService.sendPaymentEmail(order, EventType.PAYMENT_APPROVED);
        }
    }
}
