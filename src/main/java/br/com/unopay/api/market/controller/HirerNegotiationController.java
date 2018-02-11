package br.com.unopay.api.market.controller;

import br.com.unopay.api.market.model.HirerNegotiation;
import br.com.unopay.api.market.model.filter.HirerNegotiationFilter;
import br.com.unopay.api.market.service.HirerNegotiationService;
import br.com.unopay.api.model.validation.group.Create;
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

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Slf4j
@RestController
@Timed(prefix = "api")
public class HirerNegotiationController {

    private HirerNegotiationService service;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public HirerNegotiationController(HirerNegotiationService service) {
        this.service = service;
    }

    @JsonView(Views.HirerNegotiation.Detail.class)
    @ResponseStatus(CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_HIRER_NEGOTIATION')")
    @RequestMapping(value = "/hirer-negotiations", method = POST)
    public ResponseEntity<HirerNegotiation> create(@Validated(Create.class) @RequestBody HirerNegotiation negotiation){
        log.info("creating negotiation={}", negotiation);
        HirerNegotiation created = service.create(negotiation);
        log.info("created negotiation={}", created);
        return created(URI.create(String
                .format("/hirer-negotiations/%s",created.getId()))).body(created);
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_HIRER_NEGOTIATION')")
    @RequestMapping(value = "/hirer-negotiations/{id}", method = PUT)
    public void update(@PathVariable String id,
                       @Validated(Create.class) @RequestBody HirerNegotiation negotiation){
        log.info("updating negotiation={}", negotiation);
        service.update(id, negotiation);
    }

    @JsonView(Views.HirerNegotiation.Detail.class)
    @ResponseStatus(OK)
    @PreAuthorize("hasRole('ROLE_LIST_HIRER_NEGOTIATION')")
    @RequestMapping(value = "/hirer-negotiations/{id}", method = GET)
    public HirerNegotiation get(@PathVariable String id) {
        log.info("get negotiation={}", id);
        return service.findById(id);
    }

    @JsonView(Views.HirerNegotiation.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_HIRER_NEGOTIATION')")
    @RequestMapping(value = "/hirer-negotiations", method = RequestMethod.GET)
    public Results<HirerNegotiation> getByParams(HirerNegotiationFilter filter,
                                                 @Validated UnovationPageRequest pageable) {
        log.info("search negotiation with filter={}", filter);
        Page<HirerNegotiation> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/hirer-negotiations", api));
    }
}
