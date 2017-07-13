package br.com.unopay.api.controller;

import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.model.filter.BatchClosingFilter;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Views;
import static br.com.unopay.api.model.validation.group.Views.List;
import static br.com.unopay.api.model.validation.group.Views.Public;
import br.com.unopay.api.service.BatchClosingService;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import org.springframework.http.ResponseEntity;
import static org.springframework.http.ResponseEntity.created;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;


@Slf4j
@RestController
@Timed(prefix = "api")
public class BatchClosingController {

    @Value("${unopay.api}")
    private String api;

    private BatchClosingService service;

    @Autowired
    public BatchClosingController(BatchClosingService service) {
        this.service = service;
    }

    @JsonView(Public.class)
    @ResponseStatus(CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_BATCH_CLOSING')")
    @RequestMapping(value = "/batch-closings", method = POST)
    public ResponseEntity<BatchClosing> create(@Validated(Create.class) @RequestBody BatchClosing batchClosing) {
        log.info("creating batchClosing {}", batchClosing);
        BatchClosing created = service.create(batchClosing.establishmentId(), batchClosing.getClosingDateTime());
        return created(URI.create("/batch-closings/"+batchClosing.getId())).body(created);

    }
    @ResponseStatus(OK)
    @JsonView(Public.class)
    @PreAuthorize("hasRole('ROLE_LIST_BATCH_CLOSING')")
    @RequestMapping(value = "/batch-closings/{id}", method = GET)
    public BatchClosing get(@PathVariable String id) {
        log.info("get batchClosing={}", id);
        return service.findById(id);
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_BATCH_CLOSING')")
    @RequestMapping(value = "/batch-closings/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing batchClosing id={}", id);
    }

    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_BATCH_CLOSING')")
    @RequestMapping(value = "/batch-closings/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @RequestBody BatchClosing batchClosing) {
        log.info("removing batchClosing id={}", id);
    }

    @ResponseStatus(OK)
    @JsonView({Views.BatchClosing.List.class})
    @PreAuthorize("hasRole('ROLE_LIST_BATCH_CLOSING')")
    @RequestMapping(value = "/batch-closings", method = GET)
    public Results<BatchClosing> getByParams(BatchClosingFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search batchClosing with filter={}", filter);
        Page<BatchClosing> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/batch-closings", api));
    }

    @ResponseStatus(OK)
    @JsonView(List.class)
    @PreAuthorize("#oauth2.isUser() && hasRole('ROLE_LIST_BATCH_CLOSING')")
    @RequestMapping(value = "/batch-closings/my", method = GET)
    public Results<BatchClosing> findMyByFilter(OAuth2Authentication authentication,BatchClosingFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search batchClosing with filter={}", filter);
        Page<BatchClosing> page =  service.findMyByFilter(authentication.getName(),filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/batch-closings", api));
    }

}
