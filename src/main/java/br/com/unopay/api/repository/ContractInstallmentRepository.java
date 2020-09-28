package br.com.unopay.api.repository;

import br.com.unopay.api.model.ContractInstallment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

public interface ContractInstallmentRepository
        extends CrudRepository<ContractInstallment, String> {

    Optional<ContractInstallment> findById(String id);
    Set<ContractInstallment> findByContractId(String contractId);

    @Query("SELECT c FROM ContractInstallment c WHERE c.expiration  >=  ?1 and c.expiration  <= ?2 and paymentDateTime is null")
    Set<ContractInstallment> findInstallmentAboutToExpire(Date expiration, Date expirationEnd);

    @Query("SELECT c FROM ContractInstallment c WHERE paymentDateTime is null and installmentNumber = 1 and c.contract.recurrencePaymentMethod = 'CARD'")
    Set<ContractInstallment> findAllNotPaidInstallments();

}
