package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.filter.ContractorFilter;
import br.com.unopay.api.bacen.model.filter.HirerFilter;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.bacen.service.HirerService;
import br.com.unopay.api.billing.boleto.model.Ticket;
import br.com.unopay.api.billing.boleto.model.filter.TicketFilter;
import br.com.unopay.api.billing.boleto.service.TicketService;
import br.com.unopay.api.billing.creditcard.model.Transaction;
import br.com.unopay.api.billing.creditcard.model.filter.TransactionFilter;
import br.com.unopay.api.billing.creditcard.service.TransactionService;
import br.com.unopay.api.credit.model.ContractorInstrumentCredit;
import br.com.unopay.api.credit.model.Credit;
import br.com.unopay.api.credit.model.CreditPaymentAccount;
import br.com.unopay.api.credit.model.filter.ContractorInstrumentCreditFilter;
import br.com.unopay.api.credit.model.filter.CreditFilter;
import br.com.unopay.api.credit.service.ContractorInstrumentCreditService;
import br.com.unopay.api.credit.service.CreditPaymentAccountService;
import br.com.unopay.api.credit.service.CreditService;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.filter.ContractFilter;
import br.com.unopay.api.model.filter.PaymentInstrumentFilter;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
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
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
@PreAuthorize("#oauth2.isUser()")
@Timed(prefix = "api")
public class HirerController {

    private HirerService service;
    private ContractorService contractorService;
    private ContractService contractService;
    private CreditService creditService;
    private CreditPaymentAccountService creditPaymentAccountService;
    private PaymentInstrumentService paymentInstrumentService;
    private ContractorInstrumentCreditService contractorInstrumentCreditService;
    private TicketService ticketService;
    private TransactionService transactionService;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public HirerController(HirerService service,
                           ContractorService contractorService,
                           ContractService contractService,
                           CreditService creditService,
                           CreditPaymentAccountService creditPaymentAccountService,
                           PaymentInstrumentService paymentInstrumentService,
                           ContractorInstrumentCreditService contractorInstrumentCreditService,
                           TicketService ticketService, TransactionService transactionService) {
        this.service = service;
        this.contractorService = contractorService;
        this.contractService = contractService;
        this.creditService = creditService;
        this.creditPaymentAccountService = creditPaymentAccountService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.contractorInstrumentCreditService = contractorInstrumentCreditService;
        this.ticketService = ticketService;
        this.transactionService = transactionService;
    }

    @PreAuthorize("hasRole('ROLE_MANAGE_HIRER')")
    @JsonView(Views.Hirer.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/hirers", method = RequestMethod.POST)
    public ResponseEntity<Hirer> create(@Validated(Create.class) @RequestBody Hirer hirer) {
        log.info("creating hirer {}", hirer);
        Hirer created = service.create(hirer);
        return ResponseEntity
                .created(URI.create("/hirers/"+created.getId()))
                .body(created);

    }

    @JsonView(Views.Hirer.Detail.class)
    @PreAuthorize("hasRole('ROLE_LIST_HIRER')")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/hirers/{id}", method = RequestMethod.GET)
    public Hirer get(@PathVariable  String id) {
        log.info("get Hirer={}", id);
        return service.getById(id);
    }

    @PreAuthorize("hasRole('ROLE_MANAGE_HIRER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/hirers/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody Hirer hirer) {
        hirer.setId(id);
        log.info("updating hirer {}", hirer);
        service.update(id,hirer);
    }

    @PreAuthorize("hasRole('ROLE_MANAGE_HIRER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/hirers/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing hirer id={}", id);
        service.delete(id);
    }

    @JsonView(Views.Hirer.List.class)
    @PreAuthorize("hasRole('ROLE_LIST_HIRER')")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/hirers", method = RequestMethod.GET)
    public Results<Hirer> getByParams(HirerFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search Hirer with filter={}", filter);
        Page<Hirer> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/hirers", api));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_CONTRACT')")
    @RequestMapping(value = "/hirers/{document}/contracts", method = RequestMethod.POST,
            consumes = "multipart/form-data")
    public void createFromCsvById(@PathVariable  String document, @RequestParam MultipartFile file){
        String fileName = file.getOriginalFilename();
        log.info("reading clients from csv file {}", fileName);
        contractService.dealCloseFromCsv(document, file);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/hirers/me/contracts", method = RequestMethod.POST,
            consumes = "multipart/form-data")
    public void createMyFromCsvById(OAuth2Authentication authentication,
                                    @RequestParam MultipartFile file){
        String fileName = file.getOriginalFilename();
        log.info("reading clients from csv file={} for={}", fileName, authentication.getName());
        contractService.dealCloseFromCsvForCurrentUser(authentication.getName(), file);
    }

    @ResponseStatus(OK)
    @JsonView(Views.Contract.List.class)
    @RequestMapping(value = "/hirers/me/contracts", method = GET)
    public Results<Contract> findContractsByParams(Hirer hirer, ContractFilter filter,
                                                   @Validated UnovationPageRequest pageable) {
        log.info("search contracts with filter={} for hirer={}", filter, hirer.getDocumentNumber());
        filter.setHirer(hirer.getId());
        Page<Contract> page =  contractService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/hirers/me/contracts", api));
    }

    @ResponseStatus(OK)
    @JsonView(Views.Contract.Detail.class)
    @RequestMapping(value = "/hirers/me/contracts/{id}", method = GET)
    public Contract getContract(@PathVariable String id, Hirer hirer) {
        log.info("get contract={} for hirer={}", id, hirer.getDocumentNumber());
        return contractService.findByIdForHirer(id, hirer);
    }

    @JsonView(Views.Hirer.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/hirers/me", method = RequestMethod.GET)
    public Hirer getMe(Hirer hirer) {
        log.info("get Hirer={}", hirer.getDocumentNumber());
        return service.getById(hirer.getId());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/hirers/me", method = RequestMethod.PUT)
    public void updateMe(Hirer current, @Validated(Update.class) @RequestBody Hirer hirer) {
        log.info("updating hirer={} for hirer={}",hirer, current.getDocumentNumber());
        service.update(hirer.getId(),hirer);
    }

    @JsonView(Views.Contractor.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/hirers/me/contractors/{id}", method = RequestMethod.GET)
    public Contractor getContractor(Hirer hirer,@PathVariable  String id) {
        log.info("get Contractor={} for hirer={}", id, hirer.getDocumentNumber());
        return contractorService.getByIdForHirer(id, hirer);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/hirers/me/contractors/{id}", method = RequestMethod.PUT)
    public void updateContractor(Hirer hirer,
                                 @PathVariable String id,
                                 @Validated(Update.class) @RequestBody Contractor contractor){
        contractor.setId(id);
        log.info("updating contractor={} for hirer={}", contractor, hirer.getDocumentNumber());
        contractorService.updateForHirer(id, hirer, contractor);
    }

    @JsonView(Views.Contractor.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/hirers/me/contractors", method = RequestMethod.GET)
    public Results<Contractor> getContractorsByParams(Hirer hirer, ContractorFilter filter,
                                                      @Validated UnovationPageRequest pageable){
        log.info("search Contractor with filter={} for hirer={}", filter, hirer.getDocumentNumber());
        filter.setHirer(hirer.getId());
        Page<Contractor> page =  contractorService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/hirers/me/contractors", api));
    }

    @JsonView(Views.PaymentInstrument.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/hirers/me/contractors/{id}/payment-instruments", method = RequestMethod.GET)
    public Results<PaymentInstrument> getContractorsInstrumentsByParams(Hirer hirer, @PathVariable String id,
                                                                        PaymentInstrumentFilter filter,
                                                                        @Validated UnovationPageRequest pageable){
        log.info("search Contractor instruments with filter={} for hirer={}", filter, hirer.getDocumentNumber());
        filter.setContractor(id);
        contractorService.getByIdForHirer(id, hirer);
        Page<PaymentInstrument> page =  paymentInstrumentService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/hirers/me/contractors/{%s}/payment-instruments", api, id));
    }

    @JsonView(Views.ContractorInstrumentCredit.Detail.class)
    @ResponseStatus(CREATED)
    @RequestMapping(value = "/hirers/me/contractors/{contractorId}/payment-instruments/{id}/credits", method = POST)
    public ResponseEntity<ContractorInstrumentCredit> create(Hirer hirer,
                                                             @PathVariable String contractorId,
                                                             @PathVariable String id,
                                                             @Validated(Create.class) @RequestBody
                                                                         ContractorInstrumentCredit credit) {
        log.info("inserting payment instrument credit={} for contractor={} of hirer={}",
                                                credit, contractorId, hirer.getDocumentNumber());
        contractorService.getByIdForHirer(contractorId, hirer);
        paymentInstrumentService.findByIdAndContractorId(id,contractorId);
        ContractorInstrumentCredit created = contractorInstrumentCreditService.insert(id, credit);
        log.info("inserted payment instrument credit={} for contractor={} of hirer={}",
                credit, contractorId, hirer.getDocumentNumber());
        return created(URI.create(
                String.format("/hirers/me/contractors/%s/payment-instruments/%s/credits/%s",contractorId,id,
                        created.getId()))).body(created);

    }

    @ResponseStatus(OK)
    @JsonView(Views.ContractorInstrumentCredit.List.class)
    @RequestMapping(value = "/hirers/me/contractors/payment-instruments/credits", method = GET)
    public Results<ContractorInstrumentCredit> findAll(Hirer hirer,
                                                       ContractorInstrumentCreditFilter filter,
                                                       @Validated UnovationPageRequest pageable) {
        log.info("search ContractorInstrumentCredit with filter={} for hirer={}", filter, hirer.getDocumentNumber());
        filter.setHirer(hirer.getId());
        Page<ContractorInstrumentCredit> page =  contractorInstrumentCreditService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/hirers/me/contractors/payment-instruments/credits", api));
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/hirers/me/contractors/payment-instruments/credits/{id}",
            method = RequestMethod.DELETE)
    public void cancelForHirer(Hirer hirer, @PathVariable  String id) {
        log.info("canceling payment instrument credit id={} for hire={}",
                id, hirer.getDocumentNumber());
        contractorInstrumentCreditService.cancelForHirer(id, hirer);
    }

    @JsonView(Views.Credit.Detail.class)
    @ResponseStatus(CREATED)
    @RequestMapping(value = "/hirers/me/credits", method = POST)
    public ResponseEntity<Credit> insertCredit(Hirer hirer,
                                         @Validated(Create.class) @RequestBody Credit credit) {
        log.info("inserting credit={} for hirer={}", credit, hirer.getDocumentNumber());
        credit.setHirer(hirer);
        Credit created = creditService.insert(credit);
        log.info("Inserted credit={}", created);
        return created(URI.create(String.format("/hirers/me/credits/%s",created.getId()))).body(created);
    }

    @ResponseStatus(OK)
    @JsonView(Views.Credit.Detail.class)
    @RequestMapping(value = "/hirers/me/credits/{id}", method = GET)
    public Credit getCredit(Hirer hirer, @PathVariable String id) {
        log.info("get credit={} for hirer={}", id, hirer.getDocumentNumber());
        return creditService.findByIdForHirer(id, hirer);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/hirers/me/credits/{id}", method = RequestMethod.DELETE)
    public void cancel(Hirer hirer, @PathVariable String id) {
        log.info("canceling credit id={} for hirer={}", id, hirer.getDocumentNumber());
        creditService.cancelForHirer(id, hirer);
    }

    @ResponseStatus(OK)
    @JsonView(Views.Credit.List.class)
    @RequestMapping(value = "/hirers/me/credits", method = GET)
    public Results<Credit> findCreditByParams(Hirer hirer, CreditFilter filter,
                                  @Validated UnovationPageRequest pageable) {
        log.info("search Credit with filter={} for hirer={}", filter, hirer.getDocumentNumber());
        filter.setHirerDocument(hirer.getDocumentNumber());
        Page<Credit> page =  creditService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/hirers/me/credits", api));
    }

    @ResponseStatus(OK)
    @JsonView(Views.CreditPaymentAccount.Detail.class)
    @RequestMapping(value = "/hirers/me/credit-payment-accounts", method = GET)
    public Results<CreditPaymentAccount> findPaymentAccountByParams(Hirer hirer) {
        log.info("search payment accounts for hirer={}", hirer.getDocumentNumber());
        List<CreditPaymentAccount> creditPaymentAccounts = creditPaymentAccountService
                                                                .findByHirerDocument(hirer.getDocumentNumber());
        return new Results<>(creditPaymentAccounts);
    }

    @JsonView(Views.Boleto.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/hirers/me/boletos", method = RequestMethod.GET)
    public Results<Ticket> findBoletos(OAuth2Authentication authentication,
                                       TicketFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("find boletos for={} with filter={}",authentication.getName(), filter);
        Page<Ticket> page = ticketService.findMyByFilter(authentication.getName(),filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/hirers/me/boletos", api));
    }

    @JsonView(Views.Billing.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/hirers/me/transactions", method = RequestMethod.GET)
    public Results<Transaction> findTransactions(OAuth2Authentication authentication,
                                                 TransactionFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("find transactions for={} with filter={}", authentication.getName(), filter);
        Page<Transaction> page = transactionService.findMyByFilter(authentication.getName(), filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/hirers/me/transactions", api));
    }

}
