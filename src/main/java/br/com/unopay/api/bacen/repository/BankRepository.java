package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Bank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface BankRepository extends CrudRepository<Bank,Integer> {

    Page<Bank> findAll(Pageable pageable);
}
