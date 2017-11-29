package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Institution;
import br.com.unopay.api.bacen.model.filter.InstitutionFilter;
import br.com.unopay.api.bacen.service.InstitutionService;
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
import org.springframework.security.oauth2.provider.OAuth2Authentication;
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
public class InstitutionController {

    private InstitutionService service;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public InstitutionController(InstitutionService service) {
        this.service = service;
     }

    @JsonView({Views.Institution.Detail.class})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/institutions", method = RequestMethod.POST)
    public ResponseEntity<Institution> create(@Validated(Create.class) @RequestBody Institution institution) {
        log.info("creating institution {}", institution);
        Institution created = service.create(institution);
        return ResponseEntity
                .created(URI.create("/institutions/"+created.getId()))
                .body(created);

    }

    @JsonView({Views.Institution.Detail.class})
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/institutions/{id}", method = RequestMethod.GET)
    public Institution get(@PathVariable  String id) {
        log.info("get Institution={}", id);
        return service.getById(id);
    }

    @JsonView({Views.Institution.Detail.class})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/institutions/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody Institution institution) {
        institution.setId(id);
        log.info("updating institution {}", institution);
        service.update(id,institution);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/institutions/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing institution id={}", id);
        service.delete(id);
    }

    @JsonView(Views.Institution.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/institutions", method = RequestMethod.GET)
    public Results<Institution> getByParams(InstitutionFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search Institution with filter={}", filter);
        Page<Institution> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/institutions", api));
    }

    @JsonView({Views.Institution.Detail.class})
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/institutions/me", method = RequestMethod.GET)
    public Institution getMe(OAuth2Authentication authentication) {
        log.info("get Institution={}", authentication.getName());
        return service.getMe(authentication.getName());
    }

    @JsonView({Views.Institution.Detail.class})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/institutions/me", method = RequestMethod.PUT)
    public void updateMe(OAuth2Authentication authentication, @Validated(Update.class) @RequestBody Institution institution) {
        log.info("updating institution={}", authentication.getName());
        service.updateMe(authentication.getName(),institution);
    }

    @JsonView(Views.Institution.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/institutions", method = RequestMethod.GET, params = "currentUser")
    public Results<Institution> getMeByParams(OAuth2Authentication authentication,
                                              InstitutionFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search Institution with filter={}", filter);
        Page<Institution> page =  service.findMeByFilter(authentication.getName(), filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/institutions", api));
    }

}
