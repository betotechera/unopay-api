package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.HirerNegotiation;
import br.com.unopay.api.bacen.repository.HirerNegotiationRepository;
import br.com.unopay.api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HirerNegotiationService {

    private HirerNegotiationRepository repository;
    private HirerService hirerService;
    private ProductService productService;

    @Autowired
    public HirerNegotiationService(HirerNegotiationRepository repository,
                                   HirerService hirerService,
                                   ProductService productService) {
        this.repository = repository;
        this.hirerService = hirerService;
        this.productService = productService;
    }

    public HirerNegotiation save(HirerNegotiation negotiation) {
        return repository.save(negotiation);
    }

    public HirerNegotiation findById(String id) {
        return repository.findOne(id);
    }

    public void update(String id, HirerNegotiation negotiation) {
        HirerNegotiation current = findById(id);
        current.updateMe(negotiation);
        defineValidReferences(current);
        save(negotiation);
    }

    public HirerNegotiation create(HirerNegotiation negotiation) {
        defineValidReferences(negotiation);
        return save(negotiation);
    }

    private void defineValidReferences(HirerNegotiation negotiation) {
        negotiation.setHirer(hirerService.getById(negotiation.getHirer().getId()));
        negotiation.setProduct(productService.findById(negotiation.getProduct().getId()));
    }
}
