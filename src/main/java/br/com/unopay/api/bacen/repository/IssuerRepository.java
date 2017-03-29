package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.IssuerFilter;
import br.com.unopay.api.repository.UnovationJpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface IssuerRepository extends CrudRepository<Issuer,String>, UnovationJpaSpecificationExecutor<Issuer, IssuerFilter> {
}
