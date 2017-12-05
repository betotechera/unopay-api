package br.com.unopay.api.controller;

import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.model.filter.ServiceAuthorizeFilter;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Views;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
@Timed(prefix = "api")
@PreAuthorize("#oauth2.isUser()")
public class ServiceAuthorizeController {

    @Value("${unopay.api}")
    private String api;

    private ServiceAuthorizeService service;

    @Autowired
    public ServiceAuthorizeController(ServiceAuthorizeService service) {
        this.service = service;
    }

    @ResponseStatus(CREATED)
    @JsonView(Views.ServiceAuthorize.Detail.class)
    @PreAuthorize("hasRole('ROLE_MANAGE_SERVICE_AUTHORIZE')")
    @RequestMapping(value = "/service-authorizations", method = POST)
    public ResponseEntity<ServiceAuthorize> create(UserDetail currentUser, @Validated(Create.class)
                                                             @RequestBody ServiceAuthorize serviceAuthorize) {
        log.info("user={}, authorizing service={}", currentUser.getEmail(), serviceAuthorize);
        ServiceAuthorize created = service.create(currentUser,serviceAuthorize);
        log.info("authorized service={}", created);
        return created(URI.create(
                String.format("/service-authorizations/%s", created.getId()))).body(created);

    }

    @ResponseStatus(OK)
    @JsonView(Views.ServiceAuthorize.Detail.class)
    @PreAuthorize("hasRole('ROLE_LIST_SERVICE_AUTHORIZE')")
    @RequestMapping(value = "/service-authorizations/{id}", method = GET)
    public ServiceAuthorize get(@PathVariable String id) {
        log.info("get serviceAuthorize={}", id);
        return service.findById(id);
    }

    @ResponseStatus(OK)
    @JsonView({Views.ServiceAuthorize.List.class})
    @PreAuthorize("hasRole('ROLE_LIST_SERVICE_AUTHORIZE')")
    @RequestMapping(value = "/service-authorizations", method = GET)
    public Results<ServiceAuthorize> getByParams(ServiceAuthorizeFilter filter,
                                                 @Validated UnovationPageRequest pageable) {
        log.info("search ServiceAuthorize with filter={}", filter);
        Page<ServiceAuthorize> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/service-authorizations", api));
    }

    @ResponseStatus(OK)
    @JsonView(Views.ServiceAuthorize.List.class)
    @RequestMapping(value = "/service-authorizations/my", method = GET)
    public Results<ServiceAuthorize> findMyByFilter(UserDetail currentUser,ServiceAuthorizeFilter filter,
                                                    @Validated UnovationPageRequest pageable) {
        log.info("search my serviceAuthorizes with filter={}", filter);
        Page<ServiceAuthorize> page =  service.findMyByFilter(currentUser,filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/service-authorizations", api));
    }


}
