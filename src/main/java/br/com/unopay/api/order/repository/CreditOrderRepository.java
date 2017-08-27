package br.com.unopay.api.order.repository;

import br.com.unopay.api.order.model.CreditOrder;
import br.com.unopay.api.order.model.filter.OrderFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;

public interface CreditOrderRepository extends UnovationFilterRepository<CreditOrder,String,OrderFilter>{
}
