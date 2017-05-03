package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.PaymentBankAccount;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PaymentBankAccountRepository  extends CrudRepository<PaymentBankAccount,String> {

    Optional<PaymentBankAccount> findById(String id);
}
