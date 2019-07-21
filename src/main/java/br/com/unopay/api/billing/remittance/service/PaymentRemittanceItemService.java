package br.com.unopay.api.billing.remittance.service;

import br.com.unopay.api.billing.remittance.model.PaymentRemittanceItem;
import br.com.unopay.api.billing.remittance.model.RemittancePayee;
import br.com.unopay.api.billing.remittance.model.RemittanceSituation;
import br.com.unopay.api.billing.remittance.repository.PaymentRemittanceItemRepository;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentRemittanceItemService {

    private PaymentRemittanceItemRepository repository;

    public PaymentRemittanceItemService(){}

    @Autowired
    public PaymentRemittanceItemService(PaymentRemittanceItemRepository repository) {
        this.repository = repository;
    }

    public Set<PaymentRemittanceItem> processItems(Collection<RemittancePayee> payees) {
        return payees.stream().map(payee -> {
            Optional<PaymentRemittanceItem> existingItem = findProcessingByEstablishment(payee.getDocumentNumber());
            return existingItem.orElseGet(() -> {
                BigDecimal payeeReceivable = getAccumulatedReceivableTo(payees, payee);
                return save(new PaymentRemittanceItem(payee, payeeReceivable));
            });
        }).collect(Collectors.toSet());
    }

    private BigDecimal getAccumulatedReceivableTo(Collection<RemittancePayee> payees, RemittancePayee payee) {
        return payees.stream().filter(it-> it.getDocumentNumber().equals(payee.getDocumentNumber())).map(RemittancePayee::getReceivable).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public PaymentRemittanceItem save(PaymentRemittanceItem paymentRemittance) {
        return repository.save(paymentRemittance);
    }

    public Optional<PaymentRemittanceItem> findProcessingByEstablishment(String documentNumber){
        return repository.findByPayeeDocumentNumberAndSituation(documentNumber, RemittanceSituation.PROCESSING);
    }

    public PaymentRemittanceItem findById(String id) {
        return repository.findOne(id);
    }

    public PaymentRemittanceItem findByEstablishmentDocument(String document){
        Optional<PaymentRemittanceItem> byEstablishment = repository.findByPayeeDocumentNumber(document);
        return byEstablishment.orElse(null);
    }
}
