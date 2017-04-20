package br.com.unopay.api.repository;

import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.filter.ProductFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;

import java.util.List;

public interface ProductRepository  extends UnovationFilterRepository<Product,String, ProductFilter> {

    List<Product> findByNameOrCodeAndIdNot(String name, String code, String id);
}
