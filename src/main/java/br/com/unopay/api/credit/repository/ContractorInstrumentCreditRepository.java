package br.com.unopay.api.credit.repository;

import br.com.unopay.api.bacen.model.ServiceType;
import br.com.unopay.api.credit.model.ContractorCreditType;
import br.com.unopay.api.model.ContractorInstrumentCredit;
import br.com.unopay.api.credit.model.CreditSituation;
import br.com.unopay.api.credit.model.filter.ContractorInstrumentCreditFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractorInstrumentCreditRepository
        extends UnovationFilterRepository<ContractorInstrumentCredit,String, ContractorInstrumentCreditFilter> {

    ContractorInstrumentCredit findFirstByServiceTypeAndContractIdOrderByCreatedDateTimeDesc(ServiceType type,
                                                                                             String productId);

    Optional<ContractorInstrumentCredit> findById(String id);

    Set<ContractorInstrumentCredit> findByContractId(String contractId);

    Optional<ContractorInstrumentCredit> findByPaymentInstrumentContractorId(String contractorId);

    Page<ContractorInstrumentCredit> findByContractIdAndContractContractorPersonDocumentNumber(String contractId,
                                                                                               String contractorId,
                                                                                               Pageable pageable);
}
