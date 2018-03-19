package br.com.unopay.api.billing.creditcard.controller;

import br.com.unopay.api.billing.creditcard.model.PaymentRequest;
import br.com.unopay.api.billing.creditcard.model.Transaction;
import br.com.unopay.api.billing.creditcard.model.filter.TransactionFilter;
import br.com.unopay.api.billing.creditcard.service.TransactionService;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.validation.group.Create;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.bootcommons.jsoncollections.PageableResults;
import br.com.unopay.bootcommons.jsoncollections.Results;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
public class TransactionController {

    @Value("${unopay.api}")
    private String api;

    @Autowired
    private TransactionService service;

    @JsonView(Views.Billing.Detail.class)
    @ResponseStatus(CREATED)
    @RequestMapping(value = "/transactions", method = POST)
    public ResponseEntity<Transaction> create(@Validated(Create.class) @RequestBody PaymentRequest paymentRequest) {
        log.info("creating transaction={}", paymentRequest);
        Transaction created = service.create(paymentRequest);
        return created(URI.create("/transactions/"+created.getId())).body(created);
    }

    @JsonView(Views.Billing.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/transactions", method = RequestMethod.GET)
    public Results<Transaction> findTransactions(TransactionFilter filter, @Validated UnovationPageRequest pageable) {
        log.info("find transactions  with filter={}", filter);
        return getTransactionResults(filter, pageable);
    }

    @JsonView(Views.Billing.List.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/transactions", method = RequestMethod.GET, params = "orderId")
    public Results<Transaction> findTransactionsOnlyByOrderId(TransactionFilter filter,
                                                              @Validated UnovationPageRequest pageable) {
        log.info("find transactions  with filter={}", filter);
        return getTransactionResults(filter, pageable);
    }

    private Results<Transaction> getTransactionResults(TransactionFilter filter, @Validated UnovationPageRequest pageable) {
        Page<Transaction> page = service.findByFilter(filter, pageable);
        pageable.setTotal(page.getTotalElements());
        return PageableResults.create(pageable, page.getContent(), String.format("%s/transactions", api));
    }

}
