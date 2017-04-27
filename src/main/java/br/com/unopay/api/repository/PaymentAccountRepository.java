package br.com.unopay.api.repository;

import br.com.unopay.api.model.PaymentAccount;
import br.com.unopay.api.model.filter.PaymentAccountFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;

public interface PaymentAccountRepository
                        extends UnovationFilterRepository<PaymentAccount,String, PaymentAccountFilter> {
}
