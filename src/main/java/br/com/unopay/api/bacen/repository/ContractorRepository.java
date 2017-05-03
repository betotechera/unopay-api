package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.filter.ContractorFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractorRepository extends UnovationFilterRepository<Contractor,String, ContractorFilter> {

    Optional<Contractor> findById(String id);
}
