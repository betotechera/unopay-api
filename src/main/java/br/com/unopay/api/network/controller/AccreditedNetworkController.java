package br.com.unopay.api.network.controller;

import br.com.unopay.api.network.model.AccreditedNetwork;
import br.com.unopay.api.network.model.Branch;
import br.com.unopay.api.network.model.Establishment;
import br.com.unopay.api.network.model.filter.AccreditedNetworkFilter;
import br.com.unopay.api.network.model.filter.BranchFilter;
import br.com.unopay.api.network.model.filter.EstablishmentFilter;
import br.com.unopay.api.network.service.AccreditedNetworkService;
import br.com.unopay.api.network.service.BranchService;
import br.com.unopay.api.network.service.EstablishmentService;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Update;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.scheduling.model.Scheduling;
import br.com.unopay.api.scheduling.model.filter.SchedulingFilter;
import br.com.unopay.api.scheduling.service.SchedulingService;
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
public class AccreditedNetworkController {

    private AccreditedNetworkService service;
    private EstablishmentService establishmentService;
    private BranchService branchService;
    private SchedulingService schedulingService;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public AccreditedNetworkController(AccreditedNetworkService service,
                                       EstablishmentService establishmentService,
                                       BranchService branchService,
                                       SchedulingService schedulingService) {
        this.service = service;
        this.establishmentService = establishmentService;
        this.branchService = branchService;
        this.schedulingService = schedulingService;
    }

    @JsonView(Views.AccreditedNetwork.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_ACCREDITED_NETWORK')")
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
    @PreAuthorize("hasRole('ROLE_LIST_ACCREDITED_NETWORK')")
    @RequestMapping(value = "/accredited-networks/{id}", method = RequestMethod.GET)
    public AccreditedNetwork get(@PathVariable  String id) {
        log.info("get AccreditedNetwork={}", id);
        return service.getById(id);
    }

    @JsonView(Views.AccreditedNetwork.Detail.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_ACCREDITED_NETWORK')")
    @RequestMapping(value = "/accredited-networks/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id, @Validated(Update.class)
    @RequestBody AccreditedNetwork accreditedNetwork) {
        accreditedNetwork.setId(id);
        log.info("updating accreditedNetwork {}", accreditedNetwork);
        service.update(id,accreditedNetwork);
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_ACCREDITED_NETWORK')")
    @RequestMapping(value = "/accredited-networks/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing accreditedNetwork id={}", id);
        service.delete(id);
    }

    @JsonView(Views.AccreditedNetwork.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("#oauth2.isClient()")
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
    public AccreditedNetwork getMe(AccreditedNetwork accreditedNetwork) {
        log.info("get AccreditedNetwork={}", accreditedNetwork.documentNumber());
        return service.getById(accreditedNetwork.getId());
    }

    @JsonView(Views.AccreditedNetwork.Detail.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/accredited-networks/me", method = RequestMethod.PUT)
    public void updateMe(AccreditedNetwork current, @Validated(Update.class)
    @RequestBody AccreditedNetwork accreditedNetwork) {
        log.info("updating accreditedNetwork={}", accreditedNetwork);
        service.update(current.getId(),accreditedNetwork);
    }

    @JsonView(Views.Establishment.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/accredited-networks/me/establishments/{id}", method = RequestMethod.GET)
    public Establishment getEstablishment(AccreditedNetwork accreditedNetwork, @PathVariable  String id) {
        log.info("get establishment={} for network={}", id, accreditedNetwork.documentNumber());
        return establishmentService.findByIdAndNetworks(id, accreditedNetwork);
    }

    @JsonView(Views.Establishment.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/accredited-networks/me/establishments", method = RequestMethod.GET)
    public Results<Establishment> getEstablishmentByParams(AccreditedNetwork accreditedNetwork,
                                                           EstablishmentFilter filter,
                                                           @Validated UnovationPageRequest pageable) {
        log.info("search establishment with filter={} for network={}", filter, accreditedNetwork.documentNumber());
        Page<Establishment> page =  establishmentService.findByFilterForNetwork(accreditedNetwork,filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/accredited-networks/me/establishments", api));
    }

    @JsonView(Views.Branch.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/accredited-networks/me/establishments", method = RequestMethod.POST)
    public ResponseEntity<Establishment> createEstablishment(@Validated(Create.class) @RequestBody Establishment establishment,
                                                             AccreditedNetwork accreditedNetwork) {
        log.info("create an establishment for network={}", accreditedNetwork.documentNumber());
        Establishment created = establishmentService.create(establishment, accreditedNetwork);
        return ResponseEntity
                .created(URI.create("/accredited-networks/me/establishments/"+created.getId()))
                .body(created);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/accredited-networks/me/establishments/{id}", method = RequestMethod.PUT)
    public void updateEstablishment(@PathVariable  String id, @Validated(Update.class) @RequestBody Establishment establishment,
                                    AccreditedNetwork accreditedNetwork) {
        establishment.setId(id);
        log.info("update an establishment={} for network={}", id, accreditedNetwork.documentNumber());
        establishmentService.update(id,establishment, accreditedNetwork);
    }


    @JsonView(Views.Branch.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/accredited-networks/me/establishments/branches", method = RequestMethod.POST)
    public ResponseEntity<Branch> createBranch(@Validated(Create.class) @RequestBody Branch branch, AccreditedNetwork accreditedNetwork) {
        log.info("create a branch establishment for network={}", accreditedNetwork.documentNumber());
        Branch created = branchService.create(branch, accreditedNetwork);
        return ResponseEntity
                .created(URI.create("/accredited-networks/me/establishments/branches/"+created.getId()))
                .body(created);

    }

    @JsonView(Views.Branch.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/accredited-networks/me/establishments/branches/{id}", method = RequestMethod.GET)
    public Branch getBranch(@PathVariable  String id, AccreditedNetwork accreditedNetwork) {
        log.info("get a branch establishment={} for network={}",id, accreditedNetwork.documentNumber());
        return branchService.findById(id, accreditedNetwork);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/accredited-networks/me/establishments/branches/{id}", method = RequestMethod.PUT)
    public void updateBranch(@PathVariable  String id, @Validated(Update.class) @RequestBody Branch branch, AccreditedNetwork accreditedNetwork) {
        branch.setId(id);
        log.info("update a branch establishment={} for network={}", id, accreditedNetwork.documentNumber());
        branchService.update(id,branch, accreditedNetwork);
    }

    @JsonView(Views.Branch.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/accredited-networks/me/establishments/branches", method = RequestMethod.GET)
    public Results<Branch> getBranchByParams(BranchFilter filter, @Validated UnovationPageRequest pageable, AccreditedNetwork accreditedNetwork) {
        log.info("search branch establishment with filter={} for network={}", filter, accreditedNetwork.documentNumber());
        Page<Branch> page =  branchService.findByFilter(filter, accreditedNetwork, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/accredited-networks/me/establishments/branches", api));
    }

    @JsonView(Views.Branch.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/accredited-networks/me/establishments/schedules", method = RequestMethod.POST)
    public ResponseEntity<Scheduling> createScheduling(@Validated(Create.class) @RequestBody Scheduling scheduling,
                                                       AccreditedNetwork accreditedNetwork, UserDetail currentUser) {
        scheduling.setUser(currentUser);
        log.info("create a scheduling establishment for network={}", accreditedNetwork.documentNumber());
        Scheduling created = schedulingService.create(scheduling, accreditedNetwork);
        return ResponseEntity
                .created(URI.create("/accredited-networks/me/establishments/schedules/"+created.getId()))
                .body(created);

    }

    @JsonView(Views.Scheduling.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/accredited-networks/me/establishments/schedules/{id}", method = RequestMethod.GET)
    public Scheduling getScheduling(@PathVariable  String id, AccreditedNetwork accreditedNetwork) {
        log.info("get a scheduling establishment={} for network={}",id, accreditedNetwork.documentNumber());
        return schedulingService.findById(id, accreditedNetwork);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/accredited-networks/me/establishments/schedules/{id}", method = RequestMethod.PUT)
    public void updateScheduling(@PathVariable  String id, @Validated(Update.class) @RequestBody Scheduling scheduling,
                                 AccreditedNetwork accreditedNetwork, UserDetail currentUser) {
        scheduling.setUser(currentUser);
        scheduling.setId(id);
        log.info("update a scheduling establishment={} for network={}", id, accreditedNetwork.documentNumber());
        schedulingService.update(id, scheduling, accreditedNetwork);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/accredited-networks/me/establishments/schedules/{id}", method = RequestMethod.DELETE)
    public void cancelScheduling(@PathVariable  String id, AccreditedNetwork accreditedNetwork) {
        log.info("cancelling a scheduling establishment={} for network={}", id, accreditedNetwork.documentNumber());
        schedulingService.cancelById(id, accreditedNetwork);
    }

    @JsonView(Views.Scheduling.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/accredited-networks/me/establishments/schedules", method = RequestMethod.GET)
    public Results<Scheduling> getSchedulingByParams(SchedulingFilter filter, @Validated UnovationPageRequest pageable, AccreditedNetwork accreditedNetwork) {
        log.info("search scheduling establishment with filter={} for network={}", filter, accreditedNetwork.documentNumber());
        Page<Scheduling> page =  schedulingService.findAll(filter, accreditedNetwork, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/accredited-networks/me/establishments/schedules", api));
    }

}
