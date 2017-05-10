package br.com.unopay.api.repository;

import br.com.unopay.api.model.CreditPaymentAccount;
import br.com.unopay.api.model.filter.PaymentAccountFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;

import java.util.List;

public interface CreditPaymentAccountRepository
                        extends UnovationFilterRepository<CreditPaymentAccount,String, PaymentAccountFilter> {

    List<CreditPaymentAccount> findByHirerDocument(String hirerDocument);
}
