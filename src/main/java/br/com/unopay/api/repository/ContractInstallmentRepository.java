package br.com.unopay.api.repository;

import br.com.unopay.api.model.ContractInstallment;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface ContractInstallmentRepository
        extends CrudRepository<ContractInstallment, String> {

    Optional<ContractInstallment> findById(String id);

}
