package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Institution;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.filter.InstitutionFilter;
import br.com.unopay.api.bacen.model.filter.PaymentRuleGroupFilter;
import br.com.unopay.api.bacen.service.InstitutionService;
import br.com.unopay.api.bacen.service.PaymentRuleGroupService;
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
public class InstitutionController {

    private InstitutionService service;
    private PaymentRuleGroupService paymentRuleGroupService;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public InstitutionController(InstitutionService service,
                                 PaymentRuleGroupService paymentRuleGroupService) {
        this.service = service;
        this.paymentRuleGroupService = paymentRuleGroupService;
    }

    @JsonView({Views.Institution.Detail.class})
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_INSTITUTION')")
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
    @PreAuthorize("hasRole('ROLE_LIST_INSTITUTION')")
    @RequestMapping(value = "/institutions/{id}", method = RequestMethod.GET)
    public Institution get(@PathVariable  String id) {
        log.info("get Institution={}", id);
        return service.getById(id);
    }

    @JsonView({Views.Institution.Detail.class})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_INSTITUTION')")
    @RequestMapping(value = "/institutions/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable String id, @Validated(Update.class) @RequestBody Institution institution) {
        institution.setId(id);
        log.info("updating institution {}", institution);
        service.update(id,institution);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_INSTITUTION')")
    @RequestMapping(value = "/institutions/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing institution id={}", id);
        service.delete(id);
    }

    @JsonView(Views.Institution.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
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
    public Institution getMe(Institution institution) {
        log.info("get Institution={}", institution.documentNumber());
        return service.getById(institution.getId());
    }

    @JsonView({Views.Institution.Detail.class})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/institutions/me", method = RequestMethod.PUT)
    public void updateMe(Institution current, @Validated(Update.class) @RequestBody Institution institution) {
        log.info("updating institution={} for institution={}", institution, current.documentNumber());
        service.update(current.getId(),institution);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/institutions/me/payment-rule-groups/{id}", method = RequestMethod.PUT)
    public void updatePaymentRuleGroup(Institution institution, @PathVariable  String id,
                       @Validated(Update.class) @RequestBody PaymentRuleGroup paymentRuleGroup) {
        paymentRuleGroup.setId(id);
        log.info("updating paymentRuleGroups {}", paymentRuleGroup);
        paymentRuleGroupService.updateForInstitution(id, institution, paymentRuleGroup);
    }

    @JsonView(Views.PaymentRuleGroup.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/institutions/me/payment-rule-groups/{id}", method = RequestMethod.GET)
    public PaymentRuleGroup getPaymentRuleGroup(Institution institution, @PathVariable  String id) {
        log.info("get paymentRuleGroups={} for institution={}", id, institution.documentNumber());
        return paymentRuleGroupService.getForInstitutionById(id, institution);
    }

    @JsonView(Views.PaymentRuleGroup.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/institutions/me/payment-rule-groups", method = RequestMethod.POST)
    public ResponseEntity<PaymentRuleGroup> createPaymentRuleGroup(Institution institution, @Validated(Create.class)
                                                   @RequestBody PaymentRuleGroup paymentRuleGroup) {
        log.info("creating PaymentRuleGroup={} for institution={}", paymentRuleGroup, institution.documentNumber());
        PaymentRuleGroup created = paymentRuleGroupService.createForInstitution(paymentRuleGroup, institution);
        return ResponseEntity
                .created(URI.create("/payment-rule-groups/"+created.getId()))
                .body(created);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/institutions/me/payment-rule-groups/{id}", method = RequestMethod.DELETE)
    public void removePaymentRuleGroup(@PathVariable  String id, Institution institution) {
        log.info("removing payment rule groups={} for institution={}", id, institution.documentNumber());
        paymentRuleGroupService.deleteForInstitution(id, institution);
    }

    @JsonView(Views.PaymentRuleGroup.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/institutions/me/payment-rule-groups", method = RequestMethod.GET)
    public Results<PaymentRuleGroup> getPaymentRuleGroupByParams(Institution institution, PaymentRuleGroupFilter filter,
                                                 @Validated UnovationPageRequest pageable) {
        log.info("search PaymentRuleGroup by filter with filter={}", filter);
        Page<PaymentRuleGroup> page =  paymentRuleGroupService.findByFilterForInstitution(institution,filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(),
                String.format("%s/institutions/me/payment-rule-groups", api));
    }

}
