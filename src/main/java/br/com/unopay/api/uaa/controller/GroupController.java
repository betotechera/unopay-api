package br.com.unopay.api.uaa.controller;

import br.com.unopay.api.uaa.model.Group;
import br.com.unopay.api.uaa.model.valistionsgroups.Create;
import br.com.unopay.api.uaa.model.valistionsgroups.Views;
import br.com.unopay.api.uaa.service.GroupService;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@Timed(prefix = "api")
@RestController
public class GroupController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupController.class);

    @Value("${unopay.api}")
    private String api;

    @Autowired
    private GroupService service;

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/groups", method = RequestMethod.POST)
    public ResponseEntity<Group> create(@Validated(Create.class) @RequestBody Group group) {
        LOGGER.info("creating uaa group {}", group);
        Group created = service.create(group);
        return ResponseEntity
                .created(URI.create("/groups/"+created.getId()))
                .body(created);

    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/groups/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") String id) {
        LOGGER.info("deleting uaa group {}", id);
        service.delete(id);
    }

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/groups/{id}", method = RequestMethod.GET)
    public Group get(@PathVariable  String id) {
        LOGGER.info("get uaa user={}", id);
        return service.getById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    Results<Group> findAllGroups(@Valid UnovationPageRequest pageable) {
        LOGGER.info("getting all groups");
        Page<Group> page =  service.findAll(pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), api);
    }
}
