package br.com.unopay.api.order.service;

import br.com.unopay.api.order.model.CreditOrder;
import br.com.unopay.api.order.repository.CreditOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreditOrderService {

    private CreditOrderRepository repository;

    public CreditOrderService(){}

    @Autowired
    public CreditOrderService(CreditOrderRepository repository){
        this.repository = repository;
    }

    public CreditOrder save(CreditOrder creditOrder) {
        return repository.save(creditOrder);
    }

    public CreditOrder findById(String id) {
        return repository.findOne(id);
    }
}
