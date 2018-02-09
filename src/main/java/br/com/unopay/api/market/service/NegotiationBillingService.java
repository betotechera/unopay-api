package br.com.unopay.api.market.service;

import br.com.unopay.api.market.model.HirerNegotiation;
import br.com.unopay.api.market.model.NegotiationBilling;
import br.com.unopay.api.market.model.NegotiationBillingDetail;
import br.com.unopay.api.market.repository.NegotiationBillingRepository;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.service.ContractService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.model.ContractInstallment.ONE_INSTALLMENT;
import static br.com.unopay.api.order.model.PaymentStatus.CANCELED;
import static br.com.unopay.api.order.model.PaymentStatus.PAID;
import static br.com.unopay.api.order.model.PaymentStatus.PAYMENT_DENIED;
import static br.com.unopay.api.order.model.PaymentStatus.WAITING_PAYMENT;
import static br.com.unopay.api.uaa.exception.Errors.HIRER_NEGOTIATION_BILLING_NOT_FOUND;
import static java.util.Collections.singletonList;

@Service
public class NegotiationBillingService {

    private NegotiationBillingRepository repository;
    private HirerNegotiationService hirerNegotiationService;
    private ContractService contractService;
    private NegotiationBillingDetailService billingDetailService;

    @Value("${billing.hirer.tolerance.days}")
    private Integer hirerBillingToleranceDays;

    @Autowired
    public NegotiationBillingService(NegotiationBillingRepository repository,
                                     HirerNegotiationService hirerNegotiationService,
                                     ContractService contractService,
                                     NegotiationBillingDetailService billingDetailService) {
        this.repository = repository;
        this.hirerNegotiationService = hirerNegotiationService;
        this.contractService = contractService;
        this.billingDetailService = billingDetailService;
    }

    public NegotiationBilling save(NegotiationBilling billing) {
        return repository.save(billing);
    }

    public NegotiationBilling findById(String id) {
        return repository.findOne(id);
    }

    public NegotiationBilling findLastNotPaidByHirer(String hirerId) {
        return checkReturn(()->  repository
                .findFirstByHirerNegotiationHirerIdAndStatusInOrderByCreatedDateTimeDesc(hirerId,
                        Arrays.asList(CANCELED, PAYMENT_DENIED, WAITING_PAYMENT)));
    }

    @Transactional
    public void process(String hirerId) {
        Set<Contract> hirerContracts = contractService.findByHirerId(hirerId);
        HirerNegotiation negotiation = hirerNegotiationService.findByHirerId(hirerId);
        NegotiationBilling billing = new NegotiationBilling(negotiation);
        Set<NegotiationBillingDetail> billingDetails = hirerContracts.stream()
                .map(contract ->
                        new NegotiationBillingDetail(contract, billing).defineValue()).collect(Collectors.toSet());

        if(!hirerContracts.isEmpty()) {
            billing.setInstallmentExpiration(getInstallmentExpiration(negotiation));
            billing.setInstallmentNumber(getNextPaymentNumber(hirerId));
            billingDetails.forEach(b -> billing.addValue(b.getValue()));
            save(billing);
            billingDetails.forEach(b -> billingDetailService.save(b));
        }
    }

    private Integer getNextPaymentNumber(String hirerId) {
        Optional<NegotiationBilling> lasPaid =  repository
                .findFirstByHirerNegotiationHirerIdAndStatusInOrderByCreatedDateTimeDesc(hirerId, singletonList(PAID));
        return lasPaid.map(NegotiationBilling::nextInstallmentNumber).orElse(ONE_INSTALLMENT);
    }
    private Date getInstallmentExpiration(HirerNegotiation negotiation) {
        return new DateTime().withDayOfMonth(negotiation.getPaymentDay()).minusDays(hirerBillingToleranceDays).toDate();
    }

    private NegotiationBilling checkReturn(Supplier<Optional<NegotiationBilling>> supplier) {
        Optional<NegotiationBilling> billing = supplier.get();
        return billing.orElseThrow(()-> UnovationExceptions.notFound().withErrors(HIRER_NEGOTIATION_BILLING_NOT_FOUND));
    }

}
