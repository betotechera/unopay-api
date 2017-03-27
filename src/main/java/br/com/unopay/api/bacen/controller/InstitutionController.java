package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Institution;
import br.com.unopay.api.bacen.model.InstitutionFilter;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.PaymentRuleGroupFilter;
import br.com.unopay.api.bacen.service.InstitutionService;
import br.com.unopay.api.bacen.service.PaymentRuleGroupService;
import br.com.unopay.api.uaa.model.validationsgroups.Create;
import br.com.unopay.api.uaa.model.validationsgroups.Update;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@Timed(prefix = "api")
public class InstitutionController {

    private InstitutionService service;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public InstitutionController(InstitutionService service) {
        this.service = service;
     }

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/institutions", method = RequestMethod.POST)
    public ResponseEntity<Institution> create(@Validated(Create.class) @RequestBody Institution institution) {
        log.info("creating institution {}", institution);
        Institution created = service.create(institution);
        return ResponseEntity
                .created(URI.create("/institutions/"+created.getId()))
                .body(created);

    }
    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/institutions/{id}", method = RequestMethod.GET)
    public Institution get(@PathVariable  String id) {
        log.info("get Institution={}", id);
        return service.getById(id);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/institutions/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody Institution institution) {
        institution.setId(id);
        log.info("updating institution {}", institution);
        service.update(id,institution);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/institutions/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing payment rule groups id={}", id);
        service.delete(id);
    }

    @JsonView(Views.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/institutions", method = RequestMethod.GET)
    public Results<Institution> getByParams(InstitutionFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search Institution with filter={}", filter);
        Page<Institution> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/institutions", api));
    }

}
