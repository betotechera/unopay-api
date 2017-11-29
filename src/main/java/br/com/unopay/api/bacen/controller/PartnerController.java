package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Partner;
import br.com.unopay.api.bacen.model.filter.PartnerFilter;
import br.com.unopay.api.bacen.service.PartnerService;
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
public class PartnerController {

    private PartnerService service;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public PartnerController(PartnerService service) {
        this.service = service;
     }

    @JsonView(Views.Partner.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/partners", method = RequestMethod.POST)
    public ResponseEntity<Partner> create(@Validated(Create.class) @RequestBody Partner partner) {
        log.info("creating partner {}", partner);
        Partner created = service.create(partner);
        return ResponseEntity
                .created(URI.create("/partners/"+created.getId()))
                .body(created);

    }

    @JsonView(Views.Partner.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/partners/{id}", method = RequestMethod.GET)
    public Partner get(@PathVariable  String id) {
        log.info("get Partner={}", id);
        return service.getById(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/partners/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody Partner partner) {
        partner.setId(id);
        log.info("updating partner {}", partner);
        service.update(id,partner);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/partners/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing partner id={}", id);
        service.delete(id);
    }

    @JsonView(Views.Partner.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/partners", method = RequestMethod.GET)
    public Results<Partner> getByParams(PartnerFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search Partner with filter={}", filter);
        Page<Partner> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/partners", api));
    }

    @JsonView(Views.Partner.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/partners/me", method = RequestMethod.GET)
    public Partner getMe(OAuth2Authentication authentication) {
        log.info("get Partner={}", authentication.getName());
        return service.getMe(authentication.getName());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/partners/me", method = RequestMethod.PUT)
    public void updateMe(OAuth2Authentication authentication, @Validated(Update.class) @RequestBody Partner partner) {
        log.info("updating partner={}", partner);
        service.updateMe(authentication.getName(),partner);
    }

    @JsonView(Views.Partner.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/partners", method = RequestMethod.GET, params = "currentUser")
    public Results<Partner> getMeByParams(OAuth2Authentication authentication,
                                          PartnerFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search Partner with filter={}", filter);
        Page<Partner> page =  service.findMeByFilter(authentication.getName(),filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/partners", api));
    }

}
