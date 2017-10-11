package br.com.unopay.api.billing.boleto.service;

import br.com.unopay.api.billing.boleto.model.Boleto;
import br.com.unopay.api.billing.boleto.repository.BoletoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoletoService {

    private BoletoRepository repository;

    @Autowired
    public BoletoService(BoletoRepository repository) {
        this.repository = repository;
    }

    public Boleto save(Boleto boleto) {
        return repository.save(boleto);
    }

    public Boleto findById(String id) {
        return repository.findOne(id);
    }
}
