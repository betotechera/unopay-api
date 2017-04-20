package br.com.unopay.api.service;

import br.com.unopay.api.model.Product;
import br.com.unopay.api.repository.ProductRepository;
import br.com.unopay.bootcommons.exception.ConflictException;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.unopay.api.uaa.exception.Errors.PRODUCT_ALREADY_EXISTS;
import static br.com.unopay.api.uaa.exception.Errors.PRODUCT_NOT_FOUND;

@Service
public class ProductService {

    @Autowired
    ProductRepository repository;

    public Product save(Product product) {
        try {
            verifyProductExists(product, "");
            return repository.save(product);
        }catch (ConflictException e){
            throw UnovationExceptions.conflict().withErrors(PRODUCT_ALREADY_EXISTS);
        }
    }

    public void update(String id, Product product) {
        Product current = findById(id);
        verifyProductExists(product, id);
        current.setName(product.getName());
        repository.save(current);
        
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

    private void verifyProductExists(Product product, String id) {
        List<Product> products = repository.findByNameOrCodeAndIdNot(product.getName(), product.getCode(), id);
        if (!products.isEmpty()) {
            throw UnovationExceptions.conflict().withErrors(PRODUCT_ALREADY_EXISTS);
        }
    }
}
