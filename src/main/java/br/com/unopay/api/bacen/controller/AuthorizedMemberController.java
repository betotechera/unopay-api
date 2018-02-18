package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.AuthorizedMember;
import br.com.unopay.api.bacen.model.filter.AuthorizedMemberFilter;
import br.com.unopay.api.bacen.service.AuthorizedMemberService;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
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

import java.net.URI;

@Slf4j
@RestController
@Timed(prefix = "api")
public class AuthorizedMemberController {

    @Value("${unopay.api}")
    private String api;

    @Autowired
    AuthorizedMemberService service;

    @ResponseStatus(HttpStatus.CREATED)
    @JsonView(Views.AuthorizedMember.Detail.class)
    @PreAuthorize("hasRole('ROLE_MANAGE_AUTHORIZED_MEMBER')")
    @RequestMapping(value = "/authorized-members", method = RequestMethod.POST)
    public ResponseEntity<AuthorizedMember> create(@Validated(Create.class)
                                                    @RequestBody AuthorizedMember authorizedMember) {
        log.info("creating authorizedMember {}", authorizedMember);
        AuthorizedMember created = service.create(authorizedMember);
        return ResponseEntity
                .created(URI.create("/authorized-members/"+created.getId()))
                .body(created);
    }

    @PreAuthorize("hasRole('ROLE_MANAGE_AUTHORIZED_MEMBER')")
    @JsonView(Views.AuthorizedMember.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/authorized-members/{id}", method = RequestMethod.GET)
    public AuthorizedMember get(@PathVariable String id) {
        log.info("get authorizedMember={}", id);
        return service.findById(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @JsonView(Views.AuthorizedMember.Detail.class)
    @PreAuthorize("hasRole('ROLE_MANAGE_AUTHORIZED_MEMBER')")
    @RequestMapping(value = "/authorized-members/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable String id,
                       @Validated(Update.class) @RequestBody AuthorizedMember authorizedMember) {
        log.info("updating authorizedMember={}", authorizedMember);
        service.update(id, authorizedMember);
    }

    @ResponseStatus(HttpStatus.OK)
    @JsonView(Views.AuthorizedMember.List.class)
    @PreAuthorize("hasRole('ROLE_LIST_AUTHORIZED_MEMBER')")
    @RequestMapping(value = "/authorized-members", method = RequestMethod.GET)
    public PageableResults<AuthorizedMember> getByParams(AuthorizedMemberFilter filter,
                                                         @Validated UnovationPageRequest pageable) {
        log.info("search AuthorizedMember with filter={}", filter);
        Page<AuthorizedMember> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/authorized-members", api));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @JsonView(Views.AuthorizedMember.Detail.class)
    @PreAuthorize("hasRole('ROLE_MANAGE_AUTHORIZED_MEMBER')")
    @RequestMapping(value = "/authorized-members/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing authorizedMember id={}", id);
        service.delete(id);
    }
}
