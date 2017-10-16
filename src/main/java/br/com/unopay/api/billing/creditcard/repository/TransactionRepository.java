package br.com.unopay.api.billing.creditcard.repository;

import br.com.unopay.api.billing.creditcard.model.Transaction;
import br.com.unopay.api.billing.creditcard.model.filter.TransactionFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.Optional;

public interface TransactionRepository extends UnovationFilterRepository<Transaction, String, TransactionFilter> {

    Optional<Transaction> findByOrderId(String orderId);
}
