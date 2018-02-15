package br.com.unopay.api.market.service;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.credit.model.Credit;
import br.com.unopay.api.credit.service.CreditService;
import br.com.unopay.api.infra.Notifier;
import br.com.unopay.api.infra.NumberGenerator;
import br.com.unopay.api.market.model.HirerNegotiation;
import br.com.unopay.api.market.model.NegotiationBilling;
import br.com.unopay.api.market.model.NegotiationBillingDetail;
import br.com.unopay.api.market.model.filter.NegotiationBillingFilter;
import br.com.unopay.api.market.repository.NegotiationBillingRepository;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.order.model.PaymentStatus;
import br.com.unopay.api.service.ContractService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import javax.transaction.Transactional;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.model.ContractInstallment.ONE_INSTALLMENT;
import static br.com.unopay.api.order.model.PaymentStatus.CANCELED;
import static br.com.unopay.api.order.model.PaymentStatus.PAID;
import static br.com.unopay.api.order.model.PaymentStatus.PAYMENT_DENIED;
import static br.com.unopay.api.order.model.PaymentStatus.WAITING_PAYMENT;
import static br.com.unopay.api.uaa.exception.Errors.HIRER_NEGOTIATION_BILLING_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.NEGOTIATION_BILLING_NOT_FOUND;
import static java.util.Collections.singletonList;

@Service
public class NegotiationBillingService {

    private NegotiationBillingRepository repository;
    private HirerNegotiationService hirerNegotiationService;
    private ContractService contractService;
    private NegotiationBillingDetailService billingDetailService;
    private CreditService creditService;
    private NumberGenerator numberGenerator;
    @Setter private Integer memberTotal = 1;
    @Setter private Notifier notifier;

    @Autowired
    public NegotiationBillingService(NegotiationBillingRepository repository,
                                     HirerNegotiationService hirerNegotiationService,
                                     ContractService contractService,
                                     NegotiationBillingDetailService billingDetailService,
                                     CreditService creditService, Notifier notifier) {
        this.repository = repository;
        this.hirerNegotiationService = hirerNegotiationService;
        this.contractService = contractService;
        this.billingDetailService = billingDetailService;
        this.numberGenerator = new NumberGenerator(repository);
        this.creditService = creditService;
        this.notifier = notifier;
    }

    public NegotiationBilling save(NegotiationBilling billing) {
        return repository.save(billing);
    }

    public NegotiationBilling findById(String id) {
        Optional<NegotiationBilling> billing = repository.findById(id);
        return billing.orElseThrow(()->
                UnovationExceptions.notFound().withErrors(NEGOTIATION_BILLING_NOT_FOUND.withOnlyArgument(id)));
    }

    public NegotiationBilling findByIdForIssuer(String id, Issuer issuer) {
        Optional<NegotiationBilling> billing = repository.findByIdAndHirerNegotiationProductIssuerId(id,issuer.getId());
        return billing.orElseThrow(()->
                UnovationExceptions.notFound().withErrors(NEGOTIATION_BILLING_NOT_FOUND.withOnlyArgument(id)));
    }

    public NegotiationBilling findLastNotPaidByHirer(String hirerId) {
        return checkReturn(()->  repository
                .findFirstByHirerNegotiationHirerIdAndStatusInOrderByCreatedDateTimeDesc(hirerId,
                        Arrays.asList(CANCELED, PAYMENT_DENIED, WAITING_PAYMENT)));
    }

    public Optional<NegotiationBilling> findOptionalLastNotPaidByHirer(String hirerId) {
        return repository
                .findFirstByHirerNegotiationHirerIdAndStatusInOrderByCreatedDateTimeDesc(hirerId,
                        Arrays.asList(CANCELED, PAYMENT_DENIED, WAITING_PAYMENT));
    }

    public Set<NegotiationBilling> findByHirerId(String hirerId) {
        return repository.findByHirerNegotiationHirerId(hirerId);
    }

    public Page<NegotiationBilling> findByFilter(NegotiationBillingFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    public void processAsPaid(String billingId) {
        NegotiationBilling current = repository.findOne(billingId);
        current.setStatus(PaymentStatus.PAID);
        save(current);
    }

    @Transactional
    public void processForIssuer(String id, Issuer issuer) {
        HirerNegotiation negotiation = hirerNegotiationService.findByIdForIssuer(id, issuer);
        process(negotiation);
    }

    public void process(){
        Set<HirerNegotiation> negotiations = hirerNegotiationService.negotiationsNearOfPaymentDate();
        negotiations.forEach(negotiation ->
                findOptionalLastNotPaidByHirer(negotiation.hirerId()).orElseGet(() -> process(negotiation)));
    }

    @Transactional
    public void process(String id) {
        HirerNegotiation negotiation = hirerNegotiationService.findById(id);
        process(negotiation);
    }

    private NegotiationBilling process(HirerNegotiation negotiation) {
        Set<Contract> hirerContracts = contractService.findByHirerId(negotiation.hirerId());
        Integer nextInstallment = getNextInstallmentNumber(negotiation.hirerId());
        if(nextInstallment <= negotiation.getInstallments() && !hirerContracts.isEmpty()) {
            createBilling(hirerContracts, negotiation, nextInstallment);
       }
       return null;
    }

    private void createBilling(Set<Contract> hirerContracts, HirerNegotiation negotiation, Integer nextInstallment) {
        NegotiationBilling billing = new NegotiationBilling(negotiation, nextInstallment);
        billing.setNumber(numberGenerator.createNumber());
        billing.setInstallmentExpiration(getInstallmentExpiration(negotiation));
        NegotiationBilling rightBilling = createBillingDetailsAndUpdateBillingValue(hirerContracts, save(billing));
        createCreditWhenRequired(rightBilling);
        notifier.notify(Queues.HIRER_BILLING_CREATED,rightBilling);
    }

    private void createCreditWhenRequired(NegotiationBilling billing) {
        if(billing.getBillingWithCredits()){
            Credit credit = creditService.insert(new Credit(billing));
            save(billing.withCredit(credit));
        }
    }

    private NegotiationBilling createBillingDetailsAndUpdateBillingValue(Set<Contract> hirerContracts,
                                                                         NegotiationBilling billing) {
        hirerContracts.stream().map(NegotiationBillingDetail::new)
        .forEach(detail ->{
                detail.setMemberTotal(this.memberTotal);
                billing.addValue(detail.defineBillingInformation(billing).getValue());
                billing.addCreditValueWhenRequired(detail.defineBillingInformation(billing).creditValue());
                billingDetailService.save(detail);
        });
        return save(billing);
    }

    private Integer getNextInstallmentNumber(String hirerId) {
        Optional<NegotiationBilling> lastPaid =  repository
                .findFirstByHirerNegotiationHirerIdAndStatusInOrderByCreatedDateTimeDesc(hirerId, singletonList(PAID));
        return lastPaid.map(NegotiationBilling::nextInstallmentNumber).orElse(ONE_INSTALLMENT);
    }
    private Date getInstallmentExpiration(HirerNegotiation negotiation) {
        if(negotiation.getEffectiveDate().after(new Date())){
            return negotiation.getEffectiveDate();
        }
        return new DateTime().withDayOfMonth(negotiation.getPaymentDay()).toDate();
    }

    private NegotiationBilling checkReturn(Supplier<Optional<NegotiationBilling>> supplier) {
        Optional<NegotiationBilling> billing = supplier.get();
        return billing.orElseThrow(()-> UnovationExceptions.notFound().withErrors(HIRER_NEGOTIATION_BILLING_NOT_FOUND));
    }

}
