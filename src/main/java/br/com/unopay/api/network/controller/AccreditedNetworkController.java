package br.com.unopay.api.network.controller;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.filter.AuthorizedMemberFilter;
import br.com.unopay.api.bacen.model.filter.ContractorFilter;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.market.model.AuthorizedMember;
import br.com.unopay.api.market.service.AuthorizedMemberService;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.filter.ContractFilter;
import br.com.unopay.api.model.filter.PaymentInstrumentFilter;
import br.com.unopay.api.network.model.AccreditedNetwork;
import br.com.unopay.api.network.model.Branch;
import br.com.unopay.api.network.model.Establishment;
import br.com.unopay.api.network.model.EstablishmentEvent;
import br.com.unopay.api.network.model.filter.AccreditedNetworkFilter;
import br.com.unopay.api.network.model.filter.BranchFilter;
import br.com.unopay.api.network.model.filter.EstablishmentEventFilter;
import br.com.unopay.api.network.model.filter.EstablishmentFilter;
import br.com.unopay.api.network.service.AccreditedNetworkService;
import br.com.unopay.api.network.service.BranchService;
import br.com.unopay.api.network.service.EstablishmentBranchService;
import br.com.unopay.api.network.service.EstablishmentEventService;
import br.com.unopay.api.network.service.EstablishmentService;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@PreAuthorize("#oauth2.isUser()")
@Timed(prefix = "api")
public class AccreditedNetworkController {

    private AccreditedNetworkService service;
    private EstablishmentService establishmentService;
    private BranchService branchService;
    private SchedulingService schedulingService;
    private EstablishmentEventService establishmentEventService;
    private EstablishmentBranchService establishmentBranchService;
    private ContractorService contractorService;
    private PaymentInstrumentService paymentInstrumentService;
    private ContractService contractService;
    private AuthorizedMemberService authorizedMemberService;


    @Value("${unopay.api}")
    private String api;

    @Autowired
    public AccreditedNetworkController(AccreditedNetworkService service,
                                       EstablishmentService establishmentService,
                                       BranchService branchService,
                                       SchedulingService schedulingService,
                                       EstablishmentEventService establishmentEventService,
                                       EstablishmentBranchService establishmentBranchService,
                                       ContractorService contractorService,
                                       PaymentInstrumentService paymentInstrumentService,
                                       ContractService contractService,
                                       AuthorizedMemberService authorizedMemberService) {
        this.service = service;
        this.establishmentService = establishmentService;
        this.branchService = branchService;
        this.schedulingService = schedulingService;
        this.establishmentEventService = establishmentEventService;
        this.establishmentBranchService = establishmentBranchService;
        this.contractorService = contractorService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.contractService = contractService;
        this.authorizedMemberService = authorizedMemberService;
    }

    @JsonView(Views.AccreditedNetwork.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_ACCREDITED_NETWORK')")
    @RequestMapping(value = "/accredited-networks", method = RequestMethod.POST)
    public ResponseEntity<AccreditedNetwork> create(@Validated(Create.class)
                                                        @RequestBody AccreditedNetwork accreditedNetwork) {
        log.info("creating accreditedNetwork {}", accreditedNetwork);
        AccreditedNetwork created = service.create(accreditedNetwork);
        return ResponseEntity
                .created(URI.create("/accredited-networks/"+created.getId()))
                .body(created);

    }

    @JsonView({Views.AccreditedNetwork.Detail.class})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_ACCREDITED_NETWORK')")
    @RequestMapping(value = "/accredited-networks/{id}", method = RequestMethod.GET)
    public AccreditedNetwork get(@PathVariable  String id) {
        log.info("get AccreditedNetwork={}", id);
        return service.getById(id);
    }

    @JsonView(Views.AccreditedNetwork.Detail.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_ACCREDITED_NETWORK')")
    @RequestMapping(value = "/accredited-networks/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class)
    @RequestBody AccreditedNetwork accreditedNetwork) {
        accreditedNetwork.setId(id);
        log.info("updating accreditedNetwork {}", accreditedNetwork);
        service.update(id,accreditedNetwork);
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_ACCREDITED_NETWORK')")
    @RequestMapping(value = "/accredited-networks/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing accreditedNetwork id={}", id);
        service.delete(id);
    }

    @JsonView(Views.AccreditedNetwork.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/accredited-networks", method = RequestMethod.GET)
    public Results<AccreditedNetwork> getByParams(AccreditedNetworkFilter filter,
                                                  @Validated UnovationPageRequest pageable) {
        log.info("search AccreditedNetwork with filter={}", filter);
        Page<AccreditedNetwork> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/accredited-networks", api));
    }

    @JsonView({Views.AccreditedNetwork.Detail.class})
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/accredited-networks/me", method = RequestMethod.GET)
    public AccreditedNetwork getMe(AccreditedNetwork accreditedNetwork) {
        log.info("get AccreditedNetwork={}", accreditedNetwork.documentNumber());
        return service.getById(accreditedNetwork.getId());
    }

    @JsonView(Views.AccreditedNetwork.Detail.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/accredited-networks/me", method = RequestMethod.PUT)
    public void updateMe(AccreditedNetwork current, @Validated(Update.class)
    @RequestBody AccreditedNetwork accreditedNetwork) {
        log.info("updating accreditedNetwork={}", accreditedNetwork);
        service.update(current.getId(),accreditedNetwork);
    }

    @JsonView(Views.Establishment.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/accredited-networks/me/establishments/{id}", method = RequestMethod.GET)
    public Establishment getEstablishment(AccreditedNetwork accreditedNetwork, @PathVariable  String id) {
        log.info("get establishment={} for network={}", id, accreditedNetwork.documentNumber());
        return establishmentService.findByIdAndNetworks(id, accreditedNetwork);
    }

    @JsonView(Views.Establishment.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/accredited-networks/me/establishments", method = RequestMethod.GET)
    public Results<Establishment> getEstablishmentByParams(AccreditedNetwork accreditedNetwork,
                                                           EstablishmentFilter filter,
                                                           @Validated UnovationPageRequest pageable) {
        log.info("search establishment with filter={} for network={}", filter, accreditedNetwork.documentNumber());
        Page<Establishment> page =  establishmentService.findByFilterForNetwork(accreditedNetwork,filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/accredited-networks/me/establishments", api));
    }

    @JsonView(Views.Branch.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/accredited-networks/me/establishments", method = RequestMethod.POST)
    public ResponseEntity<Establishment> createEstablishment(@Validated(Create.class) @RequestBody Establishment establishment,
                                                             AccreditedNetwork accreditedNetwork) {
        log.info("create an establishment for network={}", accreditedNetwork.documentNumber());
        Establishment created = establishmentBranchService.create(establishment, accreditedNetwork);
        return ResponseEntity
                .created(URI.create("/accredited-networks/me/establishments/"+created.getId()))
                .body(created);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/accredited-networks/me/establishments/{id}", method = RequestMethod.PUT)
    public void updateEstablishment(@PathVariable  String id, @Validated(Update.class) @RequestBody Establishment establishment,
                                    AccreditedNetwork accreditedNetwork) {
        establishment.setId(id);
        log.info("update an establishment={} for network={}", id, accreditedNetwork.documentNumber());
        establishmentService.update(id,establishment, accreditedNetwork);
    }


    @JsonView(Views.Branch.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/accredited-networks/me/establishments/branches", method = RequestMethod.POST)
    public ResponseEntity<Branch> createBranch(@Validated(Create.class) @RequestBody Branch branch, AccreditedNetwork accreditedNetwork) {
        log.info("create a branch establishment for network={}", accreditedNetwork.documentNumber());
        Branch created = branchService.create(branch, accreditedNetwork);
        return ResponseEntity
                .created(URI.create("/accredited-networks/me/establishments/branches/"+created.getId()))
                .body(created);

    }

    @JsonView(Views.Branch.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/accredited-networks/me/establishments/branches/{id}", method = RequestMethod.GET)
    public Branch getBranch(@PathVariable  String id, AccreditedNetwork accreditedNetwork) {
        log.info("get a branch establishment={} for network={}",id, accreditedNetwork.documentNumber());
        return branchService.findById(id, accreditedNetwork);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/accredited-networks/me/establishments/branches/{id}", method = RequestMethod.PUT)
    public void updateBranch(@PathVariable  String id, @Validated(Update.class) @RequestBody Branch branch, AccreditedNetwork accreditedNetwork) {
        branch.setId(id);
        log.info("update a branch establishment={} for network={}", id, accreditedNetwork.documentNumber());
        branchService.update(id,branch, accreditedNetwork);
    }

    @JsonView(Views.Branch.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/accredited-networks/me/establishments/branches", method = RequestMethod.GET)
    public Results<Branch> getBranchByParams(BranchFilter filter, @Validated UnovationPageRequest pageable, AccreditedNetwork accreditedNetwork) {
        log.info("search branch establishment with filter={} for network={}", filter, accreditedNetwork.documentNumber());
        Page<Branch> page =  branchService.findByFilter(filter, accreditedNetwork, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/accredited-networks/me/establishments/branches", api));
    }

    @JsonView(Views.Scheduling.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/accredited-networks/me/establishments/schedules", method = RequestMethod.POST)
    public ResponseEntity<Scheduling> createScheduling(@Validated(Create.class) @RequestBody Scheduling scheduling,
                                                       AccreditedNetwork accreditedNetwork, UserDetail currentUser) {
        scheduling.setUser(currentUser);
        log.info("create a scheduling establishment for network={}", accreditedNetwork.documentNumber());
        Scheduling created = schedulingService.create(scheduling, accreditedNetwork);
        return ResponseEntity
                .created(URI.create("/accredited-networks/me/establishments/schedules/"+created.getId()))
                .body(created);

    }

    @JsonView(Views.Scheduling.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/accredited-networks/me/establishments/schedules/{id}", method = RequestMethod.GET)
    public Scheduling getScheduling(@PathVariable  String id, AccreditedNetwork accreditedNetwork) {
        log.info("get a scheduling establishment={} for network={}",id, accreditedNetwork.documentNumber());
        return schedulingService.findById(id, accreditedNetwork);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/accredited-networks/me/establishments/schedules/{id}", method = RequestMethod.PUT)
    public void updateScheduling(@PathVariable  String id, @Validated(Update.class) @RequestBody Scheduling scheduling,
                                 AccreditedNetwork accreditedNetwork, UserDetail currentUser) {
        scheduling.setUser(currentUser);
        scheduling.setId(id);
        log.info("update a scheduling establishment={} for network={}", id, accreditedNetwork.documentNumber());
        schedulingService.update(id, scheduling, accreditedNetwork);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/accredited-networks/me/establishments/schedules/{id}", method = RequestMethod.DELETE)
    public void cancelScheduling(@PathVariable  String id, AccreditedNetwork accreditedNetwork) {
        log.info("cancelling a scheduling establishment={} for network={}", id, accreditedNetwork.documentNumber());
        schedulingService.cancelById(id, accreditedNetwork);
    }

    @JsonView(Views.Scheduling.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/accredited-networks/me/establishments/schedules", method = RequestMethod.GET)
    public Results<Scheduling> getSchedulingByParams(SchedulingFilter filter, @Validated UnovationPageRequest pageable, AccreditedNetwork accreditedNetwork) {
        log.info("search scheduling establishment with filter={} for network={}", filter, accreditedNetwork.documentNumber());
        Page<Scheduling> page =  schedulingService.findAll(filter, accreditedNetwork, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/accredited-networks/me/establishments/schedules", api));
    }

    @JsonView(Views.EstablishmentEvent.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/accredited-networks/me/event-fees/{id}", method = RequestMethod.GET)
    public EstablishmentEvent getEstablishmentEvent(AccreditedNetwork accreditedNetwork, @PathVariable  String id) {
        log.info("get establishmentEvent={} for network={}", id, accreditedNetwork.documentNumber());
        return establishmentEventService.findByNetworkIdAndId(id, accreditedNetwork);
    }

    @JsonView(Views.EstablishmentEvent.List.class)
    @GetMapping(value = "/accredited-networks/me/event-fees")
    public Results<EstablishmentEvent> getEstablishmentEventsByParams(AccreditedNetwork accreditedNetwork,
                                                           EstablishmentEventFilter filter,
                                                           @Validated UnovationPageRequest pageable) {
        log.info("search establishmentEvent with filter={} for network={}", filter, accreditedNetwork.documentNumber());
        filter.setNetwork(accreditedNetwork.getId());
        Page<EstablishmentEvent> page =  establishmentEventService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/accredited-networks/me/event-fees", api));
    }

    @JsonView(Views.EstablishmentEvent.Detail.class)
    @PostMapping(value = "/accredited-networks/me/event-fees")
    public ResponseEntity<EstablishmentEvent> createEstablishmentEvent(@Validated(Create.class) @RequestBody EstablishmentEvent establishmentEvent,
                                                                  AccreditedNetwork accreditedNetwork) {
        log.info("create an establishmentEvent event fee for network={}", accreditedNetwork.documentNumber());
        EstablishmentEvent created = establishmentEventService.create(establishmentEvent.establishmentId(), establishmentEvent, accreditedNetwork);
        return ResponseEntity
                .created(URI.create("/accredited-networks/me/event-fees/"+created.getId()))
                .body(created);
    }

    @PutMapping(value = "/accredited-networks/me/event-fees/{id}")
    public void updateEstablishmentEvent(@PathVariable  String id, @Validated(Update.class) @RequestBody EstablishmentEvent establishment,
                                    AccreditedNetwork accreditedNetwork) {
        establishment.setId(id);
        log.info("update an establishment event fee={} for network={}", id, accreditedNetwork.documentNumber());
        establishmentEventService.update(id,establishment, accreditedNetwork);
    }

    @DeleteMapping(value = "/accredited-networks/me/event-fees/{id}")
    public void deleteEstablishmentEvent(@PathVariable  String id,
                                         AccreditedNetwork accreditedNetwork) {
        log.info("update an establishment event fee={} for network={}", id, accreditedNetwork.documentNumber());
        establishmentEventService.delete(id, accreditedNetwork);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/accredited-networks/me/event-fees", method = RequestMethod.POST,
            consumes = "multipart/form-data")
    public void createFromCsvByMe(AccreditedNetwork accreditedNetwork, @RequestParam MultipartFile file,
                                  @RequestAttribute(value ="establishment", required = false) String establishment){
        String fileName = file.getOriginalFilename();
        log.info("reading establishment event fee csv file {}", fileName);
        establishmentEventService.createFromCsv(establishment, file,accreditedNetwork);
    }

    @JsonView(Views.Contractor.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/accredited-networks/me/contractors/{id}")
    public Contractor getContractorForNetwork(AccreditedNetwork accreditedNetwork, @PathVariable  String id) {
        log.info("get contractor={} for network={}", id, accreditedNetwork.documentNumber());
        return contractorService.getByIdForNetwork(id, accreditedNetwork);
    }

    @JsonView(Views.Contractor.List.class)
    @GetMapping(value = "/accredited-networks/me/contractors")
    public Results<Contractor> getContractorByParams(AccreditedNetwork accreditedNetwork,
                                                                      ContractorFilter filter,
                                                                      @Validated UnovationPageRequest pageable) {
        log.info("search contractor with filter={} for network={}", filter, accreditedNetwork.documentNumber());
        filter.setAccreditedNetwork(accreditedNetwork.getId());
        Page<Contractor> page =  contractorService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/accredited-networks/me/contractors", api));
    }

    @JsonView(Views.PaymentInstrument.List.class)
    @GetMapping(value = "/accredited-networks/me/payment-instruments")
    public Results<PaymentInstrument> getPaymentInstrumentByParams(AccreditedNetwork accreditedNetwork,
                                                                   PaymentInstrumentFilter filter,
                                                                   @Validated UnovationPageRequest pageable) {
        log.info("search payment instrument with filter={} for network={}", filter, accreditedNetwork.documentNumber());
        filter.setAccreditedNetwork(accreditedNetwork.getId());
        Page<PaymentInstrument> page =  paymentInstrumentService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/accredited-networks/me/payment-instruments", api));
    }

    @JsonView(Views.Contract.List.class)
    @GetMapping(value = "/accredited-networks/me/contractors/{id}/contracts")
    public Results<Contract> getContractByParams(AccreditedNetwork accreditedNetwork,
                                                 @PathVariable  String id,
                                                 ContractFilter filter,
                                                 @Validated UnovationPageRequest pageable) {
        log.info("search contract with filter={} for contractor={} and logged network={}",
                 filter, id, accreditedNetwork.documentNumber());
        contractorService.getById(id);
        filter.setContractor(id);
        filter.setAccreditedNetwork(accreditedNetwork.getId());
        Page<Contract> page =  contractService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/accredited-networks/me/contractors/{%s}/contracts", api, id));
    }

    @JsonView(Views.AuthorizedMember.List.class)
    @GetMapping(value = "/accredited-networks/me/authorized-members")
    public Results<AuthorizedMember> getAuthorizedMemberByParams(AccreditedNetwork accreditedNetwork,
                                                                 AuthorizedMemberFilter filter,
                                                                 @Validated UnovationPageRequest pageable) {
        log.info("search authorized member with filter={} for network={}", filter, accreditedNetwork.documentNumber());
        filter.setNetworkId(accreditedNetwork.getId());
        Page<AuthorizedMember> page =  authorizedMemberService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/accredited-networks/me/authorized-members", api));
    }

    @JsonView(Views.AccreditedNetwork.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/accredited-networks/menu")
    List<AccreditedNetwork> listForMenu() {
        return service.listForMenu();
    }

}
