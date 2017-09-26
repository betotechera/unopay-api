package br.com.unopay.api.order.repository;

import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.model.filter.OrderFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends UnovationFilterRepository<Order,String,OrderFilter>{

    List<Order> findAllByOrderByCreateDateTimeDesc();
    Optional<Order> findFirstByOrderByCreateDateTimeDesc();
    Optional<Order> findById(String id);
}
