package br.com.unopay.api.billing.creditcard.repository;

import br.com.unopay.api.billing.creditcard.model.Transaction;
import br.com.unopay.api.billing.creditcard.model.filter.TransactionFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends UnovationFilterRepository<Transaction, String, TransactionFilter> {

    Optional<Transaction> findByOrderId(String orderId);
}
