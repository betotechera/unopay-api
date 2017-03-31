package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.PaymentBankAccount;
import org.springframework.data.repository.CrudRepository;

public interface PaymentBankAccountRepository  extends CrudRepository<PaymentBankAccount,String> {
}
