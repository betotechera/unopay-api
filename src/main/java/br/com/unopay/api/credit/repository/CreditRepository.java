package br.com.unopay.api.credit.repository;

import br.com.unopay.api.credit.model.Credit;
import br.com.unopay.api.credit.model.CreditInsertionType;
import br.com.unopay.api.credit.model.CreditSituation;
import br.com.unopay.api.credit.model.filter.CreditFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.Optional;
import java.util.Set;

public interface CreditRepository extends UnovationFilterRepository<Credit,String, CreditFilter> {

    Optional<Credit> findFirstByOrderByCreatedDateTimeDesc();

    Optional<Credit> findById(String id);

    Set<Credit> findByIssuerDocumentAndSituationAndCreditInsertionType(String document, CreditSituation situation,
                                                                       CreditInsertionType insertionType);
}
