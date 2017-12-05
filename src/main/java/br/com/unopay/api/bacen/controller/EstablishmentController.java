package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.Service;
import br.com.unopay.api.bacen.model.filter.EstablishmentFilter;
import br.com.unopay.api.bacen.model.filter.ServiceFilter;
import br.com.unopay.api.bacen.service.EstablishmentService;
import br.com.unopay.api.bacen.service.ServiceService;
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
@PreAuthorize("#oauth2.isUser()")
@Timed(prefix = "api")
public class EstablishmentController {

    private EstablishmentService service;
    private ServiceService serviceService;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public EstablishmentController(EstablishmentService service,
                                   ServiceService serviceService) {
        this.service = service;
        this.serviceService = serviceService;
    }

    @JsonView(Views.Establishment.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/establishments", method = RequestMethod.POST)
    public ResponseEntity<Establishment> create(@Validated(Create.class) @RequestBody Establishment establishment) {
        log.info("creating establishment {}", establishment);
        Establishment created = service.create(establishment);
        return ResponseEntity
                .created(URI.create("/establishments/"+created.getId()))
                .body(created);
    }

    @JsonView(Views.Establishment.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/establishments/{id}", method = RequestMethod.GET)
    public Establishment get(@PathVariable  String id) {
        log.info("get establishment={}", id);
        return service.findById(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/establishments/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class) @RequestBody Establishment establishment) {
        establishment.setId(id);
        log.info("updating establishment={}", establishment);
        service.update(id,establishment);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/establishments/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing establishment id={}", id);
        service.delete(id);
    }

    @JsonView(Views.Establishment.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/establishments", method = RequestMethod.GET)
    public Results<Establishment> getByParams(EstablishmentFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search establishment with filter={}", filter);
        Page<Establishment> page =  service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/establishments", api));
    }

    @JsonView(Views.Establishment.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/establishments/me", method = RequestMethod.GET)
    public Establishment getMe(Establishment establishment) {
        log.info("get establishment={}", establishment.documentNumber());
        return service.findById(establishment.getId());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/establishments/me", method = RequestMethod.PUT)
    public void updateMe(Establishment current,
                         @Validated(Update.class) @RequestBody Establishment establishment) {
        log.info("updating establishments={}", establishment.documentNumber());
        service.update(current.getId(), establishment);
    }

    @JsonView(Views.Service.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/establishments/me/services", method = RequestMethod.POST)
    public ResponseEntity<Service> createService(Establishment establishment,
                                                 @Validated(Create.class) @RequestBody Service service) {
        log.info("creating service={} for establishment={}", service, establishment.documentNumber());
        Service created = this.serviceService.createForEstablishment(service, establishment);
        return ResponseEntity
                .created(URI.create("/establishments/me/services/"+created.getId()))
                .body(created);

    }
    @JsonView(Views.Service.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/establishments/me/services/{id}", method = RequestMethod.GET)
    public Service getService(Establishment establishment, @PathVariable String id) {
        log.info("get bank account={} for establishment={}", id, establishment.documentNumber());
        return serviceService.findByIdForEstablishment(id, establishment);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/establishments/me/services/{id}", method = RequestMethod.PUT)
    public void updateService(Establishment establishment,
                              @PathVariable String id, @Validated(Update.class) @RequestBody Service service) {
        service.setId(id);
        log.info("updating service={} for establishment={}", service, establishment.documentNumber());
        this.serviceService.updateForEstablishment(id, establishment, service);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/establishments/me/services/{id}", method = RequestMethod.DELETE)
    public void removeService(Establishment establishment, @PathVariable String id) {
        log.info("removing service id={} for establishment={}", id, establishment.documentNumber());
        serviceService.deleteForEstablishment(id, establishment);
    }

    @JsonView(Views.Service.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/establishments/me/services", method = RequestMethod.GET)
    public Results<Service> getServiceByParams(Establishment establishment,
                                               ServiceFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("search Service by filter with filter={} for establishment={}",filter, establishment.documentNumber());
        Page<Service> page =  serviceService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/establishments/me/services", api));
    }
}
