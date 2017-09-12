package br.com.unopay.api.billing.remittance.repository;

import br.com.unopay.api.billing.remittance.model.PaymentRemittance;
import br.com.unopay.api.billing.remittance.model.RemittanceSituation;
import br.com.unopay.api.billing.remittance.model.filter.PaymentRemittanceFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentRemittanceRepository
                            extends UnovationFilterRepository<PaymentRemittance,String, PaymentRemittanceFilter> {
    List<PaymentRemittance> findByPayerDocumentNumberOrderByCreatedDateTime(String payerDocument);
    Optional<PaymentRemittance> findByPayerDocumentNumberAndSituation(String documentNumber,
                                                                      RemittanceSituation situation);
    Optional<PaymentRemittance> findByNumber(String number);
}
