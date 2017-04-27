package br.com.unopay.api.repository;

import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.filter.ContractFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;


public interface ContractRepository  extends UnovationFilterRepository<Contract,String, ContractFilter> {

}
