package br.com.unopay.api.repository;

import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.filter.ProductFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;

public interface ProductRepository  extends UnovationFilterRepository<Product,String, ProductFilter> {

}
