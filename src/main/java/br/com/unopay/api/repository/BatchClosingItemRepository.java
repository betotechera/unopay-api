package br.com.unopay.api.repository;

import br.com.unopay.api.model.BatchClosingItem;
import br.com.unopay.api.model.filter.BatchClosingItemFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;

public interface BatchClosingItemRepository extends UnovationFilterRepository<BatchClosingItem,String,
        BatchClosingItemFilter> {
}