package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Bank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BankRepository extends CrudRepository<Bank,Integer> {

    List<Bank> findAll();
}
