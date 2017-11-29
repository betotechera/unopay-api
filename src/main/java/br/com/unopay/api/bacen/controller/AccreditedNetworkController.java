package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.filter.AccreditedNetworkFilter;
import br.com.unopay.api.bacen.service.AccreditedNetworkService;
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
@PreAuthorize("#oauth2.isUser()")
@Timed(prefix = "api")
public class AccreditedNetworkController {

    private AccreditedNetworkService service;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public AccreditedNetworkController(AccreditedNetworkService service) {
        this.service = service;
     }

    @JsonView(Views.AccreditedNetwork.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/accredited-networks", method = RequestMethod.POST)
    public ResponseEntity<AccreditedNetwork> create(@Validated(Create.class)
                                                        @RequestBody AccreditedNetwork accreditedNetwork) {
        log.info("creating accreditedNetwork {}", accreditedNetwork);
        AccreditedNetwork created = service.create(accreditedNetwork);
        return ResponseEntity
                .created(URI.create("/accredited-networks/"+created.getId()))
                .body(created);

    }

    @JsonView({Views.AccreditedNetwork.Detail.class})
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/accredited-networks/{id}", method = RequestMethod.GET)
    public AccreditedNetwork get(@PathVariable  String id) {
        log.info("get AccreditedNetwork={}", id);
        return service.getById(id);
    }

    @JsonView(Views.AccreditedNetwork.Detail.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/accredited-networks/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class)
    @RequestBody AccreditedNetwork accreditedNetwork) {
        accreditedNetwork.setId(id);
        log.info("updating accreditedNetwork {}", accreditedNetwork);
        service.update(id,accreditedNetwork);
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/accredited-networks/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing accreditedNetwork id={}", id);
        service.delete(id);
    }

    @JsonView(Views.AccreditedNetwork.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/accredited-networks", method = RequestMethod.GET)
    public Results<AccreditedNetwork> getByParams(AccreditedNetworkFilter filter,
                                                  @Validated UnovationPageRequest pageable) {
        log.info("search AccreditedNetwork with filter={}", filter);
        Page<AccreditedNetwork> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/accredited-networks", api));
    }

    @JsonView({Views.AccreditedNetwork.Detail.class})
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/accredited-networks/me", method = RequestMethod.GET)
    public AccreditedNetwork getMe(OAuth2Authentication authentication) {
        log.info("get AccreditedNetwork={}", authentication.getName());
        return service.getMe(authentication.getName());
    }

    @JsonView(Views.AccreditedNetwork.Detail.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/accredited-networks/me", method = RequestMethod.PUT)
    public void updateMe(OAuth2Authentication authentication, @Validated(Update.class)
    @RequestBody AccreditedNetwork accreditedNetwork) {
        log.info("updating accreditedNetwork {}", authentication.getName());
        service.updateMe(authentication.getName(),accreditedNetwork);
    }

    @JsonView(Views.AccreditedNetwork.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/accredited-networks", method = RequestMethod.GET, params = "currentUser")
    public Results<AccreditedNetwork> getMeByParams(OAuth2Authentication authentication, AccreditedNetworkFilter filter,
                                                  @Validated UnovationPageRequest pageable) {
        log.info("search AccreditedNetwork with filter={}", filter);
        Page<AccreditedNetwork> page =  service.findMeByFilter(authentication.getName(),filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/accredited-networks", api));
    }

}
