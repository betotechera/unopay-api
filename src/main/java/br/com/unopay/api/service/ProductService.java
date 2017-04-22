package br.com.unopay.api.service;

import br.com.unopay.api.model.Product;
import br.com.unopay.api.repository.ProductRepository;
import br.com.unopay.bootcommons.exception.ConflictException;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.PRODUCT_ALREADY_EXISTS;
import static br.com.unopay.api.uaa.exception.Errors.PRODUCT_NOT_FOUND;

@Service
@Slf4j
public class ProductService {

    @Autowired
    ProductRepository repository;

    public Product save(Product product) {
        try {
            return repository.save(product);
        }catch (DataIntegrityViolationException e){
            log.info("Product with name={} or code={} already exists", product.getName(), product.getCode());
            throw UnovationExceptions.conflict().withErrors(PRODUCT_ALREADY_EXISTS);
        }
    }

    public void update(String id, Product product) {
        Product current = findById(id);
        current.setName(product.getName());
        try {
            repository.save(current);
        }catch (DataIntegrityViolationException e){
            log.info("Product with name={} or code={} already exists", product.getName(), product.getCode());
            throw UnovationExceptions.conflict().withErrors(PRODUCT_ALREADY_EXISTS);
        }
        
    }

    public Product findById(String id) {
        Product product = repository.findOne(id);
        if(product == null){
            throw UnovationExceptions.notFound().withErrors(PRODUCT_NOT_FOUND);
        }
        return product;
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);

    }
}
