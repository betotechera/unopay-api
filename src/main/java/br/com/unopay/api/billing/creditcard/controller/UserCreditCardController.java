package br.com.unopay.api.billing.creditcard.controller;

import br.com.unopay.api.billing.creditcard.model.UserCreditCard;
import br.com.unopay.api.billing.creditcard.model.filter.UserCreditCardFilter;
import br.com.unopay.api.billing.creditcard.service.UserCreditCardService;
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
public class UserCreditCardController {

    @Value("${unopay.api}")
    private String api;

    private UserCreditCardService userCreditCardService;

    @Autowired
    public UserCreditCardController(UserCreditCardService userCreditCardService) {
        this.userCreditCardService = userCreditCardService;
    }

    @JsonView(Views.UserCreditCard.List.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_USER_CREDIT_CARD')")
    @RequestMapping(value = "/credit-cards", method = RequestMethod.GET)
    public Results<UserCreditCard> getByParams(UserCreditCardFilter userCreditCardFilter,
                                               @Validated UnovationPageRequest pageable) {
        log.info("search user credit card with filter={}", userCreditCardFilter);
        Page<UserCreditCard> page = userCreditCardService.findByFilter(userCreditCardFilter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/credit-cards", api));
    }

    @JsonView(Views.UserCreditCard.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_LIST_USER_CREDIT_CARD')")
    @RequestMapping(value = "/credit-cards/{id}", method = RequestMethod.GET)
    public UserCreditCard get(@PathVariable String id) {
        log.info("get user credit card={}", id);
        return userCreditCardService.findById(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGE_USER_CREDIT_CARD')")
    @RequestMapping(value = "/credit-cards/{id}", method = RequestMethod.DELETE)
    public void remove(@PathVariable  String id) {
        log.info("removing user credit card id={}", id);
        userCreditCardService.delete(id);
    }

}
