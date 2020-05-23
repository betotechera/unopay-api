package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.network.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.AccreditedNetworkIssuer;
import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.network.model.filter.AccreditedNetworkFilter;
import br.com.unopay.api.bacen.model.filter.ContractorFilter;
import br.com.unopay.api.bacen.model.filter.HirerFilter;
import br.com.unopay.api.bacen.model.filter.IssuerFilter;
import br.com.unopay.api.bacen.service.AccreditedNetworkIssuerService;
import br.com.unopay.api.network.service.AccreditedNetworkService;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.bacen.service.HirerService;
import br.com.unopay.api.bacen.service.IssuerService;
import br.com.unopay.api.billing.boleto.model.Ticket;
import br.com.unopay.api.billing.boleto.model.filter.TicketFilter;
import br.com.unopay.api.billing.boleto.service.TicketService;
import br.com.unopay.api.billing.remittance.model.PaymentRemittance;
import br.com.unopay.api.billing.remittance.model.filter.PaymentRemittanceFilter;
import br.com.unopay.api.billing.remittance.model.filter.RemittanceFilter;
import br.com.unopay.api.billing.remittance.service.PaymentRemittanceService;
import br.com.unopay.api.market.model.BonusBilling;
import br.com.unopay.api.market.model.HirerForIssuer;
import br.com.unopay.api.market.model.HirerNegotiation;
import br.com.unopay.api.market.model.NegotiationBilling;
import br.com.unopay.api.market.model.filter.BonusBillingFilter;
import br.com.unopay.api.market.model.filter.HirerNegotiationFilter;
import br.com.unopay.api.market.model.filter.NegotiationBillingFilter;
import br.com.unopay.api.market.service.BonusBillingService;
import br.com.unopay.api.market.service.HirerNegotiationService;
import br.com.unopay.api.market.service.NegotiationBillingService;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.model.filter.ContractFilter;
import br.com.unopay.api.model.filter.PaymentInstrumentFilter;
import br.com.unopay.api.model.filter.ProductFilter;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.model.filter.OrderFilter;
import br.com.unopay.api.order.service.OrderService;
import br.com.unopay.api.service.ContractService;
import br.com.unopay.api.service.PaymentInstrumentService;
import br.com.unopay.api.service.ProductService;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Sets;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    private HirerNegotiationService hirerNegotiationService;
    private HirerService hirerService;
    private NegotiationBillingService negotiationBillingService;
    private TicketService ticketService;
    private ContractService contractService;
    private BonusBillingService bonusBillingService;

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
                            AccreditedNetworkService networkService,
                            HirerNegotiationService hirerNegotiationService,
                            HirerService hirerService,
                            NegotiationBillingService negotiationBillingService,
                            TicketService ticketService,
                            ContractService contractService,
                            BonusBillingService bonusBillingService) {
        this.service = service;
        this.productService = productService;
        this.paymentRemittanceService = paymentRemittanceService;
        this.contractorService = contractorService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.orderService = orderService;
        this.networkIssuerService = networkIssuerService;
        this.networkService = networkService;
        this.hirerNegotiationService = hirerNegotiationService;
        this.hirerService = hirerService;
        this.negotiationBillingService = negotiationBillingService;
        this.ticketService = ticketService;
        this.contractService = contractService;
        this.bonusBillingService = bonusBillingService;
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
    @PreAuthorize("isAuthenticated()")
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
        service.updateMe(current.getId(),issuer);
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
        log.info("creating a product={} for the issuer={}", product, issuer.documentNumber());
        product.setIssuer(issuer);
        Product created = productService.create(product, issuer);
        return ResponseEntity
                .created(URI.create("/issuers/me/products/"+created.getId()))
                .body(created);

    }
    @JsonView(Views.Product.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/me/products/{id}", method = RequestMethod.GET)
    public Product getProduct(Issuer issuer, @PathVariable String id) {
        log.info("getting the product={} for the issuer={}", id, issuer.documentNumber());
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

    @ResponseStatus(OK)
    @RequestMapping(value = "/issuers/me/tickets/return-files", method = POST)
    public void processReturn(Issuer issuer, @RequestParam MultipartFile file) {
        ticketService.processTicketReturnForIssuer(issuer, file);
    }

    @JsonView(Views.Ticket.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/me/tickets", method = RequestMethod.GET)
    public Results<Ticket> getTickets(Issuer issuer, TicketFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("get tickets for issuer={}", issuer.documentNumber());
        filter.setIssuerDocument(issuer.documentNumber());
        Page<Ticket> page = ticketService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/issuers/me/tickets", api));
    }

    @JsonView(Views.Ticket.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/me/tickets/{id}", method = RequestMethod.GET)
    public Ticket getTicket(Issuer issuer, @PathVariable  String id) {
        log.info("get ticket={} for issuer={}", id, issuer.documentNumber());
        return ticketService.getByIdForIssuer(id, issuer);
    }

    @JsonView(Views.HirerNegotiation.Detail.class)
    @ResponseStatus(CREATED)
    @RequestMapping(value = "/issuers/me/hirer-negotiations", method = POST)
    public ResponseEntity<HirerNegotiation> create(Issuer issuer,
                                                   @Validated(Create.class) @RequestBody HirerForIssuer negotiation){
        log.info("creating hirer and negotiation={} for issuer={}", negotiation, issuer.documentNumber());
        HirerNegotiation created = hirerNegotiationService.crateHirerAndNegotiation(issuer, negotiation);
        log.info("created negotiation={}", created);
        return created(URI.create(String
                .format("/hirer-negotiations/%s",created.getId()))).body(created);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/issuers/me/hirer-negotiations/{id}", method = PUT)
    public void update(Issuer issuer,@PathVariable String id,
                       @Validated(Create.class) @RequestBody HirerNegotiation negotiation){
        log.info("updating negotiation={} for issuer={}", negotiation, issuer.documentNumber());
        hirerNegotiationService.updateForIssuer(id,issuer, negotiation);
    }

    @JsonView(Views.HirerNegotiation.Detail.class)
    @ResponseStatus(OK)
    @RequestMapping(value = "/issuers/me/hirer-negotiations/{id}", method = GET)
    public HirerNegotiation getNegotiation(Issuer issuer, @PathVariable String id) {
        log.info("get negotiation={} for issuer={}", id, issuer.documentNumber());
        return hirerNegotiationService.findByIdForIssuer(id, issuer);
    }

    @JsonView(Views.HirerNegotiation.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/me/hirer-negotiations", method = RequestMethod.GET)
    public Results<HirerNegotiation> getByParams(Issuer issuer,HirerNegotiationFilter filter,
                                                 @Validated UnovationPageRequest pageable) {
        log.info("search negotiation for issuer={} with filter={}", issuer.documentNumber(), filter);
        filter.setIssuer(issuer.getId());
        Page<HirerNegotiation> page =  hirerNegotiationService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/issuers/me/hirer-negotiations", api));
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/issuers/me/hirer-negotiations/{id}/negotiation-billings", method = PUT)
    public void processBilling(Issuer issuer,@PathVariable String id){
        log.info("process negotiation={} billing for issuer={}",id, issuer.documentNumber());
        negotiationBillingService.processForIssuer(id,issuer);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/issuers/me/bonus-billings", method = PUT)
    public void processAllBonusBillings(Issuer issuer){
        log.info("process all bonus billing for issuer={}", issuer.documentNumber());
        bonusBillingService.processForIssuer(issuer.getId());
    }

    @JsonView(Views.BonusBilling.Detail.class)
    @ResponseStatus(OK)
    @RequestMapping(value = "issuers/me/bonus-billings/{id}", method = GET)
    public BonusBilling getBonusBilling(Issuer issuer, @PathVariable String id) {
        log.info("get bonus billing={} for issuer={}", id, issuer.documentNumber());
        return bonusBillingService.findByIdForIssuer(id, issuer);
    }

    @JsonView(Views.BonusBilling.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/me/bonus-billings", method = RequestMethod.GET)
    public Results<BonusBilling> getAllBonusBillings(Issuer issuer, BonusBillingFilter filter,
                                                     @Validated UnovationPageRequest pageable) {
        log.info("find BonusBillings for issuer={}", issuer.documentNumber());
        filter.setIssuer(issuer.documentNumber());
        Page<BonusBilling> page =  bonusBillingService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/issuers/me/bonus-billings", api));
    }

    @JsonView(Views.NegotiationBilling.Detail.class)
    @ResponseStatus(OK)
    @RequestMapping(value = "issuers/me/hirer-negotiation-billings/{id}", method = GET)
    public NegotiationBilling getBilling(Issuer issuer, @PathVariable String id) {
        log.info("get negotiation billing={} for issuer={}", id, issuer.documentNumber());
        return negotiationBillingService.findByIdForIssuer(id, issuer);
    }

    @JsonView(Views.NegotiationBilling.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/me/hirer-negotiation-billings", method = RequestMethod.GET)
    public Results<NegotiationBilling> getByParamsBilling(Issuer issuer,NegotiationBillingFilter filter,
                                                 @Validated UnovationPageRequest pageable) {
        log.info("search negotiation billing for issuer={} with filter={}", issuer.documentNumber(), filter);
        filter.setIssuer(issuer.getId());
        Page<NegotiationBilling> page =  negotiationBillingService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/issuers/me/hirer-negotiation-billings", api));
    }

    @JsonView(Views.Hirer.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/me/hirers/{id}", method = RequestMethod.GET)
    public Hirer getHirer(Issuer issuer, @PathVariable  String id) {
        log.info("get Hirer={} for issuer={}", id, issuer.documentNumber());
        return hirerService.getByIdForIssuer(id, issuer);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/issuers/me/hirers/{id}", method = RequestMethod.PUT)
    public void updateHirer(Issuer issuer, @PathVariable String id, @Validated(Update.class) @RequestBody Hirer hirer){
        hirer.setId(id);
        log.info("updating hirer={} for issuer={}", hirer, issuer.documentNumber());
        hirerService.updateForIssuer(id, issuer, hirer);
    }

    @JsonView(Views.Hirer.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/me/hirers", method = RequestMethod.GET)
    public Results<Hirer> getHirerByParams(Issuer issuer, HirerFilter filter, @Validated UnovationPageRequest pageable){
        log.info("search Hirer with filter={} for issuer={}", filter, issuer.documentNumber());
        filter.setIssuer(issuer.getId());
        Page<Hirer> page =  hirerService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/issuers/me/hirers", api));
    }

    @JsonView(Views.Contract.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/me/contracts", method = RequestMethod.GET)
    public Results<Contract> getContracts(Issuer issuer, ContractFilter filter,
                                          @Validated UnovationPageRequest pageable) {
        log.info("get contracts for issuer={}", issuer.documentNumber());
        filter.setIssuers(Collections.singleton(issuer.getId()));
        Page<Contract> page = contractService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/issuers/me/contracts", api));
    }


    @JsonView(Views.Contract.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/me/contracts/{id}", method = RequestMethod.GET)
    public Contract getContract(Issuer issuer, @PathVariable  String id) {
        log.info("get Contract={} for issuer={}", id, issuer.documentNumber());
        return contractService.getByIdForIssuer(id, issuer);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/issuers/me/contracts/{id}", method = RequestMethod.DELETE)
    public void cancelContract(Issuer issuer, @PathVariable  String id) {
        log.info("cancel Contract={} for issuer={}", id, issuer.documentNumber());
        contractService.cancelByIdForIssuer(id, issuer);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/issuers/me/contracts/{id}", method = RequestMethod.PUT)
    public void updateContract(Issuer issuer, @PathVariable String id,
                               @Validated(Update.class) @RequestBody Contract contract){
        contract.setId(id);
        log.info("updating contract={} for issuer={}", contract, issuer.documentNumber());
        contractService.updateForIssuer(id, issuer, contract);
    }

    @JsonView(Views.Contract.List.class)
    @PreAuthorize("hasRole('ROLE_MANAGE_ISSUER')")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/issuers/{id}/contracts", method = RequestMethod.GET)
    public Results<Contract> getContracts(@PathVariable String id, ContractFilter filter,
                                          @Validated UnovationPageRequest pageable) {
        log.info("get contracts for issuer={}", id);
        filter.setIssuers(Collections.singleton(id));
        Page<Contract> page = contractService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/issuers/%s/contracts", api, id));
    }

    @JsonView(Views.Issuer.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/issuers/menu")
    List<Issuer> listForMenu() {
        return service.listForMenu();
    }
}
