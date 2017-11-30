package br.com.unopay.api.uaa.controller;

import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.uaa.model.Authority;
import br.com.unopay.api.uaa.model.Group;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.model.filter.GroupFilter;
import br.com.unopay.api.uaa.service.GroupService;
import br.com.unopay.api.util.StringJoiner;
import br.com.unopay.bootcommons.jsoncollections.ListResults;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import java.net.URI;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Timed(prefix = "api")
@RestController
public class GroupController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupController.class);

    @Value("${unopay.api}")
    private String api;

    private GroupService service;

    @Autowired
    public GroupController(GroupService service) {
        this.service = service;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_GROUPS')")
    @RequestMapping(value = "/groups", method = RequestMethod.POST)
    public ResponseEntity<Group> create(@Validated(Create.class) @RequestBody Group group) {
        LOGGER.info("creating uaa group {}", group);
        Group created = service.create(group);
        return ResponseEntity
                .created(URI.create("/groups/"+created.getId()))
                .body(created);

    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_GROUPS')")
    @RequestMapping(value = "/groups/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") String id) {
        LOGGER.info("deleting uaa group {}", id);
        service.delete(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_GROUPS')")
    @RequestMapping(value = "/groups/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable("id") String id,@RequestBody Group group) {
        LOGGER.info("updating uaa group {} {}", id, group);
        service.update(id,group);
    }


    @JsonView(Views.Group.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_GROUPS')")
    @RequestMapping(value = "/groups/{id}", method = RequestMethod.GET)
    public Group get(@PathVariable  String id) {
        LOGGER.info("get uaa user={}", id);
        return service.getById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @JsonView(Views.Group.List.class)
    @PreAuthorize("hasRole('ROLE_LIST_GROUPS')")
    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    Results<Group> findAllGroups(GroupFilter groupFilter,@Valid UnovationPageRequest pageable) {
        LOGGER.info("getting all groups by filter={}",groupFilter);
        Page<Group> page =  service.findAll(groupFilter,pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), api);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_GROUPS')")
    @RequestMapping(value = "/groups/{id}/members", method = RequestMethod.PUT)
    public void groupMembers(@PathVariable("id") String id, @RequestBody Set<String> membersIds) {
        String memberIdsAsString = StringJoiner.join(membersIds);
        LOGGER.info("add members={} to group={}", memberIdsAsString, id);
        service.addMembers(id, membersIds);
    }

    @ResponseStatus(HttpStatus.OK)
    @JsonView(Views.Group.Detail.class)
    @PreAuthorize("hasRole('ROLE_LIST_GROUPS')")
    @RequestMapping(value = "/groups/{id}/members", method = RequestMethod.GET)
    public Results<UserDetail> getGroupMembers(@PathVariable("id") String id, @Valid UnovationPageRequest pageable) {
        LOGGER.info("get members to group={}", id);
        Page<UserDetail> page =  service.findMembers(id, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/users", api));
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_GROUPS')")
    @RequestMapping(value = "/groups/{id}/authorities", method = RequestMethod.PUT)
    public void groupAuthorities(@PathVariable("id") String id, @RequestBody Set<String> authoritiesIds) {
        String authorityIdsAsString = StringJoiner.join(authoritiesIds);
        LOGGER.info("add authorities={} to group={}", authorityIdsAsString, id);
        service.addAuthorities(id, authoritiesIds);
    }

    @ResponseStatus(HttpStatus.OK)
    @JsonView(Views.Group.Detail.class)
    @PreAuthorize("hasRole('ROLE_LIST_GROUPS')")
    @RequestMapping(value = "/groups/{id}/authorities", method = RequestMethod.GET)
    public Results<Authority> getGroupAuthorities(@PathVariable("id") String id) {
        LOGGER.info("get authorities to group={}", id);
        List<Authority> authorities = service.findAuthorities(id);
        return new ListResults<>(authorities, String.format("%s/authorities", api));
    }
}
