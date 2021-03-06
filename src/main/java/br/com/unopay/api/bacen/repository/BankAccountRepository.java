package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.BankAccount;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface BankAccountRepository extends CrudRepository<BankAccount,String> {

    List<BankAccount> findByIdIn(List<String> ids);

    Optional<BankAccount> findById(String id);
}
