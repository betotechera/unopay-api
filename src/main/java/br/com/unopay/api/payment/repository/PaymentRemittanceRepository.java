package br.com.unopay.api.payment.repository;

import br.com.unopay.api.payment.model.PaymentRemittance;
import br.com.unopay.api.payment.model.RemittanceSituation;
import br.com.unopay.api.payment.model.filter.PaymentRemittanceFilter;
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
