package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.AccreditedNetworkIssuer;
import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.bacen.model.filter.AccreditedNetworkFilter;
import br.com.unopay.api.bacen.model.filter.ContractorFilter;
import br.com.unopay.api.bacen.model.filter.IssuerFilter;
import br.com.unopay.api.bacen.service.AccreditedNetworkIssuerService;
import br.com.unopay.api.bacen.service.AccreditedNetworkService;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.bacen.service.IssuerService;
import br.com.unopay.api.billing.remittance.model.PaymentRemittance;
import br.com.unopay.api.billing.remittance.model.filter.PaymentRemittanceFilter;
import br.com.unopay.api.billing.remittance.model.filter.RemittanceFilter;
import br.com.unopay.api.billing.remittance.service.PaymentRemittanceService;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.filter.PaymentInstrumentFilter;
import br.com.unopay.api.model.filter.ProductFilter;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.model.filter.OrderFilter;
import br.com.unopay.api.order.service.OrderService;
import br.com.unopay.api.service.PaymentInstrumentService;
import br.com.unopay.api.service.ProductService;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Sets;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Slf4j
@RestController
@PreAuthorize("#oauth2.isUser()")
@Timed(prefix = "api")
public class IssuerController {

    private IssuerService service;
    private ProductService productService;
    private PaymentRemittanceService paymentRemittanceService;
    private ContractorService contractorService;
    private PaymentInstrumentService paymentInstrumentService;
    private OrderService orderService;
    private AccreditedNetworkIssuerService networkIssuerService;
    private AccreditedNetworkService networkService;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public IssuerController(IssuerService service,
                            ProductService productService,
                            PaymentRemittanceService paymentRemittanceService,
                            ContractorService contractorService,
                            PaymentInstrumentService paymentInstrumentService,
                            OrderService orderService,
                            AccreditedNetworkIssuerService networkIssuerService,
                            AccreditedNetworkService networkService) {
        this.service = service;
        this.productService = productService;
        this.paymentRemittanceService = paymentRemittanceService;
        this.contractorService = contractorService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.orderService = orderService;
        this.networkIssuerService = networkIssuerService;
        this.networkService = networkService;
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

    @JsonView(Views.Issuer.AccreditedNetwork.class)
    @ResponseStatus(CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_ISSUER')")
    @RequestMapping(value = "/issuers/{id}/accredited-networks", method = POST)
    public ResponseEntity<AccreditedNetworkIssuer> enableAllNetowrk(@PathVariable  String id,
                                                                    OAuth2Authentication authentication,
                                                                 @Validated(Create.class)
                                                                 @RequestBody AccreditedNetworkIssuer networkIssuer) {
        log.info("Enabling  network={} for Issuer={}", networkIssuer
                .getAccreditedNetwork().documentNumber(), id);
        networkIssuer.setIssuer(new Issuer() {{ setId(id); }});
        AccreditedNetworkIssuer created = networkIssuerService.create(authentication.getName(), networkIssuer);
        return created(URI.create("/issuers/"+id+"/accredited-networks/"+created.getId())).body(created);
    }


    @JsonView(Views.Order.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_MANAGE_ISSUER')")
    @RequestMapping(value = "/issuers/{id}/accredited-networks", method = RequestMethod.GET)
    public Results<AccreditedNetwork> getAllNetworksByParams(@PathVariable  String id, AccreditedNetworkFilter filter,
                                                          @Validated UnovationPageRequest pageable){
        log.info("search network with filter={} for issuer={}", filter, id);
        filter.setIssuer(id);
        Page<AccreditedNetwork> page =  networkService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/issuers/%s/accredited-networks", api, id));
    }

    @JsonView(Views.Issuer.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("#oauth2.isClient()")
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
        paymentRemittanceService.execute(filter);
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

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/me/payment-remittances", method = RequestMethod.POST)
    public void createMyPaymentRemittance(Issuer issuer, @RequestBody RemittanceFilter filter)
    {
        log.info("Executing paymentRemittance for issuer={} and filter={}", issuer.documentNumber(),filter);
        filter.setId(issuer.getId());
        paymentRemittanceService.execute(filter);
    }

    @ResponseStatus(OK)
    @JsonView(Views.PaymentRemittance.List.class)
    @RequestMapping(value = "/issuers/me/payment-remittances", method = GET)
    public Results<PaymentRemittance> findMyByFilter(Issuer issuer,
                                                     PaymentRemittanceFilter filter,
                                                     @Validated UnovationPageRequest pageable) {
        log.info("search PaymentRemittance with filter={} for issuer={}", filter, issuer.documentNumber());
        filter.setIssuer(issuer.documentNumber());
        Page<PaymentRemittance> page =  paymentRemittanceService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/issuers/me/payment-remittances", api));
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

    @JsonView(Views.Contractor.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/me/contractors/{id}", method = RequestMethod.GET)
    public Contractor getContractor(Issuer issuer, @PathVariable  String id) {
        log.info("get Contractor={} for issuer={}", id, issuer.documentNumber());
        return contractorService.getByIdForIssuer(id, issuer);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/issuers/me/contractors/{id}", method = RequestMethod.PUT)
    public void updateContractor(Issuer issuer,
                                 @PathVariable String id,
                                 @Validated(Update.class) @RequestBody Contractor contractor){
        contractor.setId(id);
        log.info("updating contractor={} for issuer={}", contractor, issuer.documentNumber());
        contractorService.updateForIssuer(id, Sets.newHashSet(issuer.getId()), contractor);
    }

    @JsonView(Views.Contractor.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/me/contractors", method = RequestMethod.GET)
    public Results<Contractor> getContractorsByParams(Issuer issuer, ContractorFilter filter,
                                                      @Validated UnovationPageRequest pageable){
        log.info("search Contractor with filter={} for issuer={}", filter, issuer.documentNumber());
        filter.setIssuer(issuer.getId());
        Page<Contractor> page =  contractorService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/issuers/me/contractors", api));
    }

    @JsonView(Views.PaymentInstrument.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/issuers/me/payment-instruments", method = RequestMethod.POST)
    public ResponseEntity<PaymentInstrument> createPaymentInstrument(Issuer issuer, @Validated(Create.class)
                                                    @RequestBody PaymentInstrument paymentInstrument) {
        log.info("creating paymentInstrument {}", paymentInstrument);
        PaymentInstrument created = paymentInstrumentService.createForIssuer(issuer, paymentInstrument);
        return ResponseEntity
                .created(URI.create("/issuers/me/payment-instruments/"+created.getId()))
                .body(created);
    }

    @JsonView(Views.PaymentInstrument.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/me/payment-instruments/{id}", method = RequestMethod.GET)
    public PaymentInstrument getPaymentInstrument(Issuer issuer, @PathVariable String id) {
        log.info("get paymentInstrument={} for issuer={}", id, issuer.documentNumber());
        return paymentInstrumentService.findByIdForIssuer(id, issuer);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/issuers/me/payment-instruments/{id}", method = RequestMethod.PUT)
    public void update(Issuer issuer, @PathVariable  String id,
                       @Validated(Update.class) @RequestBody PaymentInstrument paymentInstrument) {
        paymentInstrument.setId(id);
        log.info("updating paymentInstrument={} for issuer={}", paymentInstrument, issuer.documentNumber());
        paymentInstrumentService.updateForIssuer(id, issuer, paymentInstrument);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/issuers/me/payment-instruments/{id}", method = RequestMethod.DELETE)
    public void removePaymentInstrument(Issuer issuer, @PathVariable  String id) {
        log.info("removing paymentInstrument id={} for issuer={}", id, issuer.documentNumber());
        paymentInstrumentService.deleteForIssuer(id, issuer);
    }

    @JsonView(Views.PaymentInstrument.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/me/payment-instruments", method = RequestMethod.GET)
    public Results<PaymentInstrument> getPaymentInstrumentByParams(Issuer issuer, PaymentInstrumentFilter filter,
                                                  @Validated UnovationPageRequest pageable) {
        log.info("search paymentInstrument with filter={}", filter);
        filter.setIssuer(Sets.newHashSet(issuer.getId()));
        Page<PaymentInstrument> page =  paymentInstrumentService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/issuers/me/payment-instruments", api));
    }

    @JsonView(Views.Order.Detail.class)
    @ResponseStatus(CREATED)
    @RequestMapping(value = "/issuers/me/orders", method = POST)
    public ResponseEntity<Order> createOrder(Issuer issuer,
                                             @Validated(Create.Order.Adhesion.class) @RequestBody Order order) {
        log.info("creating order {}", order);
        Order created = orderService.createForIssuer(issuer, order);
        return created(URI.create("/issuers/me/credit-orders/"+created.getId())).body(created);
    }

    @ResponseStatus(OK)
    @JsonView(Views.Order.Detail.class)
    @RequestMapping(value = "/issuers/me/orders/{id}", method = GET)
    public Order getOrder(Issuer issuer, @PathVariable String id) {
        log.info("get order={}", id);
        return orderService.findByIdForIssuer(id, issuer);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/issuers/me/orders/{id}", method = PUT)
    public void updateOrder(Issuer issuer, @PathVariable String id, @RequestBody Order order) {
        log.info("update order={}", id);
        orderService.updateForIssuer(id, issuer, order);
    }

    @JsonView(Views.Order.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/me/orders", method = RequestMethod.GET)
    public Results<Order> getOrderByParams(Issuer issuer, OrderFilter filter, @Validated UnovationPageRequest pageable){
        log.info("search order with filter={} for issuer={}", filter, issuer.documentNumber());
        filter.setIssuer(issuer.getId());
        Page<Order> page =  orderService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/issuers/me/orders", api));
    }

    @JsonView(Views.Issuer.AccreditedNetwork.class)
    @ResponseStatus(CREATED)
    @RequestMapping(value = "/issuers/me/accredited-networks", method = POST)
    public ResponseEntity<AccreditedNetworkIssuer> enableNetowrk(Issuer issuer, OAuth2Authentication authentication,
                                                                 @Validated(Create.class)
                                                                 @RequestBody AccreditedNetworkIssuer networkIssuer) {
        log.info("Enabling  network={} for Issuer={}", networkIssuer
                .getAccreditedNetwork().documentNumber(), issuer.documentNumber());
        networkIssuer.setIssuer(issuer);
        AccreditedNetworkIssuer created = networkIssuerService.create(authentication.getName(), networkIssuer);
        return created(URI.create("/issuers/me/accredited-networks/"+created.getId())).body(created);
    }


    @JsonView(Views.Order.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/me/accredited-networks", method = RequestMethod.GET)
    public Results<AccreditedNetwork> getNetworksByParams(Issuer issuer, AccreditedNetworkFilter filter,
                                                                @Validated UnovationPageRequest pageable){
        log.info("search network with filter={} for issuer={}", filter, issuer.documentNumber());
        filter.setIssuer(issuer.getId());
        Page<AccreditedNetwork> page =  networkService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/issuers/me/accredited-networks", api));
    }


}
