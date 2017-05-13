package br.com.unopay.api.repository;

import br.com.unopay.api.model.ContractEstablishment;
import br.com.unopay.api.model.Credit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractEstablishmentRepository extends CrudRepository<ContractEstablishment,String> {


}

