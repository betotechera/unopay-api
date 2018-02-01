package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.filter.ContractorFilter;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.billing.boleto.model.Ticket;
import br.com.unopay.api.billing.boleto.model.filter.BoletoFilter;
import br.com.unopay.api.billing.boleto.service.BoletoService;
import br.com.unopay.api.billing.creditcard.model.Transaction;
import br.com.unopay.api.billing.creditcard.model.filter.TransactionFilter;
import br.com.unopay.api.billing.creditcard.service.TransactionService;
import br.com.unopay.api.credit.model.ContractorInstrumentCredit;
import br.com.unopay.api.credit.service.ContractorInstrumentCreditService;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.service.OrderService;
import br.com.unopay.api.service.ContractService;
import br.com.unopay.api.service.PaymentInstrumentService;
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
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
@Timed(prefix = "api")
@PreAuthorize("#oauth2.isUser()")
public class ContractorController {

    private ContractorService service;
    private ContractService contractService;
    private OrderService orderService;
    private ContractorInstrumentCreditService contractorInstrumentCreditService;
    private PaymentInstrumentService paymentInstrumentService;
    private TransactionService transactionService;
    private BoletoService boletoService;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public ContractorController(ContractorService service,
                                ContractService contractService,
                                OrderService orderService,
                                ContractorInstrumentCreditService contractorInstrumentCreditService,
                                PaymentInstrumentService paymentInstrumentService,
                                TransactionService transactionService,
                                BoletoService boletoService) {
        this.service = service;
        this.contractService = contractService;
        this.orderService = orderService;
        this.contractorInstrumentCreditService = contractorInstrumentCreditService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.transactionService = transactionService;
        this.boletoService = boletoService;
    }

    @JsonView(Views.Contractor.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_CONTRACTOR')")
    @RequestMapping(value = "/contractors", method = RequestMethod.POST)
    public ResponseEntity<Contractor> create(@Validated(Create.class) @RequestBody Contractor contractor) {
        log.info("creating contractor {}", contractor);
        Contractor created = service.create(contractor);
        return ResponseEntity
                .created(URI.create("/contractors/"+created.getId()))
                .body(created);

    }

    @JsonView(Views.Contractor.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_CONTRACTOR')")
    @RequestMapping(value = "/contractors/{id}", method = RequestMethod.GET)
    public Contractor get(@PathVariable  String id) {
        log.info("get Contractor={}", id);
        return service.getById(id);
    }

    @JsonView({Views.Contractor.Detail.class})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_CONTRACTOR')")
    @RequestMapping(value = "/contractors", method = RequestMethod.GET, params = {"documentNumber","useOnlyDocument"})
    public Contractor getByDocument(@RequestParam String documentNumber, @RequestParam String useOnlyDocument){
        log.info("get Contractor document={}", documentNumber);
        return service.getByDocument(documentNumber);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_CONTRACTOR')")
    @RequestMapping(value = "/contractors/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody Contractor contractor) {
        contractor.setId(id);
        log.info("updating contractor {}", contractor);
        service.update(id, contractor);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_CONTRACTOR')")
    @RequestMapping(value = "/contractors/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing hired id={}", id);
        service.delete(id);
    }

    @JsonView(Views.Contractor.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_CONTRACTOR')")
    @RequestMapping(value = "/contractors", method = RequestMethod.GET)
    public Results<Contractor> getByParams(ContractorFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search Contractor with filter={}", filter);
        Page<Contractor> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/contractors", api));
    }

    @JsonView(Views.Contract.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_CONTRACTOR')")
    @RequestMapping(value = "/contractors/{id}/contracts", method = RequestMethod.GET)
    public Results<Contract> getValidContracts(@PathVariable  String id,
                                               @RequestParam(required = false) String productCode) {
        log.info("search Contractor Contracts id={} productCode={}", id, productCode);
        List<Contract> contracts = contractService.getContractorValidContracts(id, productCode);
        return new Results<>(contracts);
    }

    @JsonView(Views.Contract.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/me/contracts", method = RequestMethod.GET)
    public Results<Contract> getMyContracts(@RequestParam(required = false) String productCode,
                                            OAuth2Authentication authentication) {
        log.info("search Contractor={} Contracts with productCode={}",authentication.getName(), productCode);
        List<Contract> contracts = contractService.getMeValidContracts(authentication.getName(), productCode);
        return new Results<>(contracts);
    }

    @JsonView(Views.ContractorInstrumentCredit.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_CONTRACTOR')")
    @RequestMapping(value = "/contractors/{contractorDocument}/credits", method = RequestMethod.GET)
    public Results<ContractorInstrumentCredit> getCredits(@PathVariable  String contractorDocument,
                                                          @RequestParam(required = false) String contractId,
                                                          @Validated UnovationPageRequest pageable) {
        log.info("search Contractor credits document={}", contractorDocument);
        Page<ContractorInstrumentCredit> page = contractorInstrumentCreditService
                                                    .findContractorCredits(contractId, contractorDocument, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/contractors", api));
    }

    @JsonView(Views.ContractorInstrumentCredit.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/me/credits", method = RequestMethod.GET)
    public Results<ContractorInstrumentCredit> getMyCredits(OAuth2Authentication authentication,
                                                          @RequestParam(required = false) String contractId,
                                                          @Validated UnovationPageRequest pageable) {
        log.info("search Contractor credits email={}", authentication.getName());
        Page<ContractorInstrumentCredit> page = contractorInstrumentCreditService
                .findLogedContractorCredits(contractId, authentication.getName(), pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/contractors", api));
    }

    @JsonView(Views.PaymentInstrument.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_CONTRACTOR')")
    @RequestMapping(value = "/contractors/{contractorDocument}/payment-instruments", method = RequestMethod.GET)
    public Results<PaymentInstrument> getInstruments(@PathVariable String contractorDocument) {
        log.info("search Contractor instruments document={}", contractorDocument);
        List<PaymentInstrument> contracts = paymentInstrumentService.findByContractorDocument(contractorDocument);
        return new Results<>(contracts);
    }

    @JsonView(Views.Order.Detail.class)
    @ResponseStatus(CREATED)
    @RequestMapping(value = "/contractors/me/orders", method = POST)
    public ResponseEntity<Order> create(OAuth2Authentication authentication,
                                        @Validated(Create.Order.class) @RequestBody Order order) {
        log.info("creating order {}", order);
        Order created = orderService.create(authentication.getName(), order);
        return created(URI.create("/credit-orders/"+created.getId())).body(created);
    }

    @JsonView(Views.PaymentInstrument.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/me/payment-instruments", method = RequestMethod.GET)
    public Results<PaymentInstrument> getMyInstruments(OAuth2Authentication authentication) {
        log.info("get Contractor instruments for={}", authentication.getName());
        List<PaymentInstrument> contracts = paymentInstrumentService.findMyInstruments(authentication.getName());
        return new Results<>(contracts);
    }


    @JsonView(Views.Boleto.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/me/boletos", method = RequestMethod.GET)
    public Results<Ticket> findBoletos(OAuth2Authentication authentication,
                                       BoletoFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("find boletos for={} with filter={}",authentication.getName(), filter);
        Page<Ticket> page = boletoService.findMyByFilter(authentication.getName(),filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/contractors/me/boletos", api));
    }

    @JsonView(Views.Billing.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/me/transactions", method = RequestMethod.GET)
    public Results<Transaction> findTransactions(OAuth2Authentication authentication,
                                           TransactionFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("find transactions for={} with filter={}", authentication.getName(), filter);
        Page<Transaction> page = transactionService.findMyByFilter(authentication.getName(), filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/contractors/me/transactions", api));
    }


}
