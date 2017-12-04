package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.filter.IssuerFilter;
import br.com.unopay.api.bacen.service.IssuerService;
import br.com.unopay.api.billing.remittance.model.filter.RemittanceFilter;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@PreAuthorize("#oauth2.isUser()")
@Timed(prefix = "api")
public class IssuerController {

    private IssuerService service;
    private ProductService productService;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public IssuerController(IssuerService service,
                            ProductService productService) {
        this.service = service;
        this.productService = productService;
    }

    @JsonView(Views.Issuer.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_ISSUER')")
    @RequestMapping(value = "/issuers", method = RequestMethod.POST)
    public ResponseEntity<Issuer> create(@Validated(Create.class) @RequestBody Issuer issuer) {
        log.info("creating issuer {}", issuer);
        Issuer created = service.create(issuer);
        return ResponseEntity
                .created(URI.create("/issuers/"+created.getId()))
                .body(created);

    }

    @JsonView(Views.Issuer.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_ISSUER')")
    @RequestMapping(value = "/issuers/{id}", method = RequestMethod.GET)
    public Issuer get(@PathVariable  String id) {
        log.info("get issuer={}", id);
        return service.findById(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_ISSUER')")
    @RequestMapping(value = "/issuers/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody Issuer issuer) {
        issuer.setId(id);
        log.info("updating issuers {}", issuer);
        service.update(id,issuer);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_ISSUER')")
    @RequestMapping(value = "/issuers/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing issuer id={}", id);
        service.delete(id);
    }

    @JsonView(Views.Issuer.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_ISSUER')")
    @RequestMapping(value = "/issuers", method = RequestMethod.GET)
    public Results<Issuer> getByParams(IssuerFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search issuer with filter={}", filter);
        Page<Issuer> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/issuers", api));
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_MANAGE_PAYMENT_REMITTANCE')")
    @RequestMapping(value = "/issuers/{id}/payment-remittances", method = RequestMethod.POST)
    public void createPaymentRemittance(@PathVariable  String id, @RequestBody RemittanceFilter filter)
    {
        log.info("Executing paymentRemittance for issuerId={} and filter={}", id,filter);
        filter.setId(id);
        service.executePaymentRemittance(filter);
    }

    @JsonView(Views.Issuer.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/me", method = RequestMethod.GET)
    public Issuer getMe(Issuer issuer) {
        log.info("get issuer={}", issuer.documentNumber());
        return service.findById(issuer.getId());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/issuers/me", method = RequestMethod.PUT)
    public void updateMe(Issuer current, @Validated(Update.class) @RequestBody Issuer issuer) {
        log.info("updating issuers={}", issuer);
        service.update(current.getId(),issuer);
    }

    @JsonView(Views.Issuer.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_MANAGE_ISSUER')")
    @RequestMapping(value = "/issuers", method = RequestMethod.GET, params = "currentUser")
    public Results<Issuer> getByMeParams(Issuer issuer, IssuerFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search issuer with filter={}", filter);
        filter.setDocumentNumber(issuer.documentNumber());
        Page<Issuer> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/issuers", api));
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/me/payment-remittances", method = RequestMethod.POST)
    public void createMyPaymentRemittance(Issuer issuer, @RequestBody RemittanceFilter filter)
    {
        log.info("Executing paymentRemittance for issuer={} and filter={}", issuer.documentNumber(),filter);
        filter.setId(issuer.getId());
        service.executePaymentRemittance(filter);
    }


    @JsonView(Views.Product.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/issuers/me/products", method = RequestMethod.POST)
    public ResponseEntity<Product> createProduct(Issuer issuer, @Validated(Create.class) @RequestBody Product product){
        log.info("creating product={} for issuer={}", product, issuer.documentNumber());
        product.setIssuer(issuer);
        Product created = productService.create(product);
        return ResponseEntity
                .created(URI.create("/issuers/me/products/"+created.getId()))
                .body(created);

    }
    @JsonView(Views.Product.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/me/products/{id}", method = RequestMethod.GET)
    public Product getProduct(Issuer issuer, @PathVariable String id) {
        log.info("get product={} for issuer={}", id, issuer.documentNumber());
        return productService.findByIdForIssuer(id, issuer);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/issuers/me/products/{id}", method = RequestMethod.PUT)
    public void updateProduct(Issuer issuer,
                              @PathVariable String id, @Validated(Update.class) @RequestBody Product product) {
        product.setId(id);
        log.info("updating product={} for issuer={}", product, issuer.documentNumber());
        productService.updateForIssuer(id,issuer, product);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/issuers/me/products/{id}", method = RequestMethod.DELETE)
    public void removeProduct(Issuer issuer, @PathVariable  String id) {
        log.info("removing product id={} for issuer={}", id, issuer.documentNumber());
        productService.deleteForIssuer(id, issuer);
    }

    @JsonView(Views.Product.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/me/products", method = RequestMethod.GET)
    public Results<Product> getProductByParams(Issuer issuer,
                                               ProductFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search product with filter={} for issuer={}", filter, issuer.documentNumber());
        filter.setIssuerDocument(issuer.documentNumber());
        Page<Product> page =  productService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/issuers/me/products", api));
    }
}
