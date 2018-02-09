package br.com.unopay.api.market.service;

import br.com.unopay.api.market.model.HirerNegotiation;
import br.com.unopay.api.market.model.NegotiationBilling;
import br.com.unopay.api.market.model.NegotiationBillingDetail;
import br.com.unopay.api.market.repository.NegotiationBillingRepository;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.ContractInstallment;
import br.com.unopay.api.service.ContractInstallmentService;
import br.com.unopay.api.service.ContractService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.HIRER_NEGOTIATION_BILLING_NOT_FOUND;

@Service
public class NegotiationBillingService {

    private NegotiationBillingRepository repository;
    private HirerNegotiationService hirerNegotiationService;
    private ContractInstallmentService contractInstallmentService;
    private ContractService contractService;
    private NegotiationBillingDetailService billingDetailService;

    @Value("${billing.hirer.tolerance.days}")
    private Integer hirerBillingToleranceDays;

    @Autowired
    public NegotiationBillingService(NegotiationBillingRepository repository,
                                     HirerNegotiationService hirerNegotiationService,
                                     ContractInstallmentService contractInstallmentService,
                                     ContractService contractService,
                                     NegotiationBillingDetailService billingDetailService) {
        this.repository = repository;
        this.hirerNegotiationService = hirerNegotiationService;
        this.contractInstallmentService = contractInstallmentService;
        this.contractService = contractService;
        this.billingDetailService = billingDetailService;
    }

    public NegotiationBilling save(NegotiationBilling billing) {
        return repository.save(billing);
    }

    public NegotiationBilling findById(String id) {
        return repository.findOne(id);
    }

    public NegotiationBilling findByHirer(String hirerId) {
        Optional<NegotiationBilling> billing = repository.findByHirerNegotiationHirerId(hirerId);
        return billing.orElseThrow(()-> UnovationExceptions.notFound().withErrors(HIRER_NEGOTIATION_BILLING_NOT_FOUND));
    }

    @Transactional
    public void process(String hirerId) {
        Set<Contract> hirerContracts = contractService.findByHirerId(hirerId);
        HirerNegotiation negotiation = hirerNegotiationService.findByHirerIdSilent(hirerId);
        NegotiationBilling billing = new NegotiationBilling(negotiation);
        final Integer[] installmentNumber = {0};
        Set<NegotiationBillingDetail> billingDetails = hirerContracts.stream().map(contract -> {
            ContractInstallment firstNotPaid = contractInstallmentService.findFirstNotPaid(contract.getId());
            installmentNumber[0] = firstNotPaid.getInstallmentNumber();
            return new NegotiationBillingDetail(contract, billing).defineValue();
        }).collect(Collectors.toSet());
        billing.setInstallmentNumber(installmentNumber[0]);
        billing.setInstallmentExpiration(getInstallmentExpiration(negotiation));
        if(!hirerContracts.isEmpty()) {
            billingDetails.forEach(b -> billing.addValue(b.getValue()));
            save(billing);
            billingDetails.forEach(b -> billingDetailService.save(b));
        }
    }

    private Date getInstallmentExpiration(HirerNegotiation negotiation) {
        return new DateTime().withDayOfMonth(negotiation.getPaymentDay()).minusDays(hirerBillingToleranceDays).toDate();
    }
}
