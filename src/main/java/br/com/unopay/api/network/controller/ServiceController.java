package br.com.unopay.api.network.controller;

import br.com.unopay.api.network.model.Service;
import br.com.unopay.api.network.model.filter.ServiceFilter;
import br.com.unopay.api.network.service.ServiceService;
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
import org.springframework.security.access.prepost.PreAuthorize;
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
public class ServiceController {

    @Value("${unopay.api}")
    private String api;

    private ServiceService service;

    @Autowired
    public ServiceController(ServiceService service) {
        this.service = service;
    }

    @JsonView(Views.Service.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_SERVICE')")
    @RequestMapping(value = "/services", method = RequestMethod.POST)
    public ResponseEntity<Service> create(@Validated(Create.class) @RequestBody Service service) {
        log.info("creating bank account {}", service);
        Service created = this.service.create(service);
        return ResponseEntity
                .created(URI.create("/services/"+created.getId()))
                .body(created);

    }
    @JsonView(Views.Service.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_SERVICE')")
    @RequestMapping(value = "/services/{id}", method = RequestMethod.GET)
    public Service get(@PathVariable String id) {
        log.info("get bank account={}", id);
        return service.findById(id);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_SERVICE')")
    @RequestMapping(value = "/services/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody Service service) {
        service.setId(id);
        log.info("updating bank account {}", service);
        this.service.update(id, service);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_SERVICE')")
    @RequestMapping(value = "/services/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing bank account id={}", id);
        service.delete(id);
    }

    @JsonView(Views.Service.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/services", method = RequestMethod.GET)
    public Results<Service> getByParams(ServiceFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search Service by filter with filter={}", filter);
        Page<Service> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/services", api));
    }

}
