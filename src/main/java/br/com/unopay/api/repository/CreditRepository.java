package br.com.unopay.api.repository;

import br.com.unopay.api.model.Credit;
import br.com.unopay.api.model.filter.CreditFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;

import java.util.Optional;

public interface CreditRepository extends UnovationFilterRepository<Credit,String, CreditFilter> {

    Optional<Credit> findFirstByOrderByCreatedDateTimeDesc();
}
