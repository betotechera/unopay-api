package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.filter.ContractorFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractorRepository extends UnovationFilterRepository<Contractor,String, ContractorFilter> {

    Optional<Contractor> findById(String id);

    Optional<Contractor> findByIdAndContractsHirerId(String id, String hirerId);

    Optional<Contractor> findByIdAndContractsProductAccreditedNetworkId(String id, String networkId);

    Optional<Contractor> findByIdAndContractsId(String id, String contractId);

    Optional<Contractor> findByIdAndContractsProductIssuerId(String id, String issuerId);

    Optional<Contractor> findByIdAndContractsProductIssuerIdIn(String id, Set<String> issuersIds);

    Optional<Contractor> findByPersonDocumentNumber(String document);

}
