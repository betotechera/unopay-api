package br.com.unopay.api.repository;

import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.model.filter.BatchClosingFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;

public interface BatchClosingRepository extends UnovationFilterRepository<BatchClosing,String, BatchClosingFilter> {
}
