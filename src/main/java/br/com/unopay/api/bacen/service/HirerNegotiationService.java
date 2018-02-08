package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.HirerNegotiation;
import br.com.unopay.api.bacen.repository.HirerNegotiationRepository;
import br.com.unopay.api.service.ProductService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.HIRER_NEGOTIATION_NOT_FOUND;

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

    public HirerNegotiation findByHirerDocument(String document, String productId) {
        Optional<HirerNegotiation> negotiation = repository
                                                    .findByHirerPersonDocumentNumberAndProductId(document, productId);
        return negotiation.orElseThrow(()-> UnovationExceptions.notFound()
                .withErrors(HIRER_NEGOTIATION_NOT_FOUND.withOnlyArgument(document)));
    }

    public void update(String id, HirerNegotiation negotiation) {
        negotiation.validateMe();
        HirerNegotiation current = findById(id);
        current.updateMe(negotiation);
        defineValidReferences(current);
        save(negotiation);
    }

    public HirerNegotiation create(HirerNegotiation negotiation) {
        negotiation.validateMe();
        defineValidReferences(negotiation);
        negotiation.setMeUp();
        return save(negotiation);
    }

    private void defineValidReferences(HirerNegotiation negotiation) {
        negotiation.setHirer(hirerService.getById(negotiation.hirerId()));
        negotiation.setProduct(productService.findById(negotiation.productId()));
    }
}
