package br.com.unopay.api.order.repository;

import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.model.filter.OrderFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OrderRepository extends UnovationFilterRepository<Order,String,OrderFilter> {

    List<Order> findAllByOrderByCreateDateTimeDesc();
    Optional<Order> findFirstByOrderByCreateDateTimeDesc();
    Optional<Order> findById(String id);
    Optional<Order> findByIdAndProductIssuerId(String id, String issuerId);
    Set<Order> findTop20ByPersonPhysicalPersonDetailEmailIgnoreCaseOrderByCreateDateTimeDesc(String email);
}
