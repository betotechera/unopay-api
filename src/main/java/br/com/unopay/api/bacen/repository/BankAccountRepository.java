package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.BankAccount;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BankAccountRepository extends CrudRepository<BankAccount,String> {

    List<BankAccount> findByIdIn(List<String> ids);
}
