package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Branch;
import br.com.unopay.api.bacen.model.filter.BranchFilter;
import br.com.unopay.api.bacen.service.BranchService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@Timed(prefix = "api")
public class BranchController {

    private BranchService service;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public BranchController(BranchService service) {
        this.service = service;
    }

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/branches", method = RequestMethod.POST)
    public ResponseEntity<Branch> create(@Validated(Create.class) @RequestBody Branch branch) {
        log.info("creating branch {}", branch);
        Branch created = service.create(branch);
        return ResponseEntity
                .created(URI.create("/branches/"+created.getId()))
                .body(created);

    }
    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/branches/{id}", method = RequestMethod.GET)
    public Branch get(@PathVariable  String id) {
        log.info("get branch={}", id);
        return service.findById(id);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/branches/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody Branch branch) {
        branch.setId(id);
        log.info("updating branches {}", branch);
        service.update(id,branch);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/branches/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing branch id={}", id);
        service.delete(id);
    }

    @JsonView(Views.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/branches", method = RequestMethod.GET)
    public Results<Branch> getByParams(BranchFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search branch with filter={}", filter);
        Page<Branch> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/branches", api));
    }
}
