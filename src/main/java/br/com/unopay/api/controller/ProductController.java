package br.com.unopay.api.controller;

import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.filter.ProductFilter;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.service.ProductService;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import java.net.URI;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@Timed(prefix = "api")
public class ProductController {

    @Value("${unopay.api}")
    private String api;

    private ProductService service;

    @Autowired
    public ProductController(ProductService service) {
        this.service = service;
    }

    @JsonView(Views.Product.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_PRODUCT')")
    @RequestMapping(value = "/products", method = RequestMethod.POST)
    public ResponseEntity<Product> create(@Validated(Create.class) @RequestBody Product product) {
        log.info("creating product {}", product);
        Product created = service.create(product);
        return ResponseEntity
                .created(URI.create("/products/"+created.getId()))
                .body(created);

    }
    @JsonView(Views.Product.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_PRODUCT')")
    @RequestMapping(value = "/products/{id}", method = RequestMethod.GET)
    public Product get(@PathVariable String id) {
        log.info("get product={}", id);
        return service.findById(id);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_PRODUCT')")
    @RequestMapping(value = "/products/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody Product product) {
        product.setId(id);
        log.info("updating product {}", product);
        service.update(id,product);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_PRODUCT')")
    @RequestMapping(value = "/products/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing product id={}", id);
        service.delete(id);
    }

    @JsonView(Views.Product.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/products")
    public Results<Product> getByParams(ProductFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search product with filter={}", filter);
        Page<Product> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/products", api));
    }

    @JsonView(Views.Product.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/products/menu")
    List<Product> listForMenu() {
        return service.listForMenu();
    }

}
