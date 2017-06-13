package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.PaymentBankAccount;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface PaymentBankAccountRepository  extends CrudRepository<PaymentBankAccount,String> {

    Optional<PaymentBankAccount> findById(String id);
}
