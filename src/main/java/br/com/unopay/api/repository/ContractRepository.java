package br.com.unopay.api.repository;

import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.ContractSituation;
import br.com.unopay.api.model.filter.ContractFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface ContractRepository  extends UnovationFilterRepository<Contract,String, ContractFilter> {

    List<Contract> findByContractorId(String contractorId);

    List<Contract> findByEstablishmentsId(String establishmentId);

    List<Contract> findByHirerPersonDocumentNumber(String hirerDocument);

    Set<Contract> findByHirerIdAndSituation(String hirerId, ContractSituation situation);

    Optional<Contract> findByContractorPersonDocumentNumberAndProductCode(String document, String productId);

    Optional<Contract> findById(String id);

    Optional<Contract> findByCode(Long code);

    Optional<Contract> findByIdAndHirerId(String id, String hirerId);

    Optional<Contract> findByIdAndProductIssuerId(String id, String issuerId);

}
