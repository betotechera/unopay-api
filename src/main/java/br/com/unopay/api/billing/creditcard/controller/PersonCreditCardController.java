package br.com.unopay.api.billing.creditcard.controller;

import br.com.unopay.api.billing.creditcard.model.PersonCreditCard;
import br.com.unopay.api.billing.creditcard.model.filter.PersonCreditCardFilter;
import br.com.unopay.api.billing.creditcard.service.PersonCreditCardService;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Timed(prefix = "api")
public class PersonCreditCardController {

    @Value("${unopay.api}")
    private String api;

    private PersonCreditCardService personCreditCardService;

    @Autowired
    public PersonCreditCardController(PersonCreditCardService personCreditCardService) {
        this.personCreditCardService = personCreditCardService;
    }

    @JsonView(Views.PersonCreditCard.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_USER_CREDIT_CARD')")
    @RequestMapping(value = "/credit-cards", method = RequestMethod.GET)
    public Results<br.com.unopay.api.billing.creditcard.model.PersonCreditCard> getByParams(PersonCreditCardFilter personCreditCardFilter,
                                                                                            @Validated UnovationPageRequest pageable) {
        log.info("search user credit card with filter={}", personCreditCardFilter);
        Page<br.com.unopay.api.billing.creditcard.model.PersonCreditCard> page = personCreditCardService.findByFilter(personCreditCardFilter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/credit-cards", api));
    }

    @JsonView(Views.PersonCreditCard.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_USER_CREDIT_CARD')")
    @RequestMapping(value = "/credit-cards/{id}", method = RequestMethod.GET)
    public PersonCreditCard get(@PathVariable String id) {
        log.info("get user credit card={}", id);
        return personCreditCardService.findById(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_USER_CREDIT_CARD')")
    @RequestMapping(value = "/credit-cards/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing user credit card id={}", id);
        personCreditCardService.delete(id);
    }

}
