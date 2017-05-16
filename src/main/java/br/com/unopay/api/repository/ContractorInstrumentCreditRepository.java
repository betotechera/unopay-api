package br.com.unopay.api.repository;

import br.com.unopay.api.model.ContractorInstrumentCredit;
import br.com.unopay.api.model.filter.ContractorInstrumentCreditFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ContractorInstrumentCreditRepository
        extends UnovationFilterRepository<ContractorInstrumentCredit,String, ContractorInstrumentCreditFilter> {

    ContractorInstrumentCredit findFirstByOrderByCreatedDateTimeDesc();

    Optional<ContractorInstrumentCredit> findById(String id);

    Set<ContractorInstrumentCredit> findByContractId(String contractId);
}
