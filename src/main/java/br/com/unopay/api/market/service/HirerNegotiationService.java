package br.com.unopay.api.market.service;

import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.service.HirerService;
import br.com.unopay.api.market.model.HirerNegotiation;
import br.com.unopay.api.market.model.filter.HirerNegotiationFilter;
import br.com.unopay.api.market.repository.HirerNegotiationRepository;
import br.com.unopay.api.service.ProductService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public HirerNegotiation findByIdForHirer(String id, Hirer hirer) {
        Optional<HirerNegotiation> negotiation = repository.findByIdAndHirerId(id, hirer.getId());
        return negotiation.orElseThrow(()->
                UnovationExceptions.notFound().withErrors(HIRER_NEGOTIATION_NOT_FOUND.withOnlyArgument(id)));
    }

    public HirerNegotiation findByHirerId(String hirerId) {
        Optional<HirerNegotiation> negotiation = repository.findByHirerId(hirerId);
        return negotiation.orElseThrow(()->
                UnovationExceptions.notFound().withErrors(HIRER_NEGOTIATION_NOT_FOUND));
    }

    public HirerNegotiation findByHirerIdSilent(String hirerId) {
        return repository.findFirstByHirerId(hirerId);
    }

    public HirerNegotiation findByHirerDocument(String document, String productId) {
        Optional<HirerNegotiation> negotiation = repository
                                                    .findByHirerPersonDocumentNumberAndProductId(document, productId);
        return negotiation.orElseThrow(()-> UnovationExceptions.notFound()
                .withErrors(HIRER_NEGOTIATION_NOT_FOUND.withOnlyArgument(document)));
    }

    public void update(String id, HirerNegotiation negotiation) {
        HirerNegotiation current = findById(id);
        update(negotiation, current);
    }

    public void updateForHirer(String id,Hirer hirer, HirerNegotiation negotiation) {
        HirerNegotiation current = findByIdForHirer(id, hirer);
        update(negotiation, current);
    }
    private void update(HirerNegotiation negotiation, HirerNegotiation current) {
        negotiation.validateMe();
        current.updateMe(negotiation);
        defineValidReferences(current);
        save(current);
    }

    private void defineValidReferences(HirerNegotiation negotiation) {
        negotiation.setHirer(hirerService.getById(negotiation.hirerId()));
        negotiation.setProduct(productService.findById(negotiation.productId()));
    }

    public HirerNegotiation create(HirerNegotiation negotiation) {
        negotiation.validateMe();
        defineValidReferences(negotiation);
        negotiation.setMeUp();
        return save(negotiation);
    }

    public Page<HirerNegotiation> findByFilter(HirerNegotiationFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

}
