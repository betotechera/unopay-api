package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.filter.AuthorizedMemberFilter;
import br.com.unopay.api.bacen.model.filter.ContractorFilter;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.billing.boleto.model.Ticket;
import br.com.unopay.api.billing.boleto.model.filter.TicketFilter;
import br.com.unopay.api.billing.boleto.service.TicketService;
import br.com.unopay.api.billing.creditcard.model.PaymentRequest;
import br.com.unopay.api.billing.creditcard.model.Transaction;
import br.com.unopay.api.billing.creditcard.model.filter.TransactionFilter;
import br.com.unopay.api.billing.creditcard.service.TransactionService;
import br.com.unopay.api.credit.model.ContractorInstrumentCredit;
import br.com.unopay.api.credit.service.ContractorInstrumentCreditService;
import br.com.unopay.api.market.model.AuthorizedMember;
import br.com.unopay.api.market.model.ContractorBonus;
import br.com.unopay.api.market.model.filter.ContractorBonusFilter;
import br.com.unopay.api.market.service.AuthorizedMemberService;
import br.com.unopay.api.market.service.ContractorBonusService;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.network.model.Branch;
import br.com.unopay.api.network.model.Establishment;
import br.com.unopay.api.network.model.ServiceType;
import br.com.unopay.api.network.model.filter.BranchFilter;
import br.com.unopay.api.network.model.filter.EstablishmentFilter;
import br.com.unopay.api.network.service.BranchService;
import br.com.unopay.api.network.service.EstablishmentService;
import br.com.unopay.api.order.model.Order;
import br.com.unopay.api.order.model.filter.OrderFilter;
import br.com.unopay.api.order.service.OrderService;
import br.com.unopay.api.scheduling.model.Scheduling;
import br.com.unopay.api.scheduling.model.filter.SchedulingFilter;
import br.com.unopay.api.scheduling.service.SchedulingService;
import br.com.unopay.api.service.ContractService;
import br.com.unopay.api.service.PaymentInstrumentService;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import java.net.URI;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

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
    private TicketService ticketService;
    private AuthorizedMemberService authorizedMemberService;
    private ContractorBonusService contractorBonusService;
    private EstablishmentService establishmentService;
    private BranchService branchService;
    private SchedulingService schedulingService;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public ContractorController(ContractorService service,
                                ContractService contractService,
                                OrderService orderService,
                                ContractorInstrumentCreditService contractorInstrumentCreditService,
                                PaymentInstrumentService paymentInstrumentService,
                                TransactionService transactionService,
                                TicketService ticketService,
                                AuthorizedMemberService authorizedMemberService,
                                ContractorBonusService contractorBonusService,
                                EstablishmentService establishmentService,
                                BranchService branchService,
                                SchedulingService schedulingService) {
        this.service = service;
        this.contractService = contractService;
        this.orderService = orderService;
        this.contractorInstrumentCreditService = contractorInstrumentCreditService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.transactionService = transactionService;
        this.ticketService = ticketService;
        this.authorizedMemberService = authorizedMemberService;
        this.contractorBonusService = contractorBonusService;
        this.establishmentService = establishmentService;
        this.branchService = branchService;
        this.schedulingService = schedulingService;
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

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/contractors/me", method = RequestMethod.PUT)
    public void updateMe(Contractor current, @Validated(Update.class) @RequestBody Contractor contractor) {
        log.info("updating contractor {}", contractor);
        service.update(current.getId(), contractor);
    }


    @JsonView(Views.Contract.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/me/contracts", method = RequestMethod.GET)
    public Results<Contract> getMyContracts(@RequestParam(required = false) String productCode,
                                            OAuth2Authentication authentication) {
        log.info("search Contractor={} Contracts for productCode={}",authentication.getName(), productCode);
        List<Contract> contracts = contractService.getMeValidContracts(authentication.getName(), productCode);
        return new Results<>(contracts);
    }

    @JsonView(Views.Contract.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/me/contracts/service-types", method = RequestMethod.GET)
    public Results<ServiceType> getMyContractsServiceTypes(@RequestParam(required = false) String productCode,
                                            OAuth2Authentication authentication) {
        log.info("search Contractor={} Contracts service types for productCode={}",authentication.getName(), productCode);
        List<ServiceType> contractsServiceTypes = contractService.getMeValidContractServiceType(authentication.getName(), productCode);
        return new Results<>(contractsServiceTypes);
    }

    @JsonView(Views.Establishment.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/me/contracts/establishments", method = RequestMethod.GET)
    public Results<Establishment> getMyContractsEstablishments(@Validated UnovationPageRequest pageable, EstablishmentFilter filter,
                                                           OAuth2Authentication authentication) {
        log.info("searching the contractor={} contracts establishments for productCode={}",authentication.getName());
        Page<Establishment> page = establishmentService.getContractorEstablishments(authentication.getName(), filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/contractors/me/contracts/establishments", api));
    }

    @JsonView(Views.Branch.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/me/contracts/branches", method = RequestMethod.GET)
    public Results<Branch> getMyContractsBranches(@Validated UnovationPageRequest pageable, BranchFilter filter,
                                                               OAuth2Authentication authentication) {
        log.info("searching the contractor={} contracts branches for productCode={}",authentication.getName());
        Page<Branch> page = branchService.getContractorBranches(authentication.getName(), filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/contractors/me/contracts/branches", api));
    }

    @JsonView(Views.Scheduling.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/contractors/me/schedules")
    public ResponseEntity<Scheduling> createScheduling(@Validated(Create.class) @RequestBody Scheduling scheduling,
                                                       Contractor contractor, UserDetail currentUser) {
        scheduling.setUser(currentUser);
        log.info("create a scheduling for contractor={}", contractor.getDocumentNumber());
        Scheduling created = schedulingService.create(scheduling, contractor);
        return ResponseEntity
                .created(URI.create("/contractors/me/schedules/"+created.getId()))
                .body(created);
    }

    @JsonView(Views.Scheduling.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/me/schedules/{id}", method = RequestMethod.GET)
    public Scheduling getScheduling(Contractor contractor, @PathVariable  String id) {
        log.info("get scheduling={} for contractor={}", id, contractor.getDocumentNumber());
        return schedulingService.findById(id, contractor);
    }

    @JsonView(Views.Scheduling.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/me/schedules", method = RequestMethod.GET)
    public Results<Scheduling> getMyContractorSchedules(@Validated UnovationPageRequest pageable, SchedulingFilter filter,
                                                       Contractor contractor) {
        log.info("searching the contractor={} schedules",contractor.getDocumentNumber());
        Page<Scheduling> page = schedulingService.findAll(filter, contractor, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/contractors/me/schedules", api));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/contractors/me/schedules/{id}")
    public void cancelScheduling(@PathVariable  String id, Contractor contractor) {
        log.info("cancelling a scheduling={} for contractor={}", id, contractor.getDocumentNumber());
        schedulingService.cancelById(id, contractor);
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
        return created(URI.create("/contractors/me/orders"+created.getId())).body(created);
    }

    @JsonView(Views.Order.Detail.class)
    @ResponseStatus(ACCEPTED)
    @RequestMapping(value = "/contractors/me/orders/{id}", method = PUT, params = "request-payment")
    public Order create(Contractor contractor, @PathVariable  String id,
                                        @Validated(Create.Order.class) @RequestBody PaymentRequest paymentRequest) {
        log.info("new payment for order={} and contractor={}", id, contractor.getDocumentNumber());
        return orderService.requestPayment(contractor, id, paymentRequest);
    }

    @JsonView(Views.Order.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/me/orders/{id}", method = RequestMethod.GET)
    public Order getOrder(Contractor contractor, @PathVariable  String id) {
        log.info("get order={} for contractor={}", id, contractor.getDocumentNumber());
        return orderService.findByIdForContractor(id, contractor);
    }

    @JsonView(Views.Order.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/me/orders", method = RequestMethod.GET)
    public Results<Order> getOrderByParams(Contractor contractor, OrderFilter filter, @Validated UnovationPageRequest pageable){
        log.info("search order with filter={} for contractor={}", filter, contractor.getDocumentNumber());
        filter.setDocument(contractor.getDocumentNumber());
        Page<Order> page =  orderService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/contractors/me/orders", api));
    }

    @JsonView(Views.PaymentInstrument.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/me/payment-instruments", method = RequestMethod.GET)
    public Results<PaymentInstrument> getMyInstruments(OAuth2Authentication authentication) {
        log.info("get Contractor instruments for={}", authentication.getName());
        List<PaymentInstrument> contracts = paymentInstrumentService.findMyInstruments(authentication.getName());
        return new Results<>(contracts);
    }


    @JsonView(Views.Ticket.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/me/tickets", method = RequestMethod.GET)
    public Results<Ticket> findBoletos(OAuth2Authentication authentication,
                                       TicketFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("find tickets for={} with filter={}",authentication.getName(), filter);
        Set<String> myOrderNumbers = orderService.getMyOrderNumbers(authentication.getName(), filter.getOrderNumbers());
        filter.setOrderNumber(myOrderNumbers);
        Page<br.com.unopay.api.billing.boleto.model.Ticket> page = ticketService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/contractors/me/tickets", api));
    }

    @JsonView(Views.Ticket.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/me/tickets/{id}", method = RequestMethod.GET)
    public Ticket getTicket(Contractor contractor, @PathVariable  String id) {
        log.info("get ticket={} for contractor={}", id, contractor.getDocumentNumber());
        return ticketService.getByIdForPayer(id, contractor.getPerson());
    }

    @JsonView(Views.Billing.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/me/transactions", method = RequestMethod.GET)
    public Results<Transaction> findTransactions(OAuth2Authentication authentication,
                                           TransactionFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("find transactions for={} with filter={}", authentication.getName(), filter);
        Set<String> myOrderIds = orderService.getMyOrderIds(authentication.getName(), filter.getOrderId());
        filter.setOrderId(myOrderIds);
        Page<Transaction> page = transactionService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/contractors/me/transactions", api));
    }

    @JsonView(Views.AuthorizedMember.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/me/authorized-members/{id}", method = RequestMethod.GET)
    public AuthorizedMember getAuthorizedMember(Contractor contractor, @PathVariable String id) {
        log.info("get authorizedMember={} for contractor={}", id, contractor.getPerson().documentNumber());
        return authorizedMemberService.findByIdForContractor(id, contractor);
    }

    @JsonView(Views.AuthorizedMember.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/me/authorized-members", method = RequestMethod.GET)
    public PageableResults<AuthorizedMember> getAuthorizedMember(Contractor contractor, AuthorizedMemberFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("get authorizedMembers for contractor={}", contractor.getPerson().documentNumber());
        filter.setContractorId(contractor.getId());
        Page<AuthorizedMember> page =  authorizedMemberService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/hirers/me/authorized-members", api));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @JsonView(Views.AuthorizedMember.Detail.class)
    @RequestMapping(value = "/contractors/me/authorized-members", method = RequestMethod.POST)
    public ResponseEntity<AuthorizedMember> create(Contractor contractor, @Validated(Create.class)
    @RequestBody AuthorizedMember authorizedMember) {
        log.info("creating authorizedMember {}", authorizedMember);
        AuthorizedMember created = authorizedMemberService.create(authorizedMember, contractor);
        return ResponseEntity
                .created(URI.create("/contractors/me/authorized-members/"+created.getId()))
                .body(created);
    }

    @JsonView(Views.AuthorizedMember.Detail.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/contractors/me/authorized-members/{id}", method = RequestMethod.PUT)
    public void updateAuthorizedMember(Contractor contractor, @PathVariable  String id, @Validated(Update.class)
    @RequestBody AuthorizedMember authorizedMember) {
        log.info("updating authorizedMember={} for contractor={}", id, contractor.getPerson().documentNumber());
        authorizedMemberService.updateForContractor(id, contractor, authorizedMember);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/contractors/me/authorized-members/{id}", method = RequestMethod.DELETE)
    public void removeAuthorizedMember(Contractor contractor, @PathVariable  String id) {
        log.info("removing authorized-member id={}", id);
        authorizedMemberService.deleteForContractor(id, contractor);
    }

    @JsonView(Views.AuthorizedMember.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_CONTRACTOR')")
    @RequestMapping(value = "/contractors/{contractorDocument}/authorized-members", method = RequestMethod.GET)
    public Results<AuthorizedMember> getAuthorizedMembers(@PathVariable String contractorDocument, AuthorizedMemberFilter filter,
                                                          @Validated UnovationPageRequest pageable) {
        log.info("get authorizedMembers for contractor={}", contractorDocument);
        Contractor contractor = service.getByDocument(contractorDocument);
        filter.setContractorId(contractor.getId());
        Page<AuthorizedMember> page =  authorizedMemberService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/contractors/%s/authorized-members"
                , api, contractorDocument));
    }

    @JsonView(Views.ContractorBonus.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/me/bonuses/{id}", method = RequestMethod.GET)
    public ContractorBonus getContractorBonus(Contractor contractor, @PathVariable String id) {
        log.info("get bonus={} for contractor={}", id, contractor.getPerson().documentNumber());
        return contractorBonusService.findByIdForContractor(id, contractor);
    }

    @JsonView(Views.ContractorBonus.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/contractors/me/bonuses", method = RequestMethod.GET)
    public Results<ContractorBonus> getContractorBonusByParams(Contractor contractor,
                                                               ContractorBonusFilter filter,
                                                               @Validated UnovationPageRequest pageable) {
        log.info("search bonuses with filter={} for contractor={}", filter, contractor.getPerson().documentNumber());
        filter.setContractor(contractor.getId());
        Page<ContractorBonus> page = contractorBonusService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/contractors/me/bonuses", api));
    }

    @JsonView(Views.Contractor.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/contractors/menu")
    List<Contractor> listForMenu() {
        return service.listForMenu();
    }
}
