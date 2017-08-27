package br.com.unopay.api.order.service;

import br.com.unopay.api.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private OrderRepository repository;

    public OrderService(){}

    @Autowired
    public OrderService(OrderRepository repository){
        this.repository = repository;
    }
}
