package br.com.unopay.api.bacen.controller;

import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.PaymentRuleGroup;
import br.com.unopay.api.bacen.model.filter.PaymentRuleGroupFilter;
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
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Timed(prefix = "api")
public class PaymentRuleGroupController {

    private PaymentRuleGroupService paymentRuleGroupService;

    @Value("${unopay.api}")
    private String api;

    @Autowired
    public PaymentRuleGroupController(PaymentRuleGroupService paymentRuleGroupService) {
        this.paymentRuleGroupService = paymentRuleGroupService;
     }

    @JsonView(Views.PaymentRuleGroup.Detail.class)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_MANAGE_PAYMENT_RULE_GROUP')")
    @RequestMapping(value = "/payment-rule-groups", method = RequestMethod.POST)
    public ResponseEntity<PaymentRuleGroup> create(@Validated(Create.class)
                                                       @RequestBody PaymentRuleGroup paymentRuleGroup) {
        log.info("creating PaymentRuleGroup {}", paymentRuleGroup);
        PaymentRuleGroup created = paymentRuleGroupService.create(paymentRuleGroup);
        return ResponseEntity
                .created(URI.create("/payment-rule-groups/"+created.getId()))
                .body(created);

    }

    @JsonView(Views.PaymentRuleGroup.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_PAYMENT_RULE_GROUP')")
    @RequestMapping(value = "/payment-rule-groups/{id}", method = RequestMethod.GET)
    public PaymentRuleGroup get(@PathVariable  String id) {
        log.info("get paymentRuleGroups={}", id);
        return paymentRuleGroupService.getById(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_PAYMENT_RULE_GROUP')")
    @RequestMapping(value = "/payment-rule-groups/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable  String id,
                       @Validated(Update.class) @RequestBody PaymentRuleGroup paymentRuleGroup) {
        paymentRuleGroup.setId(id);
        log.info("updating paymentRuleGroups {}", paymentRuleGroup);
        paymentRuleGroupService.update(id,paymentRuleGroup);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_PAYMENT_RULE_GROUP')")
    @RequestMapping(value = "/payment-rule-groups/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing payment rule groups id={}", id);
        paymentRuleGroupService.delete(id);
    }

    @JsonView(Views.PaymentRuleGroup.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/payment-rule-groups", method = RequestMethod.GET)
    public Results<PaymentRuleGroup> getByParams(PaymentRuleGroupFilter filter,
                                                 @Validated UnovationPageRequest pageable) {
        log.info("search PaymentRuleGroup by filter with filter={}", filter);
        Page<PaymentRuleGroup> page =  paymentRuleGroupService.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/payment-rule-groups", api));
    }

    @JsonView(Views.PaymentRuleGroup.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/payment-rule-groups/menu")
    List<PaymentRuleGroup> listForMenu() {
        return paymentRuleGroupService.listForMenu();
    }

}
