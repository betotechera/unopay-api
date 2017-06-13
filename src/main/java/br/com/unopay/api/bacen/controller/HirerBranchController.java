package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.HirerBranch;
import br.com.unopay.api.bacen.model.filter.HirerBranchFilter;
import br.com.unopay.api.bacen.service.HirerBranchService;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Timed(prefix = "api")
public class HirerBranchController {

    private HirerBranchService service;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public HirerBranchController(HirerBranchService service) {
        this.service = service;
     }

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/hirer-branches", method = RequestMethod.POST)
    public ResponseEntity<HirerBranch> create(@Validated(Create.class) @RequestBody HirerBranch hirerBranch) {
        log.info("creating hirerBranch {}", hirerBranch);
        HirerBranch created = service.create(hirerBranch);
        return ResponseEntity
                .created(URI.create("/hirer-branches/"+created.getId()))
                .body(created);

    }
    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/hirer-branches/{id}", method = RequestMethod.GET)
    public HirerBranch get(@PathVariable  String id) {
        log.info("get HirerBranch={}", id);
        return service.getById(id);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/hirer-branches/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody HirerBranch hirerBranch) {
        hirerBranch.setId(id);
        log.info("updating hirerBranch {}", hirerBranch);
        service.update(id,hirerBranch);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/hirer-branches/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing hirerBranch id={}", id);
        service.delete(id);
    }

    @JsonView(Views.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/hirer-branches", method = RequestMethod.GET)
    public Results<HirerBranch> getByParams(HirerBranchFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search Hirer with filter={}", filter);
        Page<HirerBranch> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/hirer-branches", api));
    }

}
