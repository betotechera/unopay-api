package br.com.unopay.api.credit.repository;

import br.com.unopay.api.credit.model.CreditPaymentAccount;
import br.com.unopay.api.credit.model.filter.CreditPaymentAccountFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.List;
import java.util.Optional;

public interface CreditPaymentAccountRepository
                        extends UnovationFilterRepository<CreditPaymentAccount,String, CreditPaymentAccountFilter> {

    List<CreditPaymentAccount> findByHirerDocument(String hirerDocument);

    Optional<CreditPaymentAccount> findById(String id);

    List<CreditPaymentAccount> findAll();
}
