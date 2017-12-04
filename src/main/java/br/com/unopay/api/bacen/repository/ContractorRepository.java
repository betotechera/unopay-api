package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.filter.ContractorFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractorRepository extends UnovationFilterRepository<Contractor,String, ContractorFilter> {

    Optional<Contractor> findById(String id);

    Optional<Contractor> findByIdAndContractsHirerId(String id, String hirerId);

    Optional<Contractor> findByPersonDocumentNumber(String document);
}
