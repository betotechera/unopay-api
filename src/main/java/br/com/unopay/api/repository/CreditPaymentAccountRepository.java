package br.com.unopay.api.repository;

import br.com.unopay.api.model.CreditPaymentAccount;
import br.com.unopay.api.model.filter.CreditPaymentAccountFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import java.util.List;
import java.util.Optional;

public interface CreditPaymentAccountRepository
                        extends UnovationFilterRepository<CreditPaymentAccount,String, CreditPaymentAccountFilter> {

    List<CreditPaymentAccount> findByHirerDocument(String hirerDocument);

    Optional<CreditPaymentAccount> findById(String id);

    List<CreditPaymentAccount> findAll();
}
