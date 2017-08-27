package br.com.unopay.api.billing.repository;

import br.com.unopay.api.billing.model.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, String>{
}
