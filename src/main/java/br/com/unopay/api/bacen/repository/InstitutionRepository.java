package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Institution;
import br.com.unopay.api.bacen.model.InstitutionFilter;
import br.com.unopay.api.repository.UnovationJpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionRepository extends CrudRepository<Institution,String>, UnovationJpaSpecificationExecutor<Institution, InstitutionFilter> {

    Long countByPaymentRuleGroupId(String id);

}
