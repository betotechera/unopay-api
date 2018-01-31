package br.com.unopay.api.credit.repository;

import br.com.unopay.api.credit.model.ContractorCreditRecurrence;
import br.com.unopay.api.credit.model.filter.ContractorCreditRecurrenceFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;

public interface ContractorCreditRecurrenceRepository
        extends UnovationFilterRepository<ContractorCreditRecurrence,String, ContractorCreditRecurrenceFilter> {
}
