package br.com.unopay.api.controller;

import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.model.filter.ServiceAuthorizeFilter;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.service.ServiceAuthorizeService;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import java.net.URI;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import org.springframework.http.ResponseEntity;
import static org.springframework.http.ResponseEntity.created;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Timed(prefix = "api")
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
    @PreAuthorize("#oauth2.isUser() && hasRole('ROLE_MANAGE_SERVICE_AUTHORIZE')")
    @RequestMapping(value = "/service-authorizations", method = POST)
    public ResponseEntity<ServiceAuthorize> create(OAuth2Authentication authentication, @Validated(Create.class)
                                                             @RequestBody ServiceAuthorize serviceAuthorize) {
        log.info("user={}, authorizing service={}", authentication.getName(), serviceAuthorize);
        ServiceAuthorize created = service.create(authentication.getName(),serviceAuthorize);
        log.info("authorized service={}", created);
        return created(URI.create(
                String.format("/service-authorizations/%s", created.getId()))).body(created);

    }

    @ResponseStatus(OK)
    @JsonView(Views.ServiceAuthorize.Detail.class)
    @PreAuthorize("hasRole('ROLE_LIST_SERVICE_AUTHORIZE')")
    @RequestMapping(value = "/service-authorizations/{id}", method = GET)
    public ServiceAuthorize get(@PathVariable String id) {
        log.info("get batchClosing={}", id);
        return service.findById(id);
    }

    @ResponseStatus(OK)
    @JsonView({Views.ServiceAuthorize.List.class})
    @PreAuthorize("#oauth2.isUser() && hasRole('ROLE_LIST_SERVICE_AUTHORIZE')")
    @RequestMapping(value = "/service-authorizations", method = GET)
    public Results<ServiceAuthorize> getByParams(ServiceAuthorizeFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search ServiceAuthorize with filter={}", filter);
        Page<ServiceAuthorize> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/service-authorizations", api));
    }


}
