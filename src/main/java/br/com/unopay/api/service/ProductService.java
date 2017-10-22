package br.com.unopay.api.service;

import br.com.unopay.api.bacen.service.AccreditedNetworkService;
import br.com.unopay.api.bacen.service.IssuerService;
import br.com.unopay.api.bacen.service.PaymentRuleGroupService;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.filter.ProductFilter;
import br.com.unopay.api.repository.ProductRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.PRODUCT_ALREADY_EXISTS;
import static br.com.unopay.api.uaa.exception.Errors.PRODUCT_NOT_FOUND;

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
            checkName(product);
            validateReferences(product);
            return repository.save(product);
        }catch (DataIntegrityViolationException e){
            log.info("Product with code={} already exists", product.getName(), product.getCode(), e);
            throw UnovationExceptions.conflict().withErrors(PRODUCT_ALREADY_EXISTS);
        }
    }

    public void update(String id, Product product) {
        Product current = findById(id);
        validateReferences(product);
        checkName(current, product);
        current.updateMe(product);
        try {
            repository.save(current);
        }catch (DataIntegrityViolationException e){
            log.info("Product with name={} or code={} already exists", product.getName(), product.getCode(), e);
            throw UnovationExceptions.conflict().withErrors(PRODUCT_ALREADY_EXISTS);
        }
    }

    public Product findById(String id) {
        Optional<Product> product = repository.findById(id);
        return product.orElseThrow(() ->
                UnovationExceptions.notFound().withErrors(PRODUCT_NOT_FOUND.withOnlyArgument(id)));
    }

    public Product findByCode(String code) {
        Optional<Product> product = repository.findByCode(code);
        return product.orElseThrow(() ->
                UnovationExceptions.notFound().withErrors(PRODUCT_NOT_FOUND.withOnlyArgument(code)));
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }

    public Page<Product> findByFilter(ProductFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    private void checkName(Product current, Product product) {
        if(!Objects.equals(current.getName(), product.getName())) {
            checkName(product);
        }
    }

    private void checkName(Product product) {
        repository.findByName(product.getName()).ifPresent((ThrowingConsumer) -> {
            throw UnovationExceptions.conflict().withErrors(PRODUCT_ALREADY_EXISTS);
        });
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
