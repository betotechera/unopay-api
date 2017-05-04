package br.com.unopay.api.repository;

import br.com.unopay.api.model.ContractEstablishment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractEstablishmentRepository extends CrudRepository<ContractEstablishment,String> {}

