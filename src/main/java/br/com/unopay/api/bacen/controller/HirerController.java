package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.filter.ContractorFilter;
import br.com.unopay.api.bacen.model.filter.HirerFilter;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.bacen.service.HirerService;
import br.com.unopay.api.credit.model.Credit;
import br.com.unopay.api.credit.model.filter.CreditFilter;
import br.com.unopay.api.credit.service.CreditService;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.service.ContractService;
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

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public HirerController(HirerService service,
                           ContractorService contractorService,
                           ContractService contractService,
                           CreditService creditService) {
        this.service = service;
        this.contractorService = contractorService;
        this.contractService = contractService;
        this.creditService = creditService;
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

    @JsonView(Views.Hirer.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/hirers/me", method = RequestMethod.GET)
    public Hirer getMe(OAuth2Authentication authentication) {
        log.info("get Hirer={}", authentication.getName());
        return service.getMe(authentication.getName());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/hirers/me", method = RequestMethod.PUT)
    public void updateMe(OAuth2Authentication authentication, @Validated(Update.class) @RequestBody Hirer hirer) {
        log.info("updating hirer={}", authentication.getName());
        service.updateMe(authentication.getName(),hirer);
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
        Page<Contractor> page =  contractorService.findByFilterForHirer(hirer, filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/hirers/me/contractors", api));
    }

    @JsonView(Views.Credit.Detail.class)
    @ResponseStatus(CREATED)
    @RequestMapping(value = "/hirers/me/credits", method = POST)
    public ResponseEntity<Credit> insertCredit(Hirer hirer,
                                         @Validated(Create.class) @RequestBody Credit credit) {
        log.info("inserting credit={} for hirer={}", credit, hirer.getDocumentNumber());
        credit.setHirerDocument(hirer.getDocumentNumber());
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
        Page<Credit> page =  creditService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/hirers/me/credits", api));
    }

}
