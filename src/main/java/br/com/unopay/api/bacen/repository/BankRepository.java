package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Bank;
import br.com.unopay.api.bacen.model.Branch;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BankRepository extends CrudRepository<Bank,Integer> {

    List<Bank> findAll();

    Optional<Bank> findByBacenCode(Integer id);
}
