package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.filter.HirerFilter;
import br.com.unopay.api.bacen.service.HirerService;
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

@Slf4j
@RestController
@Timed(prefix = "api")
public class HirerController {

    private HirerService service;
    private ContractService contractService;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public HirerController(HirerService service,
                           ContractService contractService) {
        this.service = service;
        this.contractService = contractService;
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
    @PreAuthorize("#oauth2.isUser()")
    @RequestMapping(value = "/hirers/me/contracts", method = RequestMethod.POST,
            consumes = "multipart/form-data")
    public void createMyFromCsvById(OAuth2Authentication authentication,
                                    @RequestParam MultipartFile file){
        String fileName = file.getOriginalFilename();
        log.info("reading clients from csv file={} for={}", fileName, authentication.getName());
        contractService.dealCloseFromCsvForCurrentUser(authentication.getName(), file);
    }

}
