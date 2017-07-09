package br.com.unopay.api.payment.service;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.service.IssuerService;
import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.payment.model.PaymentRemittance;
import br.com.unopay.api.payment.model.PaymentRemittanceItem;
import br.com.unopay.api.payment.repository.PaymentRemittanceRepository;
import br.com.unopay.api.service.BatchClosingService;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentRemittanceService {

    private PaymentRemittanceRepository repository;
    private BatchClosingService batchClosingService;
    private PaymentRemittanceItemService paymentRemittanceItemService;
    private IssuerService issuerService;

    @Autowired
    public PaymentRemittanceService(PaymentRemittanceRepository repository,
                                    BatchClosingService batchClosingService,
                                    PaymentRemittanceItemService paymentRemittanceItemService,
                                    IssuerService issuerService) {
        this.repository = repository;
        this.batchClosingService = batchClosingService;
        this.paymentRemittanceItemService = paymentRemittanceItemService;
        this.issuerService = issuerService;
    }

    public PaymentRemittance save(PaymentRemittance paymentRemittance) {
        return repository.save(paymentRemittance);
    }

    public PaymentRemittance findById(String id) {
        return repository.findOne(id);
    }

    @Transactional
    public void create(String issuer) {
        Issuer currentIssuer = issuerService.findById(issuer);
        Set<BatchClosing> batchByEstablishment = batchClosingService.findFinalizedByIssuerAndPaymentBeforeToday(issuer);
        Set<PaymentRemittanceItem> remittanceItems = processItems(batchByEstablishment);
        createRemittance(currentIssuer, remittanceItems);
    }

    private void createRemittance(Issuer currentIssuer, Set<PaymentRemittanceItem> remittanceItems) {
        PaymentRemittance paymentRemittance = new PaymentRemittance(currentIssuer, getTotal());
        paymentRemittance.setRemittanceItems(remittanceItems);
        save(paymentRemittance);
    }

    private Set<PaymentRemittanceItem> processItems(Set<BatchClosing> batchByEstablishment) {
        return batchByEstablishment.stream().map(batchClosing -> {
                PaymentRemittanceItem currentItem = getCurrentItem(batchClosing.establishmentId(), batchClosing);
                currentItem.updateValue(batchClosing.getValue());
                return paymentRemittanceItemService.save(currentItem);
            }).collect(Collectors.toSet());
    }

    public Set<PaymentRemittance> findByIssuer(String issuerId){
        return repository.findByIssuerId(issuerId);
    }

    private Long getTotal() {
        return repository.count();
    }

    private PaymentRemittanceItem getCurrentItem(String id,BatchClosing batchClosing){
        Optional<PaymentRemittanceItem> current = paymentRemittanceItemService.findProcessingByEstablishment(id);
        return current.orElse(new PaymentRemittanceItem(batchClosing));
    }
}
