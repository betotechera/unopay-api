package br.com.unopay.api.service;

import br.com.unopay.api.bacen.service.AccreditedNetworkService;
import br.com.unopay.api.bacen.service.IssuerService;
import br.com.unopay.api.bacen.service.PaymentRuleGroupService;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.filter.ProductFilter;
import br.com.unopay.api.repository.ProductRepository;
import static br.com.unopay.api.uaa.exception.Errors.PRODUCT_ALREADY_EXISTS;
import static br.com.unopay.api.uaa.exception.Errors.PRODUCT_NOT_FOUND;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ProductService {

    ProductRepository repository;
    AccreditedNetworkService accreditedNetworkService;
    IssuerService issuerService;
    PaymentRuleGroupService paymentRuleGroupService;

    @Autowired
    public ProductService(ProductRepository repository,
                          AccreditedNetworkService accreditedNetworkService,
                          IssuerService issuerService,
                          PaymentRuleGroupService paymentRuleGroupService) {
        this.repository = repository;
        this.accreditedNetworkService = accreditedNetworkService;
        this.issuerService = issuerService;
        this.paymentRuleGroupService = paymentRuleGroupService;
    }

    public Product save(Product product) {
        try {
            product.validate();
            validateReferences(product);
            return repository.save(product);
        }catch (DataIntegrityViolationException e){
            log.info("Product with name={} or code={} already exists", product.getName(), product.getCode());
            throw UnovationExceptions.conflict().withErrors(PRODUCT_ALREADY_EXISTS);
        }
    }

    public void update(String id, Product product) {
        Product current = findById(id);
        validateReferences(product);
        current.updateMe(product);
        try {
            repository.save(current);
        }catch (DataIntegrityViolationException e){
            log.info("Product with name={} or code={} already exists", product.getName(), product.getCode());
            throw UnovationExceptions.conflict().withErrors(PRODUCT_ALREADY_EXISTS);
        }
    }

    public Product findById(String id) {
        Optional<Product> product = repository.findById(id);
        return product.orElseThrow(() -> UnovationExceptions.notFound().withErrors(PRODUCT_NOT_FOUND));
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }

    public Page<Product> findByFilter(ProductFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    private void validateReferences(Product product) {
        if(product.getIssuer().getId() != null) {
            product.setIssuer(issuerService.findById(product.getIssuer().getId()));
        }
        if(product.getAccreditedNetwork().getId() != null) {
            product.setAccreditedNetwork(accreditedNetworkService.getById(product.getAccreditedNetwork().getId()));
        }
        if(product.getPaymentRuleGroup().getId() != null) {
            product.setPaymentRuleGroup(paymentRuleGroupService.getById(product.getPaymentRuleGroup().getId()));
        }
    }
}
