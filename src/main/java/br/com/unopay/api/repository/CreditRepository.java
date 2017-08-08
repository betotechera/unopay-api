package br.com.unopay.api.repository;

import br.com.unopay.api.model.Credit;
import br.com.unopay.api.model.CreditInsertionType;
import br.com.unopay.api.model.CreditSituation;
import br.com.unopay.api.model.filter.CreditFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import java.util.Optional;
import java.util.Set;

public interface CreditRepository extends UnovationFilterRepository<Credit,String, CreditFilter> {

    Optional<Credit> findFirstByOrderByCreatedDateTimeDesc();

    Optional<Credit> findById(String id);

    Set<Credit> findByIssuerDocumentAndSituationAndCreditInsertionType(String document, CreditSituation situation,
                                                                       CreditInsertionType insertionType);
}
