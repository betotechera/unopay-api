package br.com.unopay.api.payment.repository;

import br.com.unopay.api.payment.model.PaymentRemittance;
import br.com.unopay.api.payment.model.filter.PaymentRemittanceFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import java.util.Set;

public interface PaymentRemittanceRepository
                            extends UnovationFilterRepository<PaymentRemittance,String, PaymentRemittanceFilter> {
    Set<PaymentRemittance> findByIssuerId(String issuerId);
}
