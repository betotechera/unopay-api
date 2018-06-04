package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.filter.ContractorFilter;
import br.com.unopay.api.bacen.model.filter.EstablishmentFilter;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.bacen.service.EstablishmentService;
import br.com.unopay.api.market.model.ContractorBonus;
import br.com.unopay.api.market.model.filter.ContractorBonusFilter;
import br.com.unopay.api.market.service.BonusBillingService;
import br.com.unopay.api.market.service.ContractorBonusService;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.model.filter.ContractFilter;
import br.com.unopay.api.model.filter.PaymentInstrumentFilter;
import br.com.unopay.api.model.filter.ServiceAuthorizeFilter;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.service.ContractService;
import br.com.unopay.api.service.PaymentInstrumentService;
import br.com.unopay.api.service.ServiceAuthorizeService;
import br.com.unopay.api.uaa.model.UserDetail;
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
public class EstablishmentController {

    private EstablishmentService service;
    private ServiceAuthorizeService authorizeService;
    private ContractorService contractorService;
    private PaymentInstrumentService paymentInstrumentService;
    private ContractService contractService;
    private ContractorBonusService contractorBonusService;
    private BonusBillingService bonusBillingService;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public EstablishmentController(EstablishmentService service,
                                   ServiceAuthorizeService authorizeService,
                                   ContractorService contractorService,
                                   PaymentInstrumentService paymentInstrumentService,
                                   ContractService contractService,
                                   ContractorBonusService contractorBonusService,
                                   BonusBillingService bonusBillingService) {
        this.service = service;
        this.authorizeService = authorizeService;
        this.contractorService = contractorService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.contractService = contractService;
        this.contractorBonusService = contractorBonusService;
        this.bonusBillingService = bonusBillingService;
    }

    @JsonView(Views.Establishment.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/establishments", method = RequestMethod.POST)
    public ResponseEntity<Establishment> create(@Validated(Create.class) @RequestBody Establishment establishment) {
        log.info("creating establishment {}", establishment);
        Establishment created = service.create(establishment);
        return ResponseEntity
                .created(URI.create("/establishments/"+created.getId()))
                .body(created);
    }

    @JsonView(Views.Establishment.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/establishments/{id}", method = RequestMethod.GET)
    public Establishment get(@PathVariable  String id) {
        log.info("get establishment={}", id);
        return service.findById(id);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/establishments/{id}", method = PUT)
    public void update(@PathVariable String id, @Validated(Update.class) @RequestBody Establishment establishment) {
        establishment.setId(id);
        log.info("updating establishment={}", establishment);
        service.update(id,establishment);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/establishments/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing establishment id={}", id);
        service.delete(id);
    }

    @JsonView(Views.Establishment.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/establishments", method = RequestMethod.GET)
    public Results<Establishment> getByParams(EstablishmentFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search establishment with filter={}", filter);
        Page<Establishment> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/establishments", api));
    }

    @JsonView(Views.Establishment.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/establishments/me", method = RequestMethod.GET)
    public Establishment getMe(Establishment establishment) {
        log.info("get establishment={}", establishment.documentNumber());
        return service.findById(establishment.getId());
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/establishments/me", method = PUT)
    public void updateMe(Establishment current,
                         @Validated(Update.class) @RequestBody Establishment establishment) {
        log.info("updating establishments={}", establishment.documentNumber());
        service.update(current.getId(), establishment);
    }

    @ResponseStatus(CREATED)
    @JsonView(Views.ServiceAuthorize.Detail.class)
    @RequestMapping(value = "/establishments/me/service-authorizations", method = POST)
    public ResponseEntity<ServiceAuthorize> createAuthorization(UserDetail currentUser,
                                                                @Validated(Create.class)
                                                                @RequestBody ServiceAuthorize serviceAuthorize) {
        Establishment establishment = currentUser.getEstablishment();
        log.info("Authorizing service={} for establishment={}", serviceAuthorize, establishment.documentNumber());
        serviceAuthorize.setEstablishment(establishment);
        ServiceAuthorize created = authorizeService.create(currentUser, serviceAuthorize);
        log.info("authorized service={}", created);
        return created(URI.create(
                String.format("%s/establishments/me/service-authorizations/%s",api, created.getId()))).body(created);

    }

    @ResponseStatus(OK)
    @JsonView(Views.ServiceAuthorize.Detail.class)
    @RequestMapping(value = "/establishments/me/service-authorizations/{id}", method = GET)
    public ServiceAuthorize getAuthorization(Establishment establishment, @PathVariable String id) {
        log.info("get serviceAuthorize={} for establishment={}", id, establishment.documentNumber());
        return authorizeService.findByIdForEstablishment(id, establishment);
    }

    @ResponseStatus(OK)
    @JsonView({Views.ServiceAuthorize.List.class})
    @RequestMapping(value = "/establishments/me/service-authorizations", method = GET)
    public Results<ServiceAuthorize> getAuthorizationsByParams(ServiceAuthorizeFilter filter,
                                                 @Validated UnovationPageRequest pageable) {
        log.info("search ServiceAuthorize with filter={}", filter);
        Page<ServiceAuthorize> page =  authorizeService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/establishments/me/service-authorizations", api));
    }


    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/establishments/me/service-authorizations/{id}", method = RequestMethod.DELETE)
    public void cancel(Establishment establishment, @PathVariable String id) {
        log.info("cancel serviceAuthorize={} for establishment={}", id, establishment.documentNumber());
        authorizeService.cancelForEstablishment(id,establishment);
    }

    @JsonView(Views.Contractor.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/establishments/me/contractors/{id}", method = RequestMethod.GET)
    public Contractor getContractor(Establishment establishment, @PathVariable  String id) {
        log.info("get Contractor={} for establishment={}", id, establishment.documentNumber());
        return contractorService.getByIdForIssuers(id, establishment.getNetwork().issuersIds());
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/establishments/me/contractors/{id}", method = PUT)
    public void updateContractor(Establishment establishment,
                                 @PathVariable String id,
                                 @Validated(Update.class) @RequestBody Contractor contractor){
        contractor.setId(id);
        log.info("updating contractor={} for establishment={}", contractor, establishment.documentNumber());
        contractorService.updateForIssuer(id, establishment.getNetwork().issuersIds(), contractor);
    }

    @JsonView(Views.Contractor.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/establishments/me/contractors", method = RequestMethod.GET)
    public Results<Contractor> getContractorsByParams(Establishment establishment, ContractorFilter filter,
                                                      @Validated UnovationPageRequest pageable){
        log.info("search Contractor with filter={} for establishment={}", filter, establishment.documentNumber());
        filter.setIssuers(establishment.getNetwork().issuersIds());
        Page<Contractor> page =  contractorService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/establishments/me/contractors", api));
    }

    @JsonView(Views.PaymentInstrument.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/establishments/me/payment-instruments", method = RequestMethod.GET)
    public Results<PaymentInstrument> getPaymentInstrumentByParams(Establishment establishment,
                                                                   PaymentInstrumentFilter filter,
                                                                   @Validated UnovationPageRequest pageable) {
        log.info("search paymentInstrument with filter={}", filter);
        filter.setIssuer(establishment.getNetwork().issuersIds());
        Page<PaymentInstrument> page =  paymentInstrumentService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/establishments/me/payment-instruments", api));
    }

    @ResponseStatus(OK)
    @JsonView(Views.Contract.List.class)
    @RequestMapping(value = "/establishments/me/contractors/{contractorId}/contracts", method = GET)
    public Results<Contract> getByParams(@PathVariable String contractorId, Establishment establishment,
                                         ContractFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search contract with filter={}", filter);
        filter.setIssuers(establishment.getNetwork().issuersIds());
        filter.setContractor(contractorId);
        Page<Contract> page =  contractService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/establishments/me/contractors/%s/contracts", api, contractorId));
    }

    @JsonView(Views.ContractorBonus.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/establishments/me/contractor-bonuses/{id}", method = RequestMethod.GET)
    public ContractorBonus getContractorBonus(Establishment establishment, @PathVariable String id) {
        log.info("get Contractor Bonus={} for Establishment={}", id, establishment.documentNumber());
        return contractorBonusService.findByIdForPerson(id, establishment.getPerson());
    }

    @JsonView(Views.ContractorBonus.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/establishments/me/contractor-bonuses", method = RequestMethod.GET)
    public Results<ContractorBonus> getContractorBonusByParams(Establishment establishment,
                                                               ContractorBonusFilter filter,
                                                               @Validated UnovationPageRequest pageable) {
        log.info("search Contractor Bonus with filter={}", filter);
        filter.setPayer(establishment.documentNumber());
        Page<ContractorBonus> page =  contractorBonusService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/establishments/me/contractor-bonuses", api));
    }

    @ResponseStatus(CREATED)
    @JsonView(Views.ContractorBonus.Detail.class)
    @RequestMapping(value = "/establishments/me/contractor-bonuses", method = POST)
    public ResponseEntity<ContractorBonus> createContractorBonus(UserDetail currentUser,
                                                                 @Validated(Create.class)
                                                                 @RequestBody ContractorBonus contractorBonus) {
        Establishment establishment = currentUser.getEstablishment();
        log.info("creating Contractor Bonus={} for Establishment={}", contractorBonus, establishment.documentNumber());
        ContractorBonus created = contractorBonusService.createForEstablishment(establishment, contractorBonus);
        log.info("Contractor Bonus={}", created);
        return created(URI.create(
                String.format("%s/establishments/me/contractor-bonuses/%s",api, created.getId()))).body(created);
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/establishments/me/contractor-bonuses/{id}", method = PUT)
    public void updateContractorBonus(Establishment establishment,
                                      @PathVariable String id,
                                      @Validated(Update.class) @RequestBody ContractorBonus contractorBonus){
        log.info("updating Contractor Bonus={} for Establishment={}", contractorBonus, establishment.documentNumber());
        contractorBonusService.updateForEstablishment(id, establishment, contractorBonus);
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_ESTABLISHMENT')")
    @RequestMapping(value = "/establishments/{id}/bonus-billings", method = PUT)
    public void processAllBonusBillings(@PathVariable String id){
        log.info("find establishment={}", id);
        Establishment establishment = service.findById(id);
        log.info("process all bonus billing for establishment={}", establishment.documentNumber());
        bonusBillingService.process(establishment.getPerson());
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/establishments/me/bonus-billings", method = PUT)
    public void processAllBonusBillings(Establishment establishment){
        log.info("process all bonus billing for issuer={}", establishment.documentNumber());
        bonusBillingService.process(establishment.getPerson());
    }
}
